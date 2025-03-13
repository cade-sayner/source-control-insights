package com.insights.client.source_control_insights.Entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "commits")
public class Commit {
    @Id
    @GeneratedValue
    private UUID commitId;

    @ManyToOne
    @JoinColumn(name = "contributer_id", nullable = false)
    private User contributer;

    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @Column(unique = true, nullable = false)
    private String commitHash;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private java.time.Instant commitTimestamp;
}
