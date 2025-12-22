package com.example.blog.service;

import com.example.blog.dto.CreatePostDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.BlogPost;
import com.example.blog.mapper.PostMapper;
import com.example.blog.repository.BlogPostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Transactional
    public PostResponseDto createPost(CreatePostDto createPostDto) {
        if(blogPostRepository.existsByTitle(createPostDto.getTitle())) {
            throw new IllegalArgumentException("Title already exists!");
        }

        BlogPost post = PostMapper.toEntity(createPostDto);
        BlogPost saved = blogPostRepository.save(post);
        return PostMapper.toDto(saved);
    }

    @Transactional
    public List<PostResponseDto> findAll() {
        return blogPostRepository.findAll()
                .stream()
                .map(PostMapper::toDto)
                .collect(Collectors.toList());
    }
}
