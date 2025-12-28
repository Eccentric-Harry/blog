package com.example.blog.service;

import com.example.blog.entity.BlogPost;
import com.example.blog.entity.Image;
import com.example.blog.repository.BlogPostRepository;
import com.example.blog.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService {
    private final StorageService storage;
    private final ImageRepository imageRepo;
    private final BlogPostRepository postRepo;

    public ImageService(StorageService storage, ImageRepository imageRepo, BlogPostRepository postRepo) {
        this.storage = storage;
        this.imageRepo = imageRepo;
        this.postRepo = postRepo;
    }

    @Transactional
    public Image uploadForPost(Long postId, MultipartFile file) throws IOException {
        // Basic validation
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file");
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) throw new IllegalArgumentException("File must be an image");

        BlogPost post = postRepo.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        String key = storage.upload(file);
        Image image = new Image();
        image.setKey(key);
        image.setOriginalName(file.getOriginalFilename());
        image.setContentType(ct);
        image.setSize(file.getSize());
        image.setUrl(storage.getObjectUrl(key));
        image.setPost(post);

        return imageRepo.save(image);
    }

    // Optionally: method to get presigned PUT URL (for direct client upload)
    public String createPresignedUploadKey(String originalName, String contentType) {
        String ext = "";
        int dot = originalName == null ? -1 : originalName.lastIndexOf('.');
        if (dot > -1) ext = originalName.substring(dot);
        String key = java.util.UUID.randomUUID().toString() + ext;
        // presign for 10 minutes
        return storage.presignPutUrl(key, contentType, java.time.Duration.ofMinutes(10));
    }
}

