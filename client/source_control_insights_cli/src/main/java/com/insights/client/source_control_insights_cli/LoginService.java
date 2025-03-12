package com.insights.client.source_control_insights_cli;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private Environment environment;
    public String login() throws Exception {

        String authCode = null;
        try {
            authCode = getAuthCode();
        } catch (Exception e) {
            System.out.println("Failed to get the auth code");
            System.out.println(e.toString());
            return authCode;
        }

        // Exchange the auth code for a jwt from the server
        String jsonBody = "{\"authCode\" : \"" + authCode + "\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(environment.getProperty("api.endpoint") + "/public/auth"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        String jwt = client.send(request, BodyHandlers.ofString()).body();
        return jwt;
    }

    private  String waitForAuthCode() throws IOException {
        ServerSocket server = new ServerSocket(3000);
        Socket client = server.accept();

        Scanner scanner = new Scanner(client.getInputStream(), StandardCharsets.UTF_8);
        String authCode = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.contains("code=")) {
                authCode = line.split("code=")[1].split("&")[0];
                break;
            }
        }
        String responseBody = "Login successful please return to the console application :)";
        String httpResponse = """
                              HTTP/1.1 200 OK\r
                              Content-Type: text/html\r
                              Content-Length: """ + responseBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseBody;

        OutputStream outputStream = client.getOutputStream();
        outputStream.write(httpResponse.getBytes());
        
        scanner.close();
        server.close();
        return authCode;
    }

    private String getAuthCode() throws Exception {
        String authUrl = "https://accounts.google.com/o/oauth2/auth"
                + "?client_id=" + System.getenv("OAUTH_CLIENT_ID")
                + "&redirect_uri=" + environment.getProperty("google.redirect-uri")
                + "&response_type=code"
                + "&scope=openid%20phone%20email%20profile";

        openBrowser(authUrl);
    
        return waitForAuthCode();
    }

    public void openBrowser(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                rt.exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                String[] browsers = {"xdg-open", "google-chrome", "firefox"};
                boolean browserFound = false;
                for (String browser : browsers) {
                    if (!browserFound) {
                        try {
                            rt.exec(new String[]{browser, url});
                            browserFound = true;
                        } catch (IOException e) {
                            // Try the next browser
                        }
                    }
                }
                if (!browserFound) {
                    throw new UnsupportedOperationException("No web browser found");
                }
            } else {
                throw new UnsupportedOperationException("Unsupported operating system");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
