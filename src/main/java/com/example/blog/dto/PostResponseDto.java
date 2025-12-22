package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    @Getter
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;

}
