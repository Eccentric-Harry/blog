// backend: src/main/java/com/example/blog/entity/SiteStats.java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity to store site-wide statistics like total visitor count.
 * Uses a single row with a fixed ID for global counters.
 */
@Entity
@Table(name = "site_stats")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SiteStats {

    public static final Long GLOBAL_STATS_ID = 1L;

    @Id
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private Long totalVisitors = 0L;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    void updateTimestamp() {
        lastUpdated = LocalDateTime.now();
    }
}
