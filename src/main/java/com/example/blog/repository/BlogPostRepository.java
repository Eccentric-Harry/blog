// backend: src/main/java/com/example/blog/repository/BlogPostRepository.java
package com.example.blog.repository;

import com.example.blog.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    boolean existsByTitle(String title);

    boolean existsBySlug(String slug);

    Optional<BlogPost> findBySlug(String slug);

    /**
     * Find all published posts with pagination, ordered by creation date desc.
     */
    Page<BlogPost> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find all archived posts with pagination, ordered by creation date desc.
     */
    Page<BlogPost> findByArchivedTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find all published and non-archived posts with pagination.
     */
    Page<BlogPost> findByPublishedTrueAndArchivedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find published posts by tag slug.
     */
    @Query("""
        SELECT DISTINCT p FROM BlogPost p
        JOIN p.tags t
        WHERE p.published = true AND t.slug = :tagSlug
        ORDER BY p.createdAt DESC
        """)
    Page<BlogPost> findPublishedByTagSlug(@Param("tagSlug") String tagSlug, Pageable pageable);

    /**
     * Find published posts by category slug.
     */
    @Query("""
        SELECT p FROM BlogPost p
        WHERE p.published = true AND p.category.slug = :categorySlug
        ORDER BY p.createdAt DESC
        """)
    Page<BlogPost> findPublishedByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);

    /**
     * Find published posts by both tag and category.
     */
    @Query("""
        SELECT DISTINCT p FROM BlogPost p
        JOIN p.tags t
        WHERE p.published = true
        AND t.slug = :tagSlug
        AND p.category.slug = :categorySlug
        ORDER BY p.createdAt DESC
        """)
    Page<BlogPost> findPublishedByTagAndCategory(
        @Param("tagSlug") String tagSlug,
        @Param("categorySlug") String categorySlug,
        Pageable pageable
    );

    /**
     * Get recently updated published posts.
     */
    @Query("""
        SELECT p FROM BlogPost p
        WHERE p.published = true
        ORDER BY p.updatedAt DESC
        """)
    List<BlogPost> findRecentlyUpdated(Pageable pageable);

    /**
     * Search published posts by title or content (case insensitive).
     * Uses native query because JPQL LOWER() doesn't work with TEXT/CLOB columns in Hibernate 6.
     */
    @Query(value = """
        SELECT * FROM posts p
        WHERE p.published = true
        AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(CAST(p.content AS TEXT)) LIKE LOWER(CONCAT('%', :query, '%')))
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    Page<BlogPost> searchPublished(@Param("query") String query, Pageable pageable);
}
