package com.insights.client.source_control_insights.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights.Repositories.RoleRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class RepositoryController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RepositoryController(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/v1/createrepo/{name}")
    public String postRepo(@PathVariable String name) {
        return "";
    }
    
}
