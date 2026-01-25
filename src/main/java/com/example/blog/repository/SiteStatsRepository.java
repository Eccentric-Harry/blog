// backend: src/main/java/com/example/blog/repository/SiteStatsRepository.java
package com.example.blog.repository;

import com.example.blog.entity.SiteStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteStatsRepository extends JpaRepository<SiteStats, Long> {

    /**
     * Atomically increment the visitor count to prevent race conditions.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SiteStats s SET s.totalVisitors = s.totalVisitors + 1, s.lastUpdated = CURRENT_TIMESTAMP WHERE s.id = :id")
    int incrementVisitorCount(@Param("id") Long id);
}
