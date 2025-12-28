package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadResponse{

    private Long id;
    private String key;
    private String originalName;
    private String contentType;
    private Long size;
    private String url;
    private Instant uploadedAt;
}
