package com.insights.client.source_control_insights.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.Repository;

public interface RepoRepository extends JpaRepository<Repository, java.util.UUID> {
    List<Repository> findByRepoName(String name);
}
