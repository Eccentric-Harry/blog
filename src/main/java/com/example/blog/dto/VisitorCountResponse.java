// backend: src/main/java/com/example/blog/dto/VisitorCountResponse.java
package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for visitor count statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorCountResponse {
    private Long totalVisitors;
    private LocalDateTime lastUpdated;
}
