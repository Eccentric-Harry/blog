// backend: src/main/java/com/example/blog/repository/TagRepository.java
package com.example.blog.repository;

import com.example.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Optional<Tag> findBySlug(String slug);

    boolean existsByName(String name);

    /**
     * Find trending tags ordered by post count (descending).
     * Limits to tags that have at least one published post.
     */
    @Query("""
        SELECT t FROM Tag t
        JOIN t.posts p
        WHERE p.published = true
        GROUP BY t
        ORDER BY COUNT(p) DESC
        LIMIT :limit
        """)
    List<Tag> findTrendingTags(int limit);

    /**
     * Find all tags with at least one published post.
     */
    @Query("""
        SELECT DISTINCT t FROM Tag t
        JOIN t.posts p
        WHERE p.published = true
        ORDER BY t.name
        """)
    List<Tag> findAllWithPublishedPosts();
}

