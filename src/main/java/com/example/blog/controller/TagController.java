// backend: src/main/java/com/example/blog/controller/TagController.java
package com.example.blog.controller;

import com.example.blog.dto.TagResponse;
import com.example.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * GET /api/tags - Get all tags with published posts.
     */
    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.findAllWithPublishedPosts());
    }

    /**
     * GET /api/tags/trending - Get trending tags.
     */
    @GetMapping("/trending")
    public ResponseEntity<List<TagResponse>> getTrendingTags(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(tagService.findTrending(Math.min(limit, 50)));
    }
}

