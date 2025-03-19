package com.insights.client.source_control_insights_cli.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.stereotype.Component;

import static com.insights.client.source_control_insights_cli.lib.CliClientFilesHelper.isGitRepo;

@Component
public class ProcessOutput {

    private String getCommandOutput(StringBuilder output, Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch(IOException _) {

        }
        return output.toString();
    }

    public String getCommand(List<String> command) {
        StringBuilder commandOutput = new StringBuilder();
        if(isGitRepo()) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                return getCommandOutput(commandOutput, process);
            } catch (IOException _) {
                return "That did not work, try again";
            }
        }
        return "That did not work, try again";
    }
}
