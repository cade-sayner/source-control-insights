package com.insights.client.source_control_insights_cli.lib;


import static com.insights.client.source_control_insights_cli.lib.CliClientFilesHelper.isGitRepo;
import static com.insights.client.source_control_insights_cli.lib.ProcessOutputReader.getCommandOutput;

import java.io.IOException;

public class Commits {

    public String getUserRepoUrl() {
        StringBuilder commandOutput = new StringBuilder();
        if(isGitRepo()) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("git", "remote", "get-url", "origin");
                Process process = processBuilder.start();
                return getCommandOutput(commandOutput, process);
            } catch (IOException _) {
                return "That did not work, try again";
            }
        }
        return "That did not work, try again";
    }
}
