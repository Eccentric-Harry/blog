// backend: src/main/java/com/example/blog/service/CategoryService.java
package com.example.blog.service;

import com.example.blog.dto.CategoryResponse;

import java.util.List;

/**
 * Service for category operations.
 */
public interface CategoryService {

    /**
     * Get all categories that have at least one published post.
     */
    List<CategoryResponse> findAllWithPublishedPosts();
}

