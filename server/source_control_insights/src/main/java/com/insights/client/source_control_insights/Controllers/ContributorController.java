package com.insights.client.source_control_insights.Controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.insights.client.source_control_insights.Entities.Repository;
import com.insights.client.source_control_insights.Entities.User;
import com.insights.client.source_control_insights.Repositories.RepoRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Services.ContributorService;

@RestController
public class ContributorController {

    private final UserRepository userRepository;
    private final ContributorService contributorService;
    private final RepoRepository repoRepository;

    public ContributorController(UserRepository userRepository, ContributorService contributorService, RepoRepository repoRepository){ 
        this.userRepository = userRepository;
        this.contributorService = contributorService;
        this.repoRepository = repoRepository;
    }

    @GetMapping("v1/contributor/activity")
    public ResponseEntity<?> getContributorActivity(@AuthenticationPrincipal Jwt jwt) {
        List<User> userList = userRepository.findByGoogleId(jwt.getClaim("sub"));
        if(userList.isEmpty()){ 
            return ResponseEntity.status(500).body("User not found");
        }
        return ResponseEntity.ok(contributorService.getActivitySummary(userList.getFirst()));
    }

    @GetMapping("v1/contributor/activity/{repoId}")
    public ResponseEntity<?> getContributorActivityByRepo(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID repoId) {
        List<User> userList = userRepository.findByGoogleId(jwt.getClaim("sub"));
        if(userList.isEmpty()){ 
            return ResponseEntity.status(500).body("User not found");
        }
        User user = userList.getFirst();
        List<Repository> repositories = repoRepository.findByContributorGoogleId(user.getGoogleId());
        if(repositories.stream().filter(repo -> repo.getRepoId().equals(repoId)).collect(Collectors.toList()).isEmpty()){ 
            return ResponseEntity.status(404).body("Repo not found");
        }
        return ResponseEntity.ok(contributorService.getActivitySummaryForRepo(user, repoId));
    }

    @GetMapping("v1/contributor/activity/breakdown")
    public ResponseEntity<?> getContributorActivityByDay(@AuthenticationPrincipal Jwt jwt) {
        List<User> userList = userRepository.findByGoogleId(jwt.getClaim("sub"));
        if(userList.isEmpty()){ 
            return ResponseEntity.status(500).body("User not found");
        }
        User user = userList.getFirst();
        return ResponseEntity.ok(contributorService.getActivityByDay(user));
    }

    @GetMapping("v1/contributor/activity/{repoId}/breakdown")
    public ResponseEntity<?> getContributorActivityRepoByDay(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID repoId) {
        List<User> userList = userRepository.findByGoogleId(jwt.getClaim("sub"));
        if(userList.isEmpty()){ 
            return ResponseEntity.status(500).body("User not found");
        }
        User user = userList.getFirst();

        return ResponseEntity.ok(contributorService.getActivityRepoByDay(user, repoId));
    }
    
}
