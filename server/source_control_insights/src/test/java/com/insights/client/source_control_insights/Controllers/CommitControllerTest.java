package com.insights.client.source_control_insights.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Repositories.CommitRepository;

public class CommitControllerTest {
    @Mock
    private CommitRepository commitRepository;

    @InjectMocks
    private CommitController commitController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void getCommits_Successful() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");
        
        List<Commit> mockCommits = List.of(new Commit());
        when(commitRepository.findByContributor_GoogleId("user-id")).thenReturn(mockCommits);

        ResponseEntity<?> response = commitController.getCommits(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCommits, response.getBody());
    }

    @Test
    void getCommits_Failure() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("user-id");
        when(commitRepository.findByContributor_GoogleId("user-id")).thenThrow(new RuntimeException());

        ResponseEntity<?> response = commitController.getCommits(jwt);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occured while fetching the commits.", response.getBody());
    }
}
