package com.insights.client.source_control_insights_cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent()
public class Commands {
    public final LoginService loginService;

    public Commands(LoginService loginService) {
        this.loginService = loginService;
    }

    @ShellMethod("Displays a greeting message")
    public String hello(@ShellOption(defaultValue = "world") String name) {
        return "Hello" + name;
    }

    @ShellMethod("Logs in a user")
    public String login() {
        try {
            CliClientFilesHelper cliClientFilesHelper = new CliClientFilesHelper(".insights","config");
            cliClientFilesHelper.createConfigFile();
            String token = loginService.login();
            cliClientFilesHelper.writeToConfigFile(token);
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong logging in";
        }
        return "Logged in successfully";
    }
}
