package com.insights.client.source_control_insights.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insights.client.source_control_insights.Entities.User;

public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    List<User> findByName(String username);
    List<User> findByGoogleId(String googleSub);
    List<User> findByEmail(String email);

    @Query("SELECT DISTINCT c.contributor FROM Commit c WHERE c.repository.repoId = :repoId")
    List<User> findDistinctContributorsByRepositoryId(@Param("repoId") UUID repoId);
}
