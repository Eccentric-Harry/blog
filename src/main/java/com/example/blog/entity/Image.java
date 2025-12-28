package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;           // S3 object key (UUID.ext)
    private String originalName;
    private String contentType;
    private Long size;
    private String url;           // optional (public or presigned)
    private Instant uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private com.example.blog.entity.BlogPost post;

    @PrePersist
    void onCreate() { uploadedAt = Instant.now(); }
}

