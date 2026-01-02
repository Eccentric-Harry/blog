package com.example.blog.service;

import com.example.blog.dto.ImageResponse;
import com.example.blog.dto.ImageType;
import com.example.blog.dto.UploadResult;
import com.example.blog.entity.Image;
import com.example.blog.exception.InvalidFileException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.exception.StorageException;
import com.example.blog.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private final StorageService storageService;
    private final ImageRepository imageRepository;

    public ImageServiceImpl(StorageService storageService, ImageRepository imageRepository) {
        this.storageService = storageService;
        this.imageRepository = imageRepository;
    }

    @Override
    public ImageResponse upload(MultipartFile file, ImageType imageType) throws InvalidFileException, StorageException {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        // Upload to storage with the specified image type (determines folder)
        UploadResult uploadResult = storageService.upload(file, imageType);

        // Save metadata to database
        Image image = new Image();
        image.setKey(uploadResult.getKey());
        image.setFileId(uploadResult.getFileId());
        image.setOriginalName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setUrl(uploadResult.getUrl());
        Image saved = imageRepository.save(image);

        return toResponse(saved);
    }

    @Override
    public String getPresignedUrl(String objectKey, int expirySeconds) throws ResourceNotFoundException, StorageException {
        if (!storageService.exists(objectKey)) {
            throw new ResourceNotFoundException("Object not found: " + objectKey);
        }
        return storageService.presignGetUrl(objectKey, Duration.ofSeconds(expirySeconds));
    }

    @Override
    public String getPresignedUploadUrl(String objectKey, int expirySeconds) throws StorageException {
        return storageService.presignPutUrl(objectKey, "application/octet-stream", Duration.ofSeconds(expirySeconds));
    }

    @Override
    public void deleteByObjectKey(String objectKey) throws ResourceNotFoundException, StorageException {
        Image image = imageRepository.findByKey(objectKey)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with key: " + objectKey));

        storageService.delete(objectKey, image.getFileId());
        imageRepository.delete(image);
    }

    @Override
    public Image saveMetadata(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public ImageResponse getById(Long id) throws ResourceNotFoundException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));

        // ImageKit URLs are permanent CDN URLs, no need to refresh
        return toResponse(image);
    }

    @Override
    public Optional<ImageResponse> findByObjectKey(String objectKey) {
        return imageRepository.findByKey(objectKey)
                .map(this::toResponse);
    }

    @Override
    public List<ImageResponse> listAll() {
        return imageRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ImageResponse toResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .key(image.getKey())
                .originalName(image.getOriginalName())
                .url(image.getUrl())
                .uploadedAt(image.getUploadedAt())
                .postId(image.getPost() != null ? image.getPost().getId() : null)
                .build();
    }
}

