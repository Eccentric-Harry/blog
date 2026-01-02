// backend: src/main/java/com/example/blog/mapper/BlogPostMapper.java
package com.example.blog.mapper;

import com.example.blog.dto.*;
import com.example.blog.entity.BlogPost;
import com.example.blog.entity.Category;
import com.example.blog.entity.Image;
import com.example.blog.entity.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class BlogPostMapper {

    private BlogPostMapper() {
        // Utility class
    }

    /**
     * Convert CreatePostRequest to BlogPost entity.
     * Note: Tags and Category are set separately by the service.
     */
    public static BlogPost toEntity(CreatePostRequest req) {
        return BlogPost.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .excerpt(req.getExcerpt() != null ? req.getExcerpt() : generateExcerpt(req.getContent()))
                .slug(req.getSlug())
                .author(req.getAuthor())
                .coverImageUrl(req.getCoverImageUrl())
                .published(req.getPublished() != null ? req.getPublished() : false)
                .archived(req.getArchived() != null ? req.getArchived() : false)
                .build();
    }

    /**
     * Update existing BlogPost entity from UpdatePostRequest.
     * Note: Tags and Category are updated separately by the service.
     */
    public static void updateEntity(BlogPost entity, UpdatePostRequest req) {
        if (req.getTitle() != null) {
            entity.setTitle(req.getTitle());
        }
        if (req.getContent() != null) {
            entity.setContent(req.getContent());
            // Update excerpt if not explicitly provided
            if (req.getExcerpt() == null) {
                entity.setExcerpt(generateExcerpt(req.getContent()));
            }
        }
        if (req.getExcerpt() != null) {
            entity.setExcerpt(req.getExcerpt());
        }
        if (req.getSlug() != null) {
            entity.setSlug(req.getSlug());
        }
        if (req.getAuthor() != null) {
            entity.setAuthor(req.getAuthor());
        }
        if (req.getCoverImageUrl() != null) {
            entity.setCoverImageUrl(req.getCoverImageUrl());
        }
        if (req.getPublished() != null) {
            entity.setPublished(req.getPublished());
        }
        if (req.getArchived() != null) {
            entity.setArchived(req.getArchived());
        }
    }

    /**
     * Convert BlogPost entity to full PostResponse DTO.
     */
    public static PostResponse toResponse(BlogPost post, List<Image> images) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .excerpt(post.getExcerpt())
                .author(post.getAuthor())
                .coverImageUrl(post.getCoverImageUrl())
                .readTime(post.getReadTime())
                .published(post.isPublished())
                .archived(post.isArchived())
                .category(post.getCategory() != null ? toCategoryResponse(post.getCategory()) : null)
                .tags(post.getTags() != null ? post.getTags().stream()
                        .map(BlogPostMapper::toTagResponse)
                        .collect(Collectors.toList()) : List.of())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .images(images == null ? List.of() : images.stream()
                        .map(BlogPostMapper::toImageResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Convert BlogPost entity to summary DTO for listing.
     */
    public static PostSummaryResponse toSummary(BlogPost post) {
        String excerpt = post.getExcerpt();
        if (excerpt == null || excerpt.isBlank()) {
            excerpt = generateExcerpt(post.getContent());
        }

        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(excerpt)
                .author(post.getAuthor())
                .coverImageUrl(post.getCoverImageUrl())
                .readTime(post.getReadTime())
                .archived(post.isArchived())
                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                .categorySlug(post.getCategory() != null ? post.getCategory().getSlug() : null)
                .tags(post.getTags() != null ? post.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()) : List.of())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static TagResponse toTagResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .postCount(0) // Post count will be set separately by service if needed
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .postCount(0) // Post count will be set separately by service if needed
                .build();
    }

    public static ImageResponse toImageResponse(Image img) {
        return ImageResponse.builder()
                .id(img.getId())
                .key(img.getKey())
                .originalName(img.getOriginalName())
                .url(img.getUrl())
                .uploadedAt(img.getUploadedAt())
                .postId(img.getPost() != null ? img.getPost().getId() : null)
                .build();
    }

    /**
     * Generate an excerpt from content by stripping HTML and truncating.
     */
    private static String generateExcerpt(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        // Strip HTML tags
        String textOnly = content.replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
        // Truncate to ~200 chars at word boundary
        if (textOnly.length() <= 200) {
            return textOnly;
        }
        int endIndex = textOnly.lastIndexOf(' ', 200);
        if (endIndex == -1) {
            endIndex = 200;
        }
        return textOnly.substring(0, endIndex) + "...";
    }
}
