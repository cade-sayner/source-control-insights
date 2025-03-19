package com.insights.client.source_control_insights.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Entities.Repository;
import com.insights.client.source_control_insights.Models.RepoActivity;
import com.insights.client.source_control_insights.Repositories.CommitRepository;
import com.insights.client.source_control_insights.Repositories.RepoRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Services.ContributorService;
import com.insights.client.source_control_insights.Services.RepositoryService;

public class RepoControllerTest {
    @Mock
    private RepoRepository repoRepository;

    @Mock
    private CommitRepository commitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContributorService contributorService;

    @Mock
    private RepositoryService repoService;

    @InjectMocks
    private RepositoryController repositoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void postRepo_Success() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");

        String repoName = "test-repo";
        String repoUrl = "https://github.com/test/repo";
        Repository mockRepo = new Repository(repoName, "GitHub", "user-id", repoUrl, Instant.now());

        when(repoRepository.save(any())).thenReturn(mockRepo);

        ResponseEntity<?> response = repositoryController.postRepo(repoName, jwt, repoUrl);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Repository created successfully.", response.getBody());
    }

    @Test
    void postRepo_Unauthorized() {
        ResponseEntity<?> response = repositoryController.postRepo("test-repo", null, "https://github.com/test/repo");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized: JWT is missing.", response.getBody());
    }

    @Test
    void getRepos_Success() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");

        List<Repository> mockRepos = List.of(new Repository());
        when(repoRepository.findByGoogleId("user-id")).thenReturn(mockRepos);

        ResponseEntity<?> response = repositoryController.getRepos(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRepos, response.getBody());
    }

    @Test
    void getRepos_Exception() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");
        when(repoRepository.findByGoogleId("user-id")).thenThrow(new RuntimeException());

        ResponseEntity<?> response = repositoryController.getRepos(jwt);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while trying to get your repos", response.getBody());
    }

    @Test
    void getLatestCommit_NoCommits() {
        UUID repoId = UUID.randomUUID();
        when(commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(repoId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = repositoryController.getLatestCommit(mock(Jwt.class), repoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"latestCommit\": 0}", response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void getLatestCommit_WithCommits() {
        UUID repoId = UUID.randomUUID();
        Commit commit = mock(Commit.class);
        when(commit.getCommitTimestamp()).thenReturn(Instant.now());
        when(commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(repoId)).thenReturn(List.of(commit));

        ResponseEntity<?> response = repositoryController.getLatestCommit(mock(Jwt.class), repoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("latestCommit"));
    }

    @Test
    void getRepositoryActivity_Success() {
        UUID repoId = UUID.randomUUID();
        List<Commit> commits = List.of(new Commit());

        RepoActivity repoActivity = mock(RepoActivity.class);

        when(commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(repoId)).thenReturn(commits);
        when(repoService.getRepoActivitySummary(commits)).thenReturn(repoActivity);

        ResponseEntity<?> response = repositoryController.getRepositoryActivity(repoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(repoActivity, response.getBody());
    }

    @Test
    void getRepoLeaderboard_InvalidSortBy() {
        UUID repoId = UUID.randomUUID();
        ResponseEntity<?> response = repositoryController.getRepoLeaderboard(repoId, "invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid sorting option.", response.getBody());
    }

    // @Test
    // void getRepoLeaderboard_Success() {
    //     UUID repoId = UUID.randomUUID();
    //     User user = mock(User.class);

    //     when(userRepository.findDistinctContributorsByRepositoryId(repoId)).thenReturn(List.of(user));

    //     LeaderBoardEntry entry = new LeaderBoardEntry(new ActivitySummary()); 

    //     when(contributorService.getActivityForRepo(any(User.class), eq(repoId))).thenReturn(entry);

    //     ResponseEntity<?> response = repositoryController.getRepoLeaderboard(repoId, "commits");

    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody() instanceof List);
    //     assertFalse(((List<?>) response.getBody()).isEmpty());
    //     assertEquals(entry, ((List<?>) response.getBody()).get(0));
    // }
}
