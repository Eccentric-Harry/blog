package com.example.blog.mapper;


import com.example.blog.dto.*;
import com.example.blog.entity.BlogPost;
import com.example.blog.entity.Image;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class BlogPostMapper {
    private BlogPostMapper(){

    }

    public static BlogPost toEntity(CreatePostRequest req){
        return BlogPost.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .slug(req.getSlug())
                .coverImageUrl(req.getCoverImageUrl())
                .readTime(req.getReadTime()!= null ? req.getReadTime() : 0)
                .build();
    }

    public static void updateEntity(BlogPost entity, UpdatePostRequest req){
        if (req.getTitle() != null) entity.setTitle(req.getTitle());
        if (req.getContent() != null) entity.setContent(req.getContent());
        if (req.getSlug() != null) entity.setSlug(req.getSlug());
        if (req.getCoverImageUrl() != null) entity.setCoverImageUrl(req.getCoverImageUrl());
        if (req.getReadTime() != null) entity.setReadTime(req.getReadTime());
    }

    public static PostResponse toResponse(BlogPost post, List<Image> images){
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .coverImageUrl(post.getCoverImageUrl())
                .readTime(post.getReadTime())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .images(images == null ? List.of() : images.stream().map(BlogPostMapper::toImageResponse).collect(Collectors.toList()))
                .build();
    }

    public static PostSummaryResponse toSummary(BlogPost post, String excerpt) {
        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(excerpt)
                .coverImageUrl(post.getCoverImageUrl())
                .readTime(post.getReadTime())
                .createdAt(post.getCreatedAt())
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
}
