package com.insights.client.source_control_insights.Entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    private String googleId;
    private String repoUrl;
    private Instant createdAt;

    public Repository(String repoName, String provider, String creatorGoogleId, String repoUrl, Instant createdAt) {
        this.repoName = repoName;
        this.provider = provider;
        this.googleId = creatorGoogleId;
        this.repoUrl = repoUrl;
        this.createdAt = createdAt;
    }

    @JsonIgnore
    @OneToMany(mappedBy="repository")
    public List<Commit> commits;

    public Repository() {}

    // Getters
    public UUID getRepoId() {
        return repoId;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getProvider() {
        return provider;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setRepoId(UUID repoId) {
        this.repoId = repoId;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}