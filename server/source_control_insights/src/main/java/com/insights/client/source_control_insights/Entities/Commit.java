package com.insights.client.source_control_insights.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Commit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses the DB's auto-increment
    @Column(name = "commit_id")
    public int commitId;

    @Column(name = "commitHash")
    public String commitHash;

    @Column(name = "commit_message")
    public String commitMessage;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    public User contributor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    public Repository repository;

}
