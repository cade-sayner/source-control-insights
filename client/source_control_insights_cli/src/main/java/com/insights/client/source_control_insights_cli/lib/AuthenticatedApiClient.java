package com.insights.client.source_control_insights_cli.lib;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedApiClient {
    public final HttpClient client = HttpClient.newHttpClient();
    private String jwt;

    @Autowired
    private Environment environment;

    private HttpResponse<String> post(String endpoint, String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(environment.getProperty("google.redirect-uri")  + endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + jwt)
                    .POST(BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setJwt(String jwt){
        this.jwt = jwt;
    }

    public String getJwt(){
        return this.jwt;
    }

    private HttpResponse<String> get(String endpoint, HashMap<String, String> queryParams) {
        try {
            StringJoiner query = new StringJoiner("&");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                query.add(entry.getKey() + "=" + entry.getValue());
            }
            
            URI uri = URI.create(environment.getProperty("api.endpoint") +  endpoint + "?" + query.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + jwt)
                    .GET()
                    .build();

            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
