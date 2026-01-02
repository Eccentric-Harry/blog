// backend: src/main/java/com/example/blog/service/BlogPostServiceImpl.java
package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.BlogPost;
import com.example.blog.entity.Category;
import com.example.blog.entity.Tag;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.BlogPostMapper;
import com.example.blog.repository.BlogPostRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        if (blogPostRepository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("A post with this title already exists");
        }

        BlogPost post = BlogPostMapper.toEntity(request);

        // Handle category
        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            Category category = findOrCreateCategory(request.getCategoryName());
            post.setCategory(category);
        }

        // Handle tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = findOrCreateTags(request.getTags());
            post.setTags(tags);
        }

        BlogPost saved = blogPostRepository.save(post);
        log.info("Created new post: id={}, title={}", saved.getId(), saved.getTitle());
        return BlogPostMapper.toResponse(saved, null);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long id, UpdatePostRequest request) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        BlogPostMapper.updateEntity(post, request);

        // Handle category update
        if (request.getCategoryName() != null) {
            if (request.getCategoryName().isBlank()) {
                post.setCategory(null);
            } else {
                Category category = findOrCreateCategory(request.getCategoryName());
                post.setCategory(category);
            }
        }

        // Handle tags update
        if (request.getTags() != null) {
            // Clear existing tags
            post.getTags().clear();
            if (!request.getTags().isEmpty()) {
                Set<Tag> tags = findOrCreateTags(request.getTags());
                post.setTags(tags);
            }
        }

        BlogPost saved = blogPostRepository.save(post);
        log.info("Updated post: id={}", saved.getId());
        return BlogPostMapper.toResponse(saved, null);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Clear tag associations before deletion
        post.getTags().clear();
        blogPostRepository.delete(post);
        log.info("Deleted post: id={}", id);
    }

    @Override
    @Transactional
    public PostResponse findById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return BlogPostMapper.toResponse(post, null);
    }

    @Override
    @Transactional
    public PostResponse findBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with slug: " + slug));
        return BlogPostMapper.toResponse(post, null);
    }

    @Override
    @Transactional
    public Page<PostSummaryResponse> findAllPublished(Pageable pageable) {
        return blogPostRepository.findByPublishedTrueOrderByCreatedAtDesc(pageable)
                .map(BlogPostMapper::toSummary);
    }

    @Override
    @Transactional
    public Page<PostSummaryResponse> findByTag(String tagSlug, Pageable pageable) {
        return blogPostRepository.findPublishedByTagSlug(tagSlug, pageable)
                .map(BlogPostMapper::toSummary);
    }

    @Override
    @Transactional
    public Page<PostSummaryResponse> findByCategory(String categorySlug, Pageable pageable) {
        return blogPostRepository.findPublishedByCategorySlug(categorySlug, pageable)
                .map(BlogPostMapper::toSummary);
    }

    @Override
    @Transactional
    public Page<PostSummaryResponse> findByTagAndCategory(String tagSlug, String categorySlug, Pageable pageable) {
        return blogPostRepository.findPublishedByTagAndCategory(tagSlug, categorySlug, pageable)
                .map(BlogPostMapper::toSummary);
    }

    @Override
    @Transactional
    public Page<PostSummaryResponse> search(String query, Pageable pageable) {
        return blogPostRepository.searchPublished(query, pageable)
                .map(BlogPostMapper::toSummary);
    }

    @Override
    @Transactional
    public List<PostSummaryResponse> findRecentlyUpdated(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return blogPostRepository.findRecentlyUpdated(pageable).stream()
                .map(BlogPostMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<PostResponse> findAll() {
        return blogPostRepository.findAll().stream()
                .map(post -> BlogPostMapper.toResponse(post, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<PostSummaryResponse> findAllArchived(Pageable pageable) {
        return blogPostRepository.findByArchivedTrueOrderByCreatedAtDesc(pageable)
                .map(BlogPostMapper::toSummary);
    }

    @Override
    @Transactional
    public PostResponse archivePost(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        post.setArchived(true);
        BlogPost saved = blogPostRepository.save(post);
        log.info("Archived post: id={}", id);
        return BlogPostMapper.toResponse(saved, null);
    }

    @Override
    @Transactional
    public PostResponse unarchivePost(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        post.setArchived(false);
        BlogPost saved = blogPostRepository.save(post);
        log.info("Unarchived post: id={}", id);
        return BlogPostMapper.toResponse(saved, null);
    }

    @Override
    @Transactional
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    long postCount = blogPostRepository.findByPublishedTrueAndArchivedFalseOrderByCreatedAtDesc(PageRequest.of(0, Integer.MAX_VALUE))
                            .stream()
                            .filter(post -> post.getCategory() != null && post.getCategory().getId().equals(category.getId()))
                            .count();
                    return CategoryResponse.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .slug(category.getSlug())
                            .description(category.getDescription())
                            .postCount((int) postCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> {
                    long postCount = tag.getPosts().stream()
                            .filter(post -> post.isPublished() && !post.isArchived())
                            .count();
                    return TagResponse.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .slug(tag.getSlug())
                            .postCount((int) postCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Helper methods

    private Category findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .name(name)
                            .build();
                    return categoryRepository.save(newCategory);
                });
    }

    private Set<Tag> findOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            String trimmed = tagName.trim();
            if (!trimmed.isBlank()) {
                Tag tag = tagRepository.findByName(trimmed)
                        .orElseGet(() -> {
                            Tag newTag = Tag.builder()
                                    .name(trimmed)
                                    .build();
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
        }
        return tags;
    }
}

