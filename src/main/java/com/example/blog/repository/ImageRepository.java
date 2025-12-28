package com.example.blog.repository;

import com.example.blog.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPostId(Long postId);
}

