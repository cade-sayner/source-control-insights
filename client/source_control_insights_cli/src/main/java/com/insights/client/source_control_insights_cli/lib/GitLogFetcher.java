package com.insights.client.source_control_insights_cli.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitLogFetcher {

    public static String generateGitLogAsCSV(String repoPath) throws IOException {
        StringBuilder csvOutput = new StringBuilder();
       
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

    public static String getGitLogAsCSV(String repoPath) throws IOException{
        String logs = generateGitLogAsCSV(repoPath);
        String finalOutput = "";
        for(String line : logs.split("\n")){ 
            String commitHash = line.split(",")[0];
            String shortStat = getGitShortStat(commitHash, repoPath);
            finalOutput += line + "," + shortStat + "\n";
        }
        return finalOutput;
    }

    private static String getGitShortStat(String commitHash, String repoPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c","git show --shortstat " + commitHash);
            processBuilder.directory(new File(repoPath));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String lastLine = "";
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line; // Keep last non-empty line
            }
            process.waitFor();
            return lastLine.isEmpty() ? "No changes" : lastLine.strip();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
  
}

