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
import com.insights.client.source_control_insights_cli.lib.Commits;

public class CommandsTest {

    @Mock
    private AuthenticatedApiClient authenticatedApiClient;

    @Mock
    private LoginService loginService;

    @Mock
    private CliClientFilesHelper cliClientFilesHelper;

    @InjectMocks
    private Commands commands;

    @Mock
    private Commits commit;
   


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commands = new Commands(loginService, authenticatedApiClient, commit);
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
    public void testGetRepoActivity_Unauthorized() {
        when(authenticatedApiClient.getJwt()).thenReturn(null);

        String result = commands.getRepoActivity("repoId");

        assertEquals("You must be logged in to access this command", result);
    }
}
