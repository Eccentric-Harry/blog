package com.example.blog.mapper;

import com.example.blog.dto.CreatePostDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.BlogPost;

public class PostMapper {

    public static BlogPost toEntity(CreatePostDto dto){
        BlogPost post = new BlogPost();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return post;
    }

    public static PostResponseDto toDto(BlogPost entity){
        PostResponseDto responseDto = new PostResponseDto();
        entity.setTitle(entity.getTitle());
        entity.setContent(entity.getContent());
        entity.setCreatedAt(entity.getCreatedAt());
        return responseDto;
    }
}
