package com.example.madcamp.peogayo.Back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(columnDefinition = "TEXT")
    private String contentImageUrl; // 그림/사진 경로

    private String contentText;

    @Enumerated(EnumType.STRING)
    private Visibility visibility; // PUBLIC, FRIENDS, PRIVATE

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.visibility == null) this.visibility = Visibility.PUBLIC;
    }

    public enum Visibility {
        PUBLIC, FRIENDS, PRIVATE
    }
}