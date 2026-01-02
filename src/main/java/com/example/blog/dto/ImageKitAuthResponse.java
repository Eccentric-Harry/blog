package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageKitAuthResponse {
    private String token;
    private long expire; // unix time in seconds
    private String signature;
}

