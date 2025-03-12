package com.insights.client.source_control_insights.Entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "repositories")
public class Repository {
    @Id
    @GeneratedValue
    private UUID repoId;

    @Column(nullable = false)
    private String repoName;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private java.time.Instant createdAt;
}
