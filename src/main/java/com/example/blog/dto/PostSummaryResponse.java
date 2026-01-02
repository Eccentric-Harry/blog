// backend: src/main/java/com/example/blog/dto/PostSummaryResponse.java
package com.example.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryResponse {

    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String author;
    private String coverImageUrl;
    private Integer readTime;
    private boolean archived;

    private String categoryName;
    private String categorySlug;
    private List<String> tags;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant updatedAt;
}
