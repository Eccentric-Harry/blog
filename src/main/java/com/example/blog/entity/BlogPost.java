package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_posts_slug", columnList = "slug"),
    @Index(name = "idx_posts_created_at", columnList = "createdAt"),
    @Index(name = "idx_posts_published", columnList = "published"),
    @Index(name = "idx_posts_archived", columnList = "archived")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 1000)
    private String excerpt;

    @Column(length = 100)
    private String author;

    @Column(unique = true, length = 255)
    private String slug;

    private String coverImageUrl;

    @Builder.Default
    private int readTime = 0;

    @Builder.Default
    private boolean published = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean archived = false;

    private Instant createdAt;
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        generateSlugIfMissing();
        calculateReadTime();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
        calculateReadTime();
    }

    private void generateSlugIfMissing() {
        if (slug == null || slug.isBlank()) {
            slug = title.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]+", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("^-|-$", "");
            // Add timestamp suffix for uniqueness
            slug = slug + "-" + System.currentTimeMillis();
        }
    }

    private void calculateReadTime() {
        if (content != null && !content.isBlank()) {
            // Strip HTML and count words, average reading speed ~200 words/min
            String textOnly = content.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
            int wordCount = textOnly.split("\\s+").length;
            readTime = Math.max(1, (int) Math.ceil(wordCount / 200.0));
        }
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }
}
