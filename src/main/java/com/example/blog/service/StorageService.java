package com.example.blog.service;

import com.example.blog.dto.ImageType;
import com.example.blog.dto.UploadResult;
import com.example.blog.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * Service abstraction for object storage operations.
 * Implementations may use ImageKit, S3, or other storage backends.
 */
public interface StorageService {

    /**
     * Uploads a multipart file to object storage with specified image type.
     *
     * @param file multipart file from request
     * @param imageType the type of image (determines storage folder)
     * @return UploadResult containing the key, fileId, and URL
     * @throws StorageException when upload fails
     */
    UploadResult upload(MultipartFile file, ImageType imageType) throws StorageException;

    /**
     * Uploads a multipart file to object storage (defaults to CONTENT type).
     *
     * @param file multipart file from request
     * @return UploadResult containing the key, fileId, and URL
     * @throws StorageException when upload fails
     */
    default UploadResult upload(MultipartFile file) throws StorageException {
        return upload(file, ImageType.CONTENT);
    }

    /**
     * Generates a presigned PUT URL for direct client upload.
     *
     * @param key         desired object key
     * @param contentType MIME type for the upload
     * @param validFor    duration for which the URL is valid
     * @return presigned URL the client can use to upload directly to storage
     * @throws StorageException when signing fails
     */
    String presignPutUrl(String key, String contentType, Duration validFor) throws StorageException;

    /**
     * Generates a presigned GET URL for downloading an object.
     *
     * @param key      object key in storage
     * @param validFor duration for which the URL is valid
     * @return presigned URL to download the object
     * @throws StorageException when signing fails
     */
    String presignGetUrl(String key, Duration validFor) throws StorageException;

    /**
     * Returns a public/direct URL for the object (if bucket is publicly accessible).
     *
     * @param key object key in storage
     * @return public URL to access the object
     */
    String getObjectUrl(String key);

    /**
     * Deletes an object from storage.
     *
     * @param key object key in storage
     * @param fileId storage provider's file ID (optional, used by some providers like ImageKit)
     * @throws StorageException when deletion fails
     */
    void delete(String key, String fileId) throws StorageException;

    /**
     * Checks if an object exists in storage.
     *
     * @param key object key to check
     * @return true if the object exists, false otherwise
     * @throws StorageException when the check fails
     */
    boolean exists(String key) throws StorageException;
}

