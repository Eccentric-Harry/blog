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
        responseDto.setId(entity.getId());
        responseDto.setTitle(entity.getTitle());
        responseDto.setContent(entity.getContent());
        responseDto.setCreatedAt(entity.getCreatedAt());
        return responseDto;
    }
}
