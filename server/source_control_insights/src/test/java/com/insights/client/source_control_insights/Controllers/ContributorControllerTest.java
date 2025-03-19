package com.insights.client.source_control_insights.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

import com.insights.client.source_control_insights.Entities.Repository;
import com.insights.client.source_control_insights.Entities.User;
import com.insights.client.source_control_insights.Models.ActivitySummaryRepo;
import com.insights.client.source_control_insights.Repositories.RepoRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Services.ContributorService;

public class ContributorControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ContributorService contributorService;

    @Mock
    private RepoRepository repoRepository;

    @InjectMocks
    private ContributorController contributorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getContributor_UserFound() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");

        User user = new User();
        when(userRepository.findByGoogleId("user-id")).thenReturn(List.of(user));

        ResponseEntity<?> response = contributorController.getContributor(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getContributor_UserNotFound() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");

        when(userRepository.findByGoogleId("user-id")).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = contributorController.getContributor(jwt);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void getContributorActivityByRepo_RepoNotFound() {
        Jwt jwt = mock(Jwt.class);
        UUID repoId = UUID.randomUUID();
        User user = new User();
        user.setGoogleId("user-id");

        when(jwt.getClaim("sub")).thenReturn("user-id");
        when(userRepository.findByGoogleId("user-id")).thenReturn(List.of(user));
        when(repoRepository.findByContributorGoogleId("user-id")).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = contributorController.getContributorActivityByRepo(jwt, repoId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Repo not found", response.getBody());
    }

    @Test
    void getContributorActivityByRepo_Success() {
        Jwt jwt = mock(Jwt.class);
        UUID repoId = UUID.randomUUID();
        User user = new User();
        user.setGoogleId("user-id");
        Repository repo = new Repository();
        repo.setRepoId(repoId);

        ActivitySummaryRepo activitySummary = mock(ActivitySummaryRepo.class);

        when(jwt.getClaim("sub")).thenReturn("user-id");
        when(userRepository.findByGoogleId("user-id")).thenReturn(List.of(user));
        when(repoRepository.findByContributorGoogleId("user-id")).thenReturn(List.of(repo));
        when(contributorService.getActivitySummaryForRepo(user, repoId)).thenReturn(activitySummary);

        ResponseEntity<?> response = contributorController.getContributorActivityByRepo(jwt, repoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activitySummary, response.getBody());
    }
}
