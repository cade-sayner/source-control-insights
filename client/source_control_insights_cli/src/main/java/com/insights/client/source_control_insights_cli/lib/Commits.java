package com.insights.client.source_control_insights_cli.lib;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Commits {

    private final ProcessOutput processOutput;

    @Autowired
    public Commits(ProcessOutput processOutput) {
        this.processOutput = processOutput;
    }

    public String getRepoUrl() {
        return processOutput.getCommand(List.of("git", "remote", "get-url", "origin"));
    }

    public String getUserLocalRepoRoot() { return processOutput.getCommand(List.of("git", "rev-parse", "--show-toplevel")); }
}
