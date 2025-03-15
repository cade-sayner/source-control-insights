package com.insights.client.source_control_insights.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.Repository;

public interface RepoRepository extends JpaRepository<Repository, java.util.UUID> {
    List<Repository> findByRepoName(String name);
    List<Repository> findByGoogleId(String googleId);
    List<Repository> findByRepoId(UUID repoId);
}


