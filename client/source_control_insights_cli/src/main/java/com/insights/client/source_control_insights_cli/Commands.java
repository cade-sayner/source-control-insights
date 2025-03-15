package com.insights.client.source_control_insights_cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.insights.client.source_control_insights_cli.Services.LoginService;
import com.insights.client.source_control_insights_cli.lib.AuthenticatedApiClient;
import com.insights.client.source_control_insights_cli.lib.CliClientFilesHelper;

@ShellComponent()
public class Commands {

    private final AuthenticatedApiClient authenticatedApiClient;
    private final LoginService loginService;

    public Commands(LoginService loginService, AuthenticatedApiClient authenticatedApiClient) {
        this.loginService = loginService;
        this.authenticatedApiClient = authenticatedApiClient;
    }

    @ShellMethod("Logs in to a user")
    public String login() {
        try {
            if(authenticatedApiClient.getJwt() != null) return "You are already logged in";
            CliClientFilesHelper cliClientFilesHelper = new CliClientFilesHelper(".insights","config");
            cliClientFilesHelper.createConfigFile();
            String token = loginService.login();
            // write the jwt to a local file, set the jwt on the authenticated client
            cliClientFilesHelper.writeToConfigFile(token);
            authenticatedApiClient.setJwt(token);
            return "Login successful";
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return "Something went wrong logging in";
        }
    }
    @ShellMethod("Prints the currently logged in user's jwt")
    public String jwt() {
        return authenticatedApiClient.getJwt();
    }

    @ShellMethod("Creates a repository")
    public String create_repo(){
        if(authenticatedApiClient.getJwt() == null) return "You must be logged in to access this command";
        return "Not yet implemented";
        
    }

}
