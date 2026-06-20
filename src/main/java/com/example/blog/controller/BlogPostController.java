// backend: src/main/java/com/example/blog/controller/BlogPostController.java
package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;

    /**
     * GET /api/posts - List published posts with pagination and optional filters.
     *
     * @param page page number (0-based)
     * @param size page size
     * @param tag optional tag slug filter
     * @param category optional category slug filter
     * @param q optional search query
     */
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50)); // Cap at 50

        Page<PostSummaryResponse> result;

        if (q != null && !q.isBlank()) {
            // Search takes priority
            result = blogPostService.search(q.trim(), pageable);
        } else if (tag != null && category != null) {
            result = blogPostService.findByTagAndCategory(tag, category, pageable);
        } else if (tag != null) {
            result = blogPostService.findByTag(tag, pageable);
        } else if (category != null) {
            result = blogPostService.findByCategory(category, pageable);
        } else {
            result = blogPostService.findAllPublished(pageable);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/posts/recent - Get recently updated posts.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<PostSummaryResponse>> getRecentPosts(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(blogPostService.findRecentlyUpdated(Math.min(limit, 20)));
    }

    /**
     * GET /api/posts/{id} - Get a single post by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.findById(id));
    }

    /**
     * GET /api/posts/slug/{slug} - Get a single post by slug.
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<PostResponse> getPostBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogPostService.findBySlug(slug));
    }

    /**
     * POST /api/posts - Create a new post.
     * TODO: Add @PreAuthorize("hasRole('ADMIN')") when security is configured.
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse created = blogPostService.createPost(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /api/posts/{id} - Update an existing post.
     * TODO: Add @PreAuthorize("hasRole('ADMIN')") when security is configured.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        return ResponseEntity.ok(blogPostService.updatePost(id, request));
    }

    /**
     * DELETE /api/posts/{id} - Delete a post.
     * TODO: Add @PreAuthorize("hasRole('ADMIN')") when security is configured.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/posts/archived - List archived posts with pagination.
     */
    @GetMapping("/archived")
    public ResponseEntity<Page<PostSummaryResponse>> listArchivedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        return ResponseEntity.ok(blogPostService.findAllArchived(pageable));
    }

    /**
     * POST /api/posts/{id}/archive - Archive a post.
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<PostResponse> archivePost(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.archivePost(id));
    }

    /**
     * POST /api/posts/{id}/unarchive - Unarchive a post.
     */
    @PostMapping("/{id}/unarchive")
    public ResponseEntity<PostResponse> unarchivePost(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.unarchivePost(id));
    }

    /**
     * GET /api/posts/categories/all - Get all categories with post counts.
     */
    @GetMapping("/categories/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(blogPostService.getAllCategories());
    }

    /**
     * GET /api/posts/tags/all - Get all tags with post counts.
     */
    @GetMapping("/tags/all")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(blogPostService.getAllTags());
    }
}
