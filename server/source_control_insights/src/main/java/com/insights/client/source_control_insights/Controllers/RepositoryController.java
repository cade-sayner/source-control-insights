package com.insights.client.source_control_insights.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.insights.client.source_control_insights.Repositories.RepoRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Repositories.CommitRepository;
import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Entities.Repository;
import com.insights.client.source_control_insights.Entities.User;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class RepositoryController {

    private final RepoRepository repoRepository;
    private final CommitRepository commitRepository;
    private final UserRepository userRepository;
    public RepositoryController(RepoRepository repoRepository, CommitRepository commitRepository, UserRepository userRepository) {
        this.repoRepository = repoRepository;
        this.commitRepository = commitRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("v1/repos/{name}")
    public ResponseEntity<?> postRepo(@PathVariable String name, @AuthenticationPrincipal Jwt jwt, @RequestBody String repo_url) {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized: JWT is missing.");
            }
            String sub = jwt.getClaim("sub");
            Repository repo = new Repository(name, "Github", sub, repo_url, java.time.Instant.now());
            repoRepository.save(repo);
            return ResponseEntity.ok(repo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the repository.");
        }
    }

    @GetMapping("v1/repos")
    public ResponseEntity<?> getRepos(@AuthenticationPrincipal Jwt jwt) {
        try{
            List<Repository> repos;
            if(((List<String>)jwt.getClaim("scope")).contains("PROJ_MAN")){
                repos = repoRepository.findByGoogleId(jwt.getClaim("sub"));
            }else{ 
                // just a normal dev so just give them the repositories that they have commited to
                System.out.println("normal dev login");
                repos = repoRepository.findByContributorGoogleId(jwt.getClaim("sub"));
            }
            return ResponseEntity.ok(repos);
        }catch(Exception e){
            System.out.println(e);
            return ResponseEntity.status(500).body("An error occurred while trying to get your repos");
        }
    }

    @GetMapping("v1/repos/latest/{repoId}")
    public ResponseEntity<?> getLatestCommit(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID repoId){ 
        List<Commit> commits = commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(repoId);
        if(commits.isEmpty()){ 
            return ResponseEntity.ok("{\"latestCommit\": 0}");
        }
        else{
            return ResponseEntity.ok("{\"latestCommit\":" + "\"" + commits.get(0).getCommitTimestamp().atOffset(ZoneOffset.ofHours(2)) + "\"}");
        }
    }

    @PatchMapping("v1/repos/{repoId}")
    public ResponseEntity<?> patchRepos(@AuthenticationPrincipal Jwt jwt, @RequestBody String body, @PathVariable UUID repoId){ 
        try{    
            String[] rows = body.split("\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss X");
            List<Commit> commits = new ArrayList<>();
            for(String row : rows){ 
                // make a commit for this row
                String[] cols = row.split(",");
                String commitHash = cols[0];
                OffsetDateTime date = OffsetDateTime.parse(cols[2], formatter);
                String email = cols[3];
                String commitMessage = cols[4];
                List<User> contributorList = userRepository.findByEmail(email);
                List<Repository> repoList = repoRepository.findByRepoId(repoId);
                if(contributorList.isEmpty() || repoList.isEmpty()){ 
                    continue;
                }
                User user = contributorList.get(0);
                Repository repo = repoList.get(0);
                Commit commit = new Commit(user, repo, commitHash, commitMessage, date.toInstant());
                commitRepository.save(commit);
                commits.add(commit);
            }
            return ResponseEntity.ok(commits);
        }catch(Exception e){ 
            return ResponseEntity.status(500).body("Failed to update the repo");
        }
    }
}
