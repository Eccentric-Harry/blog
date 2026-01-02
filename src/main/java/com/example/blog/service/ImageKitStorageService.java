package com.example.blog.service;

import com.example.blog.dto.ImageType;
import com.example.blog.dto.UploadResult;
import com.example.blog.exception.StorageException;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ImageKit.io implementation of {@link StorageService}.
 * Uses ImageKit SDK for image storage and delivery.
 *
 * Folder structure:
 * - /blogs_cover_images - for blog post cover/thumbnail images
 * - /blog_post_images - for inline content images within posts
 */
@Service
public class ImageKitStorageService implements StorageService {

    private final ImageKit imageKit;

    public ImageKitStorageService(ImageKit imageKit) {
        this.imageKit = imageKit;
    }

    @Override
    public UploadResult upload(MultipartFile file, ImageType imageType) throws StorageException {
        String ext = "";
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int dot = original.lastIndexOf('.');
        if (dot > -1) {
            ext = original.substring(dot);
        }

        String fileName = UUID.randomUUID() + ext;

        try {
            // Convert file to base64
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());

            // Create FileCreateRequest with base64 data and filename
            FileCreateRequest fileCreateRequest = new FileCreateRequest(base64, fileName);

            // Set folder based on image type
            fileCreateRequest.setFolder(imageType.getFolder());
            fileCreateRequest.setUseUniqueFileName(false);

            // Upload using SDK - throws exceptions on failure
            Result result = imageKit.upload(fileCreateRequest);

            if (result == null || result.getFileId() == null) {
                throw new StorageException("Failed to upload file to ImageKit: no fileId returned");
            }

            // Return UploadResult with key, fileId, and URL
            return UploadResult.builder()
                    .key(result.getFilePath())
                    .fileId(result.getFileId())
                    .url(result.getUrl())
                    .build();
        } catch (IOException e) {
            throw new StorageException("Failed to read file: " + e.getMessage(), e);
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("ImageKit error during upload: " + e.getMessage(), e);
        }
    }

    @Override
    public String presignPutUrl(String key, String contentType, Duration validFor) throws StorageException {
        // ImageKit doesn't use presigned PUT URLs in the same way as S3
        // Direct uploads are handled through the SDK
        throw new UnsupportedOperationException("ImageKit uses direct upload through SDK, not presigned URLs");
    }

    @Override
    public String presignGetUrl(String key, Duration validFor) throws StorageException {
        // ImageKit URLs don't expire by default - they're permanent CDN URLs
        // If you need signed URLs for private images, you can use the SDK's signed URL feature
        return getObjectUrl(key);
    }

    @Override
    public String getObjectUrl(String key) {
        // ImageKit provides permanent CDN URLs
        // Use the SDK to generate URL
        Map<String, Object> options = new HashMap<>();
        options.put("path", key);
        return imageKit.getUrl(options);
    }

    @Override
    public void delete(String key, String fileId) throws StorageException {
        if (fileId == null || fileId.isBlank()) {
            // Cannot delete without fileId in ImageKit
            throw new StorageException("Cannot delete from ImageKit without fileId");
        }

        try {
            // deleteFile throws exception on failure, returns Result on success
            imageKit.deleteFile(fileId);
            // If we get here without exception, deletion was successful
        } catch (Exception e) {
            throw new StorageException("Failed to delete file from ImageKit: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String key) throws StorageException {
        // ImageKit doesn't have a direct "exists" check
        // We return true assuming the key was stored in our database
        // The actual file existence is managed through our Image entity
        return true;
    }
}
