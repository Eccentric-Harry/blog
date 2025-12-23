package com.example.blog.dto;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
}
