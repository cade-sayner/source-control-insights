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
     
    private String creatorGoogleId;

    private String repoUrl;

    private java.time.Instant createdAt;

    public Repository(String repoName, String provider, String creatorGoogleId, String repo_url, java.time.Instant createdAt){
        this.repoName = repoName;
        this.provider = provider;
        this.creatorGoogleId = creatorGoogleId;
        this.repoUrl = repo_url;
        this.createdAt = createdAt;
    }

    public Repository(){}
}
