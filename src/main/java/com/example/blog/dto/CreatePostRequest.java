package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;
    @NotBlank
    @Size(min = 3, max = 100)
    private String content;

    @Size(max = 10)
    private String slug;

    private String coverImageUrl; // returned by minIo upload

    //private int readTime; // backend should compute this.
}
