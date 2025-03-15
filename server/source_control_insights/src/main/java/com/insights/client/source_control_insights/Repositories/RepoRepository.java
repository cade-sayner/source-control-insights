package com.insights.client.source_control_insights.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insights.client.source_control_insights.Entities.Repository;

public interface RepoRepository extends JpaRepository<Repository, java.util.UUID> {
    List<Repository> findByRepoName(String name);
    List<Repository> findByGoogleId(String googleId);
    List<Repository> findByRepoId(UUID repoId);

    @Query("SELECT DISTINCT r FROM Repository r JOIN r.commits c WHERE c.contributor.googleId = :userId")
    List<Repository> findByContributorGoogleId(@Param("userId") String googleId);
}


