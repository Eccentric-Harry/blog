// backend: src/main/java/com/example/blog/controller/VisitorController.java
package com.example.blog.controller;

import com.example.blog.dto.VisitorCountResponse;
import com.example.blog.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for visitor tracking.
 */
@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    /**
     * GET /api/visitors - Get the current visitor count.
     */
    @GetMapping
    public ResponseEntity<VisitorCountResponse> getVisitorCount() {
        return ResponseEntity.ok(visitorService.getVisitorCount());
    }

    /**
     * POST /api/visitors/track - Increment visitor count and return updated count.
     * Called once per unique session/visit from the frontend.
     */
    @PostMapping("/track")
    public ResponseEntity<VisitorCountResponse> trackVisitor() {
        return ResponseEntity.ok(visitorService.incrementAndGetVisitorCount());
    }
}
