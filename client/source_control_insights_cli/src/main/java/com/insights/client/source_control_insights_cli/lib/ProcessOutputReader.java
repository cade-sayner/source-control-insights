package com.insights.client.source_control_insights_cli.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessOutputReader {

    public static String getCommandOutput(StringBuilder output, Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch(IOException _) {

        }
        return output.toString();
    }
}
