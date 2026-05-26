package id.go.govedu.assist.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.go.govedu.assist.annotation.Idempotent;
import id.go.govedu.assist.exception.ConcurrentRequestException;
import id.go.govedu.assist.exception.IdempotencyMismatchException;
import id.go.govedu.assist.exception.MissingIdempotencyKeyException;
import id.go.govedu.assist.model.IdempotencyRecord;
import id.go.govedu.assist.repository.IdempotencyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(Idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new MissingIdempotencyKeyException();
        }

        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();
        String currentPayloadHash = generateHash(extractBody(joinPoint.getArgs()));
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        // Check if key already exists with pessimistic locking
        Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findByIdAndLock(idempotencyKey);

        if (existingRecord.isPresent()) {
            IdempotencyRecord record = existingRecord.get();

            // Check if hash matches
            if (!record.getRequestHash().equals(currentPayloadHash)) {
                throw new IdempotencyMismatchException();
            }

            // If still processing (responseBody null), reject with 409
            if (record.getResponseBody() == null) {
                throw new ConcurrentRequestException();
            }

            // Return cached response
            log.info("Returning cached response for idempotency key: {}", idempotencyKey);
            return buildResponseEntity(record.getResponseStatusCode(), record.getResponseBody());
        }

        // Create new record with processing status
        IdempotencyRecord newRecord = new IdempotencyRecord(
                idempotencyKey,
                requestPath,
                requestMethod,
                currentPayloadHash,
                expiresAt
        );
        idempotencyRepository.save(newRecord);

        try {
            // Execute controller
            Object result = joinPoint.proceed();

            // Update record with response
            updateRecordWithResponse(newRecord, result);

            return result;
        } catch (Exception e) {
            // Delete key on failure so client can retry
            idempotencyRepository.delete(newRecord);
            throw e;
        }
    }

    private String extractBody(Object[] args) {
        try {
            if (args.length > 0) {
                return objectMapper.writeValueAsString(args[0]);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SHA-256 hash", e);
        }
    }

    private void updateRecordWithResponse(IdempotencyRecord record, Object result) {
        try {
            String responseBody = objectMapper.writeValueAsString(result);
            record.setResponseBody(responseBody);
            record.setResponseStatusCode(200);
            idempotencyRepository.save(record);
        } catch (Exception e) {
            log.error("Failed to update idempotency record", e);
        }
    }

    private Object buildResponseEntity(Integer statusCode, String responseBody) {
        try {
            return objectMapper.readValue(responseBody, Object.class);
        } catch (Exception e) {
            log.error("Failed to deserialize cached response", e);
            throw new RuntimeException("Failed to return cached response");
        }
    }
}
