package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryResponse {

    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String coverImageUrl;
    private Integer readTime;
    private Instant createdAt;

}
