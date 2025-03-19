package com.insights.client.source_control_insights_cli.lib;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthenticatedApiClient {


    public final HttpClient client = HttpClient.newHttpClient();
    private String jwt = "";

    @Autowired
    private Environment environment;

   
    public String createRepository(String name, String repoUrl) {
        return post("/v1/repos/" + name, repoUrl).body();
    }

    public String getRepositories() {
        return get("/v1/repos", new HashMap<>()).body();
    }

    public String getCommits(){ 
        return get("/v1/commits", new HashMap<>()).body();
    }

    public String getRepoLeaderboard(String repoId, String groupBy){ 
        var queryParams = new HashMap<String,String>();
        queryParams.put("sortBy", groupBy);
        return get("/v1/repository/" + repoId + "/leaderboard", queryParams).body();
    }

    public String getMe(){ 
        return get("/v1/contributor", new HashMap<>()).body();
    }

    public String getMyActivity(){ 
        return get("/v1/contributor/activity", new HashMap<>()).body();
    }


    public String getMyActivity(String repoId){ 
        return get("/v1/contributor/activity/" + repoId, new HashMap<>()).body();
    }

    public String getBreakdown(){ 
        return get("/v1/contributor/activity/breakdown", new HashMap<>()).body();
    }

    public String getBreakdown(String repoId){ 
        return get("/v1/contributor/activity/" + repoId + "/breakdown", new HashMap<>()).body();
    }

    public String getLatestCommitDate(String repoId) {
        return get("/v1/repos/latest/" + repoId, new HashMap<>()).body();
    }

    public String getRepositoryActivity(String repoId){ 
        return get("/v1/repository/" + repoId + "/activity", new HashMap<>()).body();
    }

    public String updateRepo(String repoId, String logCsv) throws Exception {
        String latestCommitResponse = getLatestCommitDate(repoId);
        ObjectMapper om = new ObjectMapper();
        String latestCommitDateString = om.readValue(latestCommitResponse, new TypeReference<Map<String, String>>() {
        }).get("latestCommit");
        OffsetDateTime latestCommitDate = latestCommitDateString.equals("0") ? OffsetDateTime.MIN
                : OffsetDateTime.parse(latestCommitDateString);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss X");
        List<String> rowSubset = new ArrayList<>();
        List<String> rows = List.of(logCsv.split("\n"));
        for (int i = 1; i < rows.size(); i++) {
            OffsetDateTime time = OffsetDateTime.parse(rows.get(i).split(",")[2], formatter);
            if (latestCommitDate.compareTo(time) < 0) {
                rowSubset.add(rows.get(i));
            }
        }
        if (rowSubset.isEmpty()) {
            return "The repository is up to date";
        }
        String body = String.join("\n", rowSubset);
        return patch("/v1/repos/" + repoId, body).body();
    }

    private HttpResponse<String> post(String endpoint, String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(environment.getProperty("api.endpoint") + endpoint))
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

    private HttpResponse<String> patch(String endpoint, String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(environment.getProperty("api.endpoint") + endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + jwt)
                    .method("PATCH", BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return this.jwt;
    }

    private HttpResponse<String> get(String endpoint, HashMap<String, String> queryParams) {
        try {
            StringJoiner query = new StringJoiner("&");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                query.add(entry.getKey() + "=" + entry.getValue());
            }

            URI uri = URI.create(environment.getProperty("api.endpoint") + endpoint + "?" + query.toString());
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

    public String getCommitCodeFrequency(String repoId, String code) {
        var queryParams = new HashMap<String, String>();
        queryParams.put("repoId", repoId);
        queryParams.put("code", code);
        return get("/api/commits/frequencies", queryParams).body();
    }
}
