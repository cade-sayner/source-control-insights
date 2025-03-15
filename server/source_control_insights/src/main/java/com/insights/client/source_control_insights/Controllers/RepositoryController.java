package com.insights.client.source_control_insights.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights.Repositories.RepoRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;



import com.insights.client.source_control_insights.Entities.Repository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;


@RestController
public class RepositoryController {
    private final RepoRepository repoRepository;

    public RepositoryController(RepoRepository repoRepository) {
        this.repoRepository = repoRepository;
    }

    @PostMapping("v1/createrepo/{name}")
    public ResponseEntity<?> postRepo(@PathVariable String name, @AuthenticationPrincipal Jwt jwt, @RequestBody String repo_url) {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized: JWT is missing.");
            }
            
            String sub = jwt.getClaim("sub");
            System.out.println("The sub is: " + sub);

            Repository repo = new Repository(name, "Github", sub, repo_url, java.time.Instant.now());
            repoRepository.save(repo);

            return ResponseEntity.ok("Repository created successfully.");

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(500).body("An error occurred while creating the repository.");
        }
    }
}
