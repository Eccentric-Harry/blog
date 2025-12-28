package com.example.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;

    public StorageService(S3Client s3, S3Presigner presigner,
                          @org.springframework.beans.factory.annotation.Value("${storage.bucket}") String bucket) {
        this.s3 = s3;
        this.presigner = presigner;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file) throws IOException {
        String ext = "";
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int dot = original.lastIndexOf('.');
        if (dot > -1) ext = original.substring(dot);

        String key = UUID.randomUUID().toString() + ext;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    public String presignPutUrl(String key, String contentType, Duration validFor) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(req)
                .signatureDuration(validFor)
                .build();

        return presigner.presignPutObject(presignRequest).url().toString();
    }

    public String getObjectUrl(String key) {
        // For MinIO dev, you can expose public read; or better, presign GET if private.
        // Example public path: http://localhost:9000/<bucket>/<key>
        return String.format("%s/%s/%s", "http://localhost:9000", bucket, key);
    }
}
