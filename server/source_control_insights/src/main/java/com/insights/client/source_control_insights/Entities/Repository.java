package com.insights.client.source_control_insights.Entities;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Repository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses the DB's auto-increment
    @Column(name = "repository_id")
    private int repositoryId;
    
    @Column(name = "repository_name")
    private String repositoryName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commit> commits = new ArrayList<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Worklog> worklogs = new ArrayList<>();

    public int getRepositoryId(){
        return this.repositoryId;
    }

    public String getRepositoryName(){
        return this.repositoryName;
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }
    
    public void  setRepositoryId(int repositoryId){
        this.repositoryId = repositoryId;
    }

    public void setRepositoryName(String repositoryName){
        this.repositoryName = repositoryName;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}
