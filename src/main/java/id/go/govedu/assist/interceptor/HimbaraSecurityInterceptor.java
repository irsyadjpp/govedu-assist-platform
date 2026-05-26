package id.go.govedu.assist.interceptor;

import id.go.govedu.assist.config.BankH2HConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@RequiredArgsConstructor
public class HimbaraSecurityInterceptor implements ClientHttpRequestInterceptor {

    private final BankH2HConfig bankConfig;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws java.io.IOException {
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String bodyHash = generateSHA256Hash(body);
        String stringToSign = request.getMethod().name() + ":" + request.getURI().getPath() + ":" + bodyHash + ":" + timestamp;

        String signature = generateHMACSHA256Signature(stringToSign, bankConfig.getClientSecret());

        request.getHeaders().add("X-TIMESTAMP", timestamp);
        request.getHeaders().add("X-SIGNATURE", signature);

        return execution.execute(request, body);
    }

    private String generateSHA256Hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SHA-256 hash", e);
        }
    }

    private String generateHMACSHA256Signature(String data, String secret) {
        try {
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }
}
