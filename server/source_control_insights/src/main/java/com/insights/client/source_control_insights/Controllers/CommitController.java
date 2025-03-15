package com.insights.client.source_control_insights.Controllers;

import org.springframework.web.bind.annotation.RestController;
import com.insights.client.source_control_insights.Repositories.CommitRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class CommitController {
    CommitRepository commitRepository;

    public CommitController(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @GetMapping("v1/commits")
    public ResponseEntity<?> getCommits(@AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(commitRepository.findByContributor_GoogleId(jwt.getClaim("sub")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occured while fetching the commits.");
        }
    }
}
