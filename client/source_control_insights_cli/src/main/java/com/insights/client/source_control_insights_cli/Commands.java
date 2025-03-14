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
            System.out.println("Your JWT" + loginService.login());
        } catch (Exception e) {
            System.out.println(e);
            return "Something went wrong logging in";
        }
        return "Logged in successfully";
    }

    private boolean validateJwt(String jwt) {
        return false;
    }
}
