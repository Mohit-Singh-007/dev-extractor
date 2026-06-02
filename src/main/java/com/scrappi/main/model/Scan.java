package com.scrappi.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scan")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Enumerated(EnumType.STRING)
    private ScanStatus status;

    private String title;

    @Column(length = 5000)
    private String description;

    private Integer totalLinks;

    private Integer totalImages;

    private Integer totalScripts;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;


    @OneToMany(
            mappedBy = "scan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Technology> technologies =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "scan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Font> fonts = new ArrayList<>();
}
