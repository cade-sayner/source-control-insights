package com.insights.client.source_control_insights.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByRoleName(String name);
}
