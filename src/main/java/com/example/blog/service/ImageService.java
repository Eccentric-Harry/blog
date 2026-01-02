package com.example.blog.service;

import com.example.blog.dto.ImageResponse;
import com.example.blog.dto.ImageType;
import com.example.blog.entity.Image;
import com.example.blog.exception.InvalidFileException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service abstraction for image storage and metadata management.
 * Implementations may store binary data in object storage (MinIO, S3, etc.)
 * and persist metadata to a database.
 */
public interface ImageService {

    /**
     * Uploads a multipart file to object storage and persists metadata.
     *
     * @param file multipart file from request
     * @param imageType the type of image (COVER or CONTENT)
     * @return an ImageResponse containing metadata and a public or presigned URL
     * @throws InvalidFileException when the file is invalid (empty, wrong type)
     * @throws StorageException when upload to storage fails
     */
    ImageResponse upload(MultipartFile file, ImageType imageType) throws InvalidFileException, StorageException;

    /**
     * Uploads a multipart file to object storage (defaults to CONTENT type).
     *
     * @param file multipart file from request
     * @return an ImageResponse containing metadata and a public or presigned URL
     * @throws InvalidFileException when the file is invalid (empty, wrong type)
     * @throws StorageException when upload to storage fails
     */
    default ImageResponse upload(MultipartFile file) throws InvalidFileException, StorageException {
        return upload(file, ImageType.CONTENT);
    }

    /**
     * Generates a presigned GET URL for the given object key.
     *
     * @param objectKey object key in storage (e.g. "images/<uuid>.jpg")
     * @param expirySeconds expiry in seconds for the presigned URL
     * @return presigned URL to download the object
     * @throws ResourceNotFoundException if the objectKey is not found
     * @throws StorageException for other storage-related errors
     */
    String getPresignedUrl(String objectKey, int expirySeconds)
            throws ResourceNotFoundException, StorageException;

    /**
     * Generates a presigned PUT/POST URL for direct client upload (optional).
     *
     * @param objectKey desired object key
     * @param expirySeconds expiry in seconds for the presigned upload URL
     * @return presigned URL the client can use to upload directly to storage
     * @throws StorageException when signing fails
     */
    String getPresignedUploadUrl(String objectKey, int expirySeconds) throws StorageException;

    /**
     * Delete an object from storage and remove its metadata record (if present).
     *
     * @param objectKey object key in storage
     * @throws ResourceNotFoundException if metadata or object is not found
     * @throws StorageException on storage deletion errors
     */
    void deleteByObjectKey(String objectKey) throws ResourceNotFoundException, StorageException;

    /**
     * Persist a metadata-only Image record (useful if you want separate creation).
     *
     * @param image entity to persist
     * @return persisted Image (with id)
     */
    Image saveMetadata(Image image);

    /**
     * Find persisted image metadata by database id.
     *
     * @param id database id
     * @return ImageResponse with metadata and (optionally) a URL
     * @throws ResourceNotFoundException if not present
     */
    ImageResponse getById(Long id) throws ResourceNotFoundException;

    /**
     * Try to find image metadata by object key.
     *
     * @param objectKey storage object key
     * @return optional ImageResponse
     */
    Optional<ImageResponse> findByObjectKey(String objectKey);

    /**
     * List all stored image metadata entries (pageable overloads can be added if needed).
     *
     * @return list of ImageResponse
     */
    List<ImageResponse> listAll();
}
