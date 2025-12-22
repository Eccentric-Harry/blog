package com.example.blog.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor

public class CreatePostDto {
    @Getter
    @NotBlank
    private String title;
    @Getter
    @NotBlank
    private String content;

}
