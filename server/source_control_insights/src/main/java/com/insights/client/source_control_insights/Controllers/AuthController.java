package com.insights.client.source_control_insights.Controllers;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.insights.client.source_control_insights.Services.GoogleAuthService;
import com.insights.client.source_control_insights.Services.GoogleAuthService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights.Models.LoginRequestBody;
import com.insights.client.source_control_insights.Services.AuthService;

@RestController
public class AuthController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("public/auth")
    public ResponseEntity<String> getLogin(@RequestBody LoginRequestBody loginReq, GoogleAuthService googleAuthService) {
        try {
            String token = authService.login(loginReq, googleAuthService);
            return ResponseEntity.ok(token); 
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred."); 
        }
    }

    @GetMapping("public/jwks.json")
    public JsonNode getJwks() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/jwks.json");
        String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return objectMapper.readTree(content);
    }

    @GetMapping("/api/test-project_manager")
    public String getTestOwner() {
        return "Only a project manager should be seeing this";
    }

    @GetMapping("/api/test-developer")
    public String getTestContributor() {
        return "Any dev can see this";
    }
}
