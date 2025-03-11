package com.insights.client.source_control_insights.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.User;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByUsername(String username);

    List<User> findByEmail(String email);
}
