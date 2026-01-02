// backend: src/main/java/com/example/blog/service/TagService.java
package com.example.blog.service;

import com.example.blog.dto.TagResponse;

import java.util.List;

/**
 * Service for tag operations.
 */
public interface TagService {

    /**
     * Get all tags that have at least one published post.
     */
    List<TagResponse> findAllWithPublishedPosts();

    /**
     * Get trending tags (most used).
     */
    List<TagResponse> findTrending(int limit);
}

