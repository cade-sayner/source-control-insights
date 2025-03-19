package com.insights.client.source_control_insights_cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.insights.client.source_control_insights_cli.Services.LoginService;
import com.insights.client.source_control_insights_cli.lib.AuthenticatedApiClient;
import com.insights.client.source_control_insights_cli.lib.CliClientFilesHelper;

public class CommandsTest {

    @Mock
    private AuthenticatedApiClient authenticatedApiClient;

    @Mock
    private LoginService loginService;

    @Mock
    private CliClientFilesHelper cliClientFilesHelper;

    @InjectMocks
    private Commands commands;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commands = new Commands(loginService, authenticatedApiClient);
    }

    @Test
    void testLogin_Successful() throws Exception {
        when(cliClientFilesHelper.getToken()).thenReturn(null);
        when(loginService.login()).thenReturn("new-token");
        
        String result = commands.login();
        
        assertEquals("Login successful", result);
        verify(authenticatedApiClient).setJwt("new-token");
    }

    @Test
    void testJwt() {
        when(authenticatedApiClient.getJwt()).thenReturn("test-jwt");
        
        String result = commands.jwt();
        
        assertEquals("test-jwt", result);
    }

    @Test
    public void testLogout() {
        String result = commands.logout();

        assertEquals("Successfully logged out, type \"quit\" and return to quit cli :)", result);
        verify(authenticatedApiClient).setJwt("");
    }

    @Test
    public void testLogin_Exception() throws Exception {
        doThrow(new RuntimeException()).when(cliClientFilesHelper).createConfigFile();

        String result = commands.login();

        assertEquals("Something went wrong logging in", result);
    }
    
    @Test
    void testCreateRepo_Unauthorized() {
        when(authenticatedApiClient.getJwt()).thenReturn(null);
        
        String result = commands.createRepo("repoName", "repoUrl");
        
        assertEquals("You must be logged in to access this command", result);
    }
    
    @Test
    void testCreateRepo_Successful() {
        when(authenticatedApiClient.getJwt()).thenReturn("valid-jwt");
        
        String result = commands.createRepo("repoName", "repoUrl");
        
        assertEquals("Repository successfully created", result);
    }

    @Test
    public void testCreateRepo_Exception() throws Exception {
        when(authenticatedApiClient.getJwt()).thenReturn("jwtToken");
        doThrow(new RuntimeException()).when(authenticatedApiClient).createRepository(anyString(), anyString());

        String result = commands.createRepo("repoName", "repoUrl");

        assertEquals("Something went wrong creating a repository", result);
    }
    

    @Test
    public void testGetRepoActivity_Successful() throws Exception {
        when(authenticatedApiClient.getJwt()).thenReturn("jwtToken");
        String jsonResponse = "{\"totalCommits\": 10, \"activeDays\": 5, \"mostActiveDay\": \"Monday\", \"lastCommitDate\": \"2024-10-01\", \"commitVelocityPerDay\": 2.0, \"commitVelocityPerWeek\": 14.0, \"filesChanged\": 3, \"insertions\": 100, \"deletions\": 50, \"netChanges\":50}";

        when(authenticatedApiClient.getRepositoryActivity("repoId")).thenReturn(jsonResponse);

        String result = commands.getRepoActivity("repoId");

        assertTrue(result.contains("Total Commits     : 10"));
        assertTrue(result.contains("Active Days       : 5"));
        assertTrue(result.contains("Most Active Day   : Monday"));
        assertTrue(result.contains("Last Commit Date  : 2024-10-01"));
    }

    @Test
    public void testGetRepoActivity_Unauthorized() {
        when(authenticatedApiClient.getJwt()).thenReturn(null);

        String result = commands.getRepoActivity("repoId");

        assertEquals("You must be logged in to access this command", result);
    }
}
