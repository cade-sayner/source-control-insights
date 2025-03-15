package com.insights.client.source_control_insights.Entities;

import java.time.Instant;
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
    @JoinColumn(name = "contributor_id", nullable = false)
    private User contributor;

    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @Column(unique = true, nullable = false)
    private String commitHash;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant commitTimestamp;

    // Constructors
    public Commit() {}

    public Commit(User contributer, Repository repository, String commitHash, String message, Instant commitTimestamp) {
        this.contributor = contributer;
        this.repository = repository;
        this.commitHash = commitHash;
        this.message = message;
        this.commitTimestamp = commitTimestamp;
    }

    // Getters and Setters
    public UUID getCommitId() {
        return commitId;
    }

    public void setCommitId(UUID commitId) {
        this.commitId = commitId;
    }

    public User getContributer() {
        return contributor;
    }

    public void setContributer(User contributer) {
        this.contributor = contributer;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCommitTimestamp() {
        return commitTimestamp;
    }

    public void setCommitTimestamp(Instant commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }
}