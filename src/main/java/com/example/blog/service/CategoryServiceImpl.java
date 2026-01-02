// backend: src/main/java/com/example/blog/service/CategoryServiceImpl.java
package com.example.blog.service;

import com.example.blog.dto.CategoryResponse;
import com.example.blog.mapper.BlogPostMapper;
import com.example.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> findAllWithPublishedPosts() {
        return categoryRepository.findAllWithPublishedPosts().stream()
                .map(BlogPostMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
}

