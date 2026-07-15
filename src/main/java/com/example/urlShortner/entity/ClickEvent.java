package com.example.urlShortner.entity;


import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "click_events", indexes = {
        @Index(name = "idx_click_short_code", columnList = "shortCode"),
        @Index(name = "idx_click_clicked_at", columnList = "clickedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String shortCode; // denormalized for fast querying, no join needed

    @Column(nullable = false, updatable = false)
    private Instant clickedAt;

    @Column(length = 45)
    private String ipAddress; // supports IPv6

    @Column(length = 512)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        this.clickedAt = Instant.now();
    }
}