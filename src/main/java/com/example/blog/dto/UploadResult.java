package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of an upload operation to the storage service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {
    private String key;      // File path/key in storage
    private String fileId;   // Storage provider's file ID (e.g., ImageKit fileId)
    private String url;      // CDN/public URL for the file
}

