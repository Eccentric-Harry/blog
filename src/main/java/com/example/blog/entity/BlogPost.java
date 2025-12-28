package com.example.blog.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private Instant createdAt;
    private Instant updatedAt;
    private String slug;
    private String coverImageUrl;
    private int readTime;


    @PrePersist
    void onCreate(){
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate(){
        updatedAt = Instant.now();
    }
}
