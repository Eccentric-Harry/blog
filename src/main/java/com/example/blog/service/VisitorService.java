// backend: src/main/java/com/example/blog/service/VisitorService.java
package com.example.blog.service;

import com.example.blog.dto.VisitorCountResponse;

/**
 * Service for tracking site visitor statistics.
 */
public interface VisitorService {

    /**
     * Get the current total visitor count.
     */
    VisitorCountResponse getVisitorCount();

    /**
     * Increment the visitor count and return the updated count.
     */
    VisitorCountResponse incrementAndGetVisitorCount();
}
