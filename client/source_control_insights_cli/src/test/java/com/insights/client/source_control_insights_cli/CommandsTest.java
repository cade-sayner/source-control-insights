package com.insights.client.source_control_insights_cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void testGetRepoActivityException() throws Exception {

        when(authenticatedApiClient.getJwt()).thenReturn("jwtToken");

        doThrow(new RuntimeException()).when(authenticatedApiClient).getRepositoryActivity(anyString());


        String result = commands.getRepoActivity("repoId");


        assertEquals("Error parsing JSON data.", result);

    }

    @Test
    public void testLoginAlreadyLoggedIn() throws Exception {
        when(cliClientFilesHelper.getToken()).thenReturn("validToken");
        when(loginService.isValidToken("validToken")).thenReturn(true);

        String result = commands.login();

        assertEquals("You are already logged in", result);
        verify(authenticatedApiClient).setJwt("validToken");

    }

    @Test
    public void testGetRepoActivitySuccess() throws Exception {
        when(authenticatedApiClient.getJwt()).thenReturn("jwtToken");
        String jsonResponse = "{\"totalCommits\": 10, \"activeDays\": 5, \"mostActiveDay\": \"Monday\", \"lastCommitDate\": \"2023-10-01\", \"commitVelocityPerDay\": 2.0, \"commitVelocityPerWeek\": 14.0, \"filesChanged\": 3, \"insertions\": 100, \"deletions\": 50, \"netChanges\":50}";

        when(authenticatedApiClient.getRepositoryActivity("repoId")).thenReturn(jsonResponse);

        String result = commands.getRepoActivity("repoId");

        assertEquals("========================================\n" +
                "        REPOSITORY ACTIVITY REPORT         \n" +
                "========================================\n" +
                " Total Commits     : 10   \n" +
                " Active Days       : 5    \n" +
                " Most Active Day   : Monday    \n" +
                " Last Commit Date  : 2023-10-01         \n" +
                "----------------------------------------\n" +
                " Commit Velocity\n" +
                "----------------------------------------\n" +
                " Per Day          : 2.0 \n" +
                " Per Week         : 14.0\n" +
                "----------------------------------------\n" +
                " Code Changes\n" +
                "----------------------------------------\n" +
                " Files Changed     : 3    \n" +
                " Insertions        : 100   \n" +
                " Deletions         : 50   \n" +
                " Net Changes       :    +50\n" +
                "========================================\n", result);

    }

    // Add more tests for other methods and scenarios
}
