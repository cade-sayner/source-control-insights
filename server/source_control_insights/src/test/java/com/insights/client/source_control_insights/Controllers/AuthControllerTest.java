package com.insights.client.source_control_insights.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights.Models.LoginRequestBody;
import com.insights.client.source_control_insights.Services.AuthService;
import com.insights.client.source_control_insights.Services.GoogleAuthService;

public class AuthControllerTest {
   @Mock
   private AuthService authService;
   
   @InjectMocks
   private AuthController authController;

   @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
   }

   @Test
   void getLogin_Successful() throws Exception {
        LoginRequestBody loginRequestBody = new LoginRequestBody();
        GoogleAuthService googleAuthService = mock(GoogleAuthService.class);

        when(authService.login(loginRequestBody, googleAuthService)).thenReturn("mocked-token");

        ResponseEntity<String> response = authController.getLogin(loginRequestBody, googleAuthService);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mocked-token", response.getBody());
   }

   @Test
    void getLogin_Unauthorized() throws Exception{
        LoginRequestBody loginRequest = new LoginRequestBody();
        GoogleAuthService googleAuthService = mock(GoogleAuthService.class);

        when(authService.login(loginRequest, googleAuthService)).thenThrow(new IllegalArgumentException("Invalid credentials"));

        ResponseEntity<String> response = authController.getLogin(loginRequest, googleAuthService);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void getJwks_ReturnsJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("static/jwks.json");
        String expectedJson = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        JsonNode expectedNode = objectMapper.readTree(expectedJson);

        JsonNode result = authController.getJwks();

        assertEquals(expectedNode, result);
    }

    @Test
    void getTestOwner_ReturnsCorrectMessage() {
        assertEquals("Only a project manager should be seeing this", authController.getTestOwner());
    }

    @Test
    void getTestContributor_ReturnsCorrectMessage() {
        assertEquals("Any dev can see this", authController.getTestContributor());
    }
}
