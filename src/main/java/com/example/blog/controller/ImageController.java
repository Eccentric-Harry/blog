package com.example.blog.controller;

import com.example.blog.dto.ImageResponse;
import com.example.blog.dto.ImageType;
import com.example.blog.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Upload an image with specified type.
     *
     * @param file the image file to upload
     * @param type the image type: COVER (for blog cover images) or CONTENT (for inline post images)
     *             Defaults to CONTENT if not specified.
     * @return ImageResponse with the uploaded image details
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "CONTENT") ImageType type) {
        ImageResponse response = imageService.upload(file, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Upload a cover image for a blog post.
     * Convenience endpoint that always stores in blogs_cover_images folder.
     */
    @PostMapping(value = "/cover", consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> uploadCover(@RequestPart("file") MultipartFile file) {
        ImageResponse response = imageService.upload(file, ImageType.COVER);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Upload a content/inline image for a blog post.
     * Convenience endpoint that always stores in blog_post_images folder.
     */
    @PostMapping(value = "/content", consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> uploadContent(@RequestPart("file") MultipartFile file) {
        ImageResponse response = imageService.upload(file, ImageType.CONTENT);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> getById(@PathVariable Long id) {
        ImageResponse response = imageService.getById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{objectKey}")
    public ResponseEntity<Void> delete(@PathVariable String objectKey) {
        imageService.deleteByObjectKey(objectKey);
        return ResponseEntity.noContent().build();
    }
}
