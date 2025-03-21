package com.insights.client.source_control_insights_cli.lib;

import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandOutputs {

    private final ProcessOutput processOutput;

    @Autowired
    public CommandOutputs(ProcessOutput processOutput) {
        this.processOutput = processOutput;
    }

    public String getRepoUrl() {
        return processOutput.getCommand(List.of("git", "remote", "get-url", "origin")).strip();
    }

    public String getRepoName() {
        return Arrays.asList(processOutput.getCommand(List.of("git", "rev-parse", "--show-toplevel")).split("[/\\\\]")).getLast().strip();
    }

    public String getRepoLocalDirectory() {
        return processOutput.getCommand(List.of("git", "rev-parse", "--show-toplevel"));
    }
}
