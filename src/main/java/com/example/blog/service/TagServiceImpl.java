// backend: src/main/java/com/example/blog/service/TagServiceImpl.java
package com.example.blog.service;

import com.example.blog.dto.TagResponse;
import com.example.blog.mapper.BlogPostMapper;
import com.example.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> findAllWithPublishedPosts() {
        return tagRepository.findAllWithPublishedPosts().stream()
                .map(BlogPostMapper::toTagResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagResponse> findTrending(int limit) {
        return tagRepository.findTrendingTags(limit).stream()
                .map(BlogPostMapper::toTagResponse)
                .collect(Collectors.toList());
    }
}

