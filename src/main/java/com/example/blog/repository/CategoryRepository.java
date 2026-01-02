// backend: src/main/java/com/example/blog/repository/CategoryRepository.java
package com.example.blog.repository;

import com.example.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    /**
     * Find all categories that have at least one published post.
     */
    @Query("""
        SELECT DISTINCT c FROM Category c
        JOIN c.posts p
        WHERE p.published = true
        ORDER BY c.name
        """)
    List<Category> findAllWithPublishedPosts();
}

