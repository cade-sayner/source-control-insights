package com.insights.client.source_control_insights.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights.Repositories.RepoRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Services.ContributorService;
import com.insights.client.source_control_insights.Services.RepositoryService;
import com.insights.client.source_control_insights.Repositories.CommitRepository;
import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Entities.Repository;
import com.insights.client.source_control_insights.Entities.User;
import com.insights.client.source_control_insights.Models.LeaderBoardEntry;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;

@RestController
public class RepositoryController {

    private final RepoRepository repoRepository;
    private final CommitRepository commitRepository;
    private final UserRepository userRepository;
    private final ContributorService contributorService;
    private final RepositoryService repoService;

    public RepositoryController(RepoRepository repoRepository, CommitRepository commitRepository, UserRepository userRepository, ContributorService contributerService, RepositoryService repoService) {
        this.repoRepository = repoRepository;
        this.commitRepository = commitRepository;
        this.userRepository = userRepository;
        this.contributorService = contributerService;
        this.repoService = repoService;
    }

    @PostMapping("v1/repo/{name}")
    public ResponseEntity<?> postRepo(@PathVariable String name, @AuthenticationPrincipal Jwt jwt, @RequestBody String repo_url) {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized: JWT is missing.");
            }
            String sub = jwt.getClaim("sub");
            Repository repo = new Repository(name, "Github", sub, repo_url, java.time.Instant.now());
            repoRepository.save(repo);
            return ResponseEntity.ok("Repository created successfully.");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(500).body("An error occurred while creating the repository.");
        }
    }

    @GetMapping("v1/repos")
    public ResponseEntity<?> getRepos(@AuthenticationPrincipal Jwt jwt) {
        try{
            List<Repository> repos = repoRepository.findByGoogleId(jwt.getClaim("sub"));
            return ResponseEntity.ok(repos);
        }catch(Exception e){
            return ResponseEntity.status(500).body("An error occurred while trying to get your repos");
        }
    }

    @GetMapping("v1/repos/latest/{repoId}")
    public ResponseEntity<?> getLatestCommit(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID repoId){ 
        List<Commit> commits = commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(repoId);
        if(commits.size() == 0){ 
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
            System.out.println("The rows are");
            System.out.println(Arrays.toString(rows));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss X");
            for(String row : rows){ 
                // make a commit for this row
                String[] cols = row.split(",");
                String commitHash = cols[0];
                OffsetDateTime date = OffsetDateTime.parse(cols[2], formatter);
                String email = cols[3];
                String commitMessage = cols[4];
                int filesChanged = 0;
                int insertions = 0;
                int deletions = 0;
                try{filesChanged = Integer.parseInt(cols[5].split(" ")[0]);}catch(Exception e){}
                try{insertions = Integer.parseInt(cols.length >= 7 ? cols[6].strip().split(" ")[0] : "0");}catch(Exception e){}
                try{deletions = Integer.parseInt(cols.length >= 8 ? cols[7].strip().split(" ")[0] : "0");}catch(Exception e){}
        
                List<User> contributorList = userRepository.findByEmail(email);
                List<Repository> repoList = repoRepository.findByRepoId(repoId);
                if(contributorList.isEmpty() || repoList.isEmpty()){ 
                    continue;
                }
                User user = contributorList.get(0);
                Repository repo = repoList.get(0);
                Commit commit = new Commit(user, repo, commitHash, commitMessage, date.toInstant(),filesChanged, insertions, deletions);
                commitRepository.save(commit);
            }
            return ResponseEntity.ok("You have successfully updated the repo's history");
        }catch(Exception e){ 
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update the repo");
        }
    }

    // @GetMapping("v1/repository/{repoId}/activity")
    // public ResponseEntity getRepositoryActivity(@PathVariable UUID repoId, @RequestParam String sortBy){ 
    //     List<Commit> commits = commitRespository.findByRepoId(repoId);
    //     repoService.
    // }

    @GetMapping("v1/repository/{repoId}/leaderboard")
    public ResponseEntity<?> getRepoLeaderboard(@PathVariable UUID repoId, @RequestParam String sortBy) {
        // get the users that belong to the repo
        // TODO: move this to a static somewhere
        Set<String> names = new HashSet<>(Set.of("commits", "days", "velocity_days", "velocity_weeks"));
        if(! names.contains(sortBy)) return ResponseEntity.status(400).body("Invalid sorting option.");

        var comparator = switch(sortBy){ 
            case "commits" -> LeaderBoardEntry.BY_TOTAL_COMMITS;
            case "days" -> LeaderBoardEntry.BY_COMMIT_DAYS;
            case "velocity_days" -> LeaderBoardEntry.BY_COMMIT_VELOCITY_DAYS;
            case "velocity_weeks" -> LeaderBoardEntry.BY_COMMIT_VELOCITY_WEEKS;
            default -> LeaderBoardEntry.BY_TOTAL_COMMITS;
        };

        List<User> users = userRepository.findDistinctContributorsByRepositoryId(repoId);
        for(User user : users){ 
            contributorService.getActivityForRepo(user, repoId);
        }

        List<LeaderBoardEntry> sortedLb = users.stream().map(u -> new LeaderBoardEntry(contributorService.getActivityForRepo(u, repoId))).sorted(comparator.reversed()).collect(Collectors.toList());
        for(int i = 0; i < sortedLb.size(); i++){ 
            sortedLb.get(i).ranking = i+1;
        }

        return ResponseEntity.ok(sortedLb);
    }
    
}
