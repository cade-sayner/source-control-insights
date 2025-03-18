package com.insights.client.source_control_insights.Entities;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @Column(unique = true, nullable = false)
    private String commitHash;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant commitTimestamp;

    @Column(name = "files_changed")
    private int filesChanged;

    @Column(name = "insertions")
    private int insertions;

    @Column(name = "deletions")
    private int deletions;

    public int getFilesChanged(){
        return this.filesChanged;
    }

    public void setFilesChanged(int filesChanged){
        this.filesChanged = filesChanged;
    }

    public int getInsertions(){
        return insertions;
    }

    public void setInsertions(int insertions){
        this.insertions = insertions;
    }

    public int getDeletions(){
        return this.deletions;
    }

    public void setDeletions(int deletions){
        this.deletions = deletions;
    }

    // Constructors
    public Commit() {}

    public Commit(User contributer, Repository repository, String commitHash, String message, Instant commitTimestamp, int filesChanged, int insertions, int deletions) {
        this.contributor = contributer;
        this.repository = repository;
        this.commitHash = commitHash;
        this.message = message;
        this.commitTimestamp = commitTimestamp;
        this.filesChanged = filesChanged;
        this.insertions = insertions;
        this.deletions = deletions;
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