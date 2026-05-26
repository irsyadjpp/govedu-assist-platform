package id.go.govedu.assist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${s3.bucket-name}")
    private String bucketName;

    public String uploadFile(String folder, File file) {
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
        log.info("File uploaded to S3: {}", fileUrl);

        return fileUrl;
    }

    public String generatePresignedUrl(String objectKey) {
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(builder -> builder
                .getObjectRequest(r -> r
                        .bucket(bucketName)
                        .key(objectKey))
                .signatureDuration(java.time.Duration.ofHours(24))
                .build());
        return presignedRequest.url().toString();
    }
}
