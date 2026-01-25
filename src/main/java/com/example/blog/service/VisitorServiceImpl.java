// backend: src/main/java/com/example/blog/service/VisitorServiceImpl.java
package com.example.blog.service;

import com.example.blog.dto.VisitorCountResponse;
import com.example.blog.entity.SiteStats;
import com.example.blog.repository.SiteStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of visitor tracking service.
 * Uses atomic database updates to safely handle concurrent visitor increments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorServiceImpl implements VisitorService {

    private final SiteStatsRepository siteStatsRepository;

    /**
     * Ensure the site stats row exists, creating it if necessary.
     * This is called lazily when stats are first accessed.
     */
    private SiteStats ensureSiteStatsExists() {
        return siteStatsRepository.findById(SiteStats.GLOBAL_STATS_ID)
                .orElseGet(() -> {
                    log.info("Initializing site stats record");
                    SiteStats stats = SiteStats.builder()
                            .id(SiteStats.GLOBAL_STATS_ID)
                            .totalVisitors(0L)
                            .build();
                    return siteStatsRepository.save(stats);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public VisitorCountResponse getVisitorCount() {
        SiteStats stats = siteStatsRepository.findById(SiteStats.GLOBAL_STATS_ID)
                .orElse(SiteStats.builder()
                        .id(SiteStats.GLOBAL_STATS_ID)
                        .totalVisitors(0L)
                        .build());

        return VisitorCountResponse.builder()
                .totalVisitors(stats.getTotalVisitors())
                .lastUpdated(stats.getLastUpdated())
                .build();
    }

    @Override
    @Transactional
    public VisitorCountResponse incrementAndGetVisitorCount() {
        // First ensure the record exists
        ensureSiteStatsExists();

        // Use atomic increment to prevent race conditions
        siteStatsRepository.incrementVisitorCount(SiteStats.GLOBAL_STATS_ID);

        return getVisitorCount();
    }
}
