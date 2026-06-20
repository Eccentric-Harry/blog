package com.example.blog.service;

import com.example.blog.dto.CreatePostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.entity.BlogPost;
import com.example.blog.mapper.BlogPostMapper;
import com.example.blog.repository.BlogPostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        if (blogPostRepository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("Title already exists!");
        }

        BlogPost post = BlogPostMapper.toEntity(request);
        BlogPost saved = blogPostRepository.save(post);
        return BlogPostMapper.toResponse(saved, null);
    }

    @Override
    @Transactional
    public List<PostResponse> findAll() {
        return blogPostRepository.findAll()
                .stream()
                .map(post -> BlogPostMapper.toResponse(post, null))
                .collect(Collectors.toList());
    }
}

