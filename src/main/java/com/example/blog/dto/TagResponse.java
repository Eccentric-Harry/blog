package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagResponse {
    private Long id;
    private String name;
    private String slug;
    private Integer postCount; // Optional: number of posts with this tag
}

