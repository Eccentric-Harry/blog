package com.example.blog.controller;

import com.example.blog.dto.CreatePostDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {
    private final BlogPostService blogPostService;
    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> create(@Valid @RequestBody CreatePostDto createPostDto) {
        PostResponseDto created = blogPostService.createPost(createPostDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> findAll() {
        return ResponseEntity.ok(blogPostService.findAll());
    }
}
