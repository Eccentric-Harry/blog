package com.example.blog.service;

import com.example.blog.dto.CreatePostRequest;
import com.example.blog.dto.PostResponse;

import java.util.List;

/**
 * Service abstraction for blog post operations.
 */
public interface BlogPostService {

    /**
     * Creates a new blog post.
     *
     * @param request the post creation request
     * @return the created post as a response DTO
     * @throws IllegalArgumentException if a post with the same title already exists
     */
    PostResponse createPost(CreatePostRequest request);

    /**
     * Retrieves all blog posts.
     *
     * @return list of all posts
     */
    List<PostResponse> findAll();
}

