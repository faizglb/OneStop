package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "learning_resources", schema = "learning_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "platform", nullable = false, length = 100)
    private String platform; // YouTube | edX | Coursera | Udemy | NPTEL | freeCodeCamp | books

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType; // Video Course | Playlist | Book | Article | Documentation | GitHub Project | Certification Course

    @Column(name = "category", nullable = false, length = 100)
    private String category; // Programming Languages | Frameworks | DSA | Soft Skills | System Design | Cloud | DevOps | AI/ML | Database

    @Column(name = "skill", nullable = false, length = 100)
    private String skill;

    @Column(name = "difficulty", nullable = false, length = 30)
    private String difficulty; // BEGINNER | INTERMEDIATE | ADVANCED

    @Column(name = "duration", nullable = false, length = 50)
    private String duration;

    @Column(name = "language", nullable = false, length = 50)
    @Builder.Default
    private String language = "English";

    @Column(name = "is_free", nullable = false)
    @Builder.Default
    private Boolean isFree = true;

    @Column(name = "price", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @Column(name = "resource_url", nullable = false, length = 250)
    private String resourceUrl;

    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "provider", length = 100)
    private String provider;

    @Column(name = "certificate_available", nullable = false)
    @Builder.Default
    private Boolean certificateAvailable = false;
}
