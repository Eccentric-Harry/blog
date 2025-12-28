package com.example.blog.controller;

import com.example.blog.entity.Image;
import com.example.blog.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts/{postId}/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ImageDto> upload(@PathVariable Long postId, @RequestPart("file") MultipartFile file) throws IOException {
        Image saved = imageService.uploadForPost(postId, file);
        ImageDto dto = new ImageDto(saved.getId(), saved.getUrl(), saved.getOriginalName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Example endpoint to get presigned PUT url for direct uploads
    @PostMapping("/presign")
    public ResponseEntity<String> presign(@RequestParam String filename, @RequestParam String contentType) {
        String presigned = imageService.createPresignedUploadKey(filename, contentType);
        return ResponseEntity.ok(presigned);
    }
}
