package com.insights.client.source_control_insights.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
    List<AppUser> findByUsername(String username);
    List<AppUser> findByGoogleSub(String googleSub);
    List<AppUser> findByEmail(String email);
}
