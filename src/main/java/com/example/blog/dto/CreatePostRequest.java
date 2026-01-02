// backend: src/main/java/com/example/blog/dto/CreatePostRequest.java
package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    private String content;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;

    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;

    private String coverImageUrl;

    private String categoryName; // Will create or link to existing category

    private List<String> tags; // Will create or link to existing tags

    private Boolean published; // Defaults to false if not provided
    private Boolean archived; // Defaults to false if not provided
}
