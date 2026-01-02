// backend: src/main/java/com/example/blog/service/BlogPostService.java
package com.example.blog.service;

import com.example.blog.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Updates an existing blog post.
     *
     * @param id the post ID
     * @param request the update request
     * @return the updated post
     */
    PostResponse updatePost(Long id, UpdatePostRequest request);

    /**
     * Deletes a blog post by ID.
     *
     * @param id the post ID
     */
    void deletePost(Long id);

    /**
     * Retrieves a post by ID.
     *
     * @param id the post ID
     * @return the post response
     */
    PostResponse findById(Long id);

    /**
     * Retrieves a post by slug.
     *
     * @param slug the post slug
     * @return the post response
     */
    PostResponse findBySlug(String slug);

    /**
     * Retrieves all published posts with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of post summaries
     */
    Page<PostSummaryResponse> findAllPublished(Pageable pageable);

    /**
     * Retrieves published posts filtered by tag.
     *
     * @param tagSlug the tag slug
     * @param pageable pagination parameters
     * @return paginated list of post summaries
     */
    Page<PostSummaryResponse> findByTag(String tagSlug, Pageable pageable);

    /**
     * Retrieves published posts filtered by category.
     *
     * @param categorySlug the category slug
     * @param pageable pagination parameters
     * @return paginated list of post summaries
     */
    Page<PostSummaryResponse> findByCategory(String categorySlug, Pageable pageable);

    /**
     * Retrieves published posts filtered by both tag and category.
     *
     * @param tagSlug the tag slug
     * @param categorySlug the category slug
     * @param pageable pagination parameters
     * @return paginated list of post summaries
     */
    Page<PostSummaryResponse> findByTagAndCategory(String tagSlug, String categorySlug, Pageable pageable);

    /**
     * Searches published posts by query string.
     *
     * @param query the search query
     * @param pageable pagination parameters
     * @return paginated list of post summaries
     */
    Page<PostSummaryResponse> search(String query, Pageable pageable);

    /**
     * Retrieves recently updated posts.
     *
     * @param limit maximum number of posts to return
     * @return list of post summaries
     */
    List<PostSummaryResponse> findRecentlyUpdated(int limit);

    /**
     * Retrieves all blog posts (admin view - includes unpublished).
     *
     * @return list of all posts
     */
    List<PostResponse> findAll();

    /**
     * Retrieves all archived posts with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of archived post summaries
     */
    Page<PostSummaryResponse> findAllArchived(Pageable pageable);

    /**
     * Archives a blog post.
     *
     * @param id the post ID
     * @return the updated post
     */
    PostResponse archivePost(Long id);

    /**
     * Unarchives a blog post.
     *
     * @param id the post ID
     * @return the updated post
     */
    PostResponse unarchivePost(Long id);

    /**
     * Retrieves all categories with post counts.
     *
     * @return list of categories
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Retrieves all tags with post counts.
     *
     * @return list of tags
     */
    List<TagResponse> getAllTags();
}
