package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {
    @Size(max=300)
    private String title;

    @NotBlank
    private String content;

    @Size(max = 10)
    private String slug;

    private String coverImageUrl;
    //private Integer readTime;

    private boolean published;

}

