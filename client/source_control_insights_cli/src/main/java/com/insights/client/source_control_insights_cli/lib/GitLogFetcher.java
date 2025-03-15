package com.insights.client.source_control_insights_cli.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitLogFetcher {

    public static String getGitLogAsCSV(String repoPath) throws IOException {
        System.out.println(repoPath);
        StringBuilder csvOutput = new StringBuilder();
        csvOutput.append("Commit Hash,Author,Date,Message\n");

        ProcessBuilder processBuilder = new ProcessBuilder(
                "git", "log", "--pretty=format:%H,%an,%ad,%ae,%s", "--date=iso");
        processBuilder.directory(new File(repoPath));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                csvOutput.append(line).append("\n"); 
            }
        }

        return csvOutput.toString(); 
    }
  
}

