package com.insights.client.source_control_insights.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.User;

public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    List<User> findByName(String username);
    List<User> findByGoogleId(String googleSub);
    List<User> findByEmail(String email);
}
