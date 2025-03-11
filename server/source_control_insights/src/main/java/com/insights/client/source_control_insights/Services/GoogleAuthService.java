package com.insights.client.source_control_insights.Services;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleAuthService {
    public String getJWT(String authCode) throws Exception {
        String url = "https://oauth2.googleapis.com/token";
        String body = "code=" + URLDecoder.decode(authCode, StandardCharsets.UTF_8) + "&" +
                "client_id=" + System.getenv("OAUTH_CLIENT_ID") + "&" +
                "client_secret=" + System.getenv("OAUTH_CLIENT_SECRET") + "&" +
                "redirect_uri=" + "http://localhost:3000/oauth2callback" + "&" +
                "grant_type=" + "authorization_code";
        System.out.println("The body of the token request");
        System.out.println(body);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> resp = client.send(request, BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> map = objectMapper.readValue(resp.body(), new TypeReference<HashMap<String, String>>() {
        });

        return map.get("id_token");
    }
}
