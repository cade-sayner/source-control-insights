package com.insights.client.source_control_insights_cli;

import java.util.List;
import java.util.Map;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights_cli.Services.LoginService;
import com.insights.client.source_control_insights_cli.lib.AuthenticatedApiClient;
import com.insights.client.source_control_insights_cli.lib.CliClientFilesHelper;
import com.insights.client.source_control_insights_cli.lib.GitLogFetcher;

@ShellComponent()
public class Commands {

    private final AuthenticatedApiClient authenticatedApiClient;
    private final LoginService loginService;

    public Commands(LoginService loginService, AuthenticatedApiClient authenticatedApiClient) {
        this.loginService = loginService;
        this.authenticatedApiClient = authenticatedApiClient;
    }

    @ShellMethod("Logs in a user")
    public String login() {
        try {
            CliClientFilesHelper cliClientFilesHelper = new CliClientFilesHelper(".insights", "config");
            cliClientFilesHelper.createConfigFile();
            String token = cliClientFilesHelper.getToken();
            if(!token.equalsIgnoreCase("no token") && loginService.isValidToken(token)) {
                authenticatedApiClient.setJwt(token);
                return "You are already logged in";
            };
            if (authenticatedApiClient.getJwt() != null)
                return "You are already logged in";


            token = loginService.login();
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
    public String create_repo(@ShellOption(help = "The name of the repository") String name,
            @ShellOption(help = "The URL of the repository") String repoUrl) {
        if (authenticatedApiClient.getJwt() == null)
            return "You must be logged in to access this command";
        try{
            return "Repository successfully created";
        }catch(Exception e){ 
            return "Something went wrong creating a repository";
        }
    }

    @ShellMethod(value = "Gets your repos", key = "get-repos")
    public String getRepos() {
        if (authenticatedApiClient.getJwt() == null)
            return "You must be logged in to access this command";
        String repositoriesResponse = authenticatedApiClient.getRepositories();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var resp = objectMapper.readValue(repositoriesResponse, new TypeReference<List<Map<String, Object>>>() {
            });
            return "Your Repositories:\n" + printJsonArrayObject(resp);
        } catch (Exception e) {
            return "The response from the server was not valid json";
        }
    }

    @ShellMethod(value = "Updates the specified repo with the most up to date information at the git repo specified by the path provided", key = "update-repo")
    public String updateRepo(@ShellOption String repoId, @ShellOption(value = "-p") String path) {
        if (authenticatedApiClient.getJwt() == null)
            return "You must be logged in to access this command";
        try {
            return authenticatedApiClient.updateRepo(repoId, GitLogFetcher.getGitLogAsCSV(path));
        } catch (Exception e) {
            e.printStackTrace();
            return "File does not exist or something idk man do better";
        }
    }

    @ShellMethod(value = "Get the date of the latest commit to a repository", key = "get-latest-commit")
    public String getLatest(@ShellOption String repoId) {
        if (authenticatedApiClient.getJwt() == null)
            return "You must be logged in to access this command";
        return authenticatedApiClient.getLatestCommitDate(repoId);
    }

    public String printJsonArrayObject(List<Map<String, Object>> jsonObject) {
        String resultString = "";
        for (Map<String, Object> map : jsonObject) {
            for (String key : map.keySet()) {
                resultString += (key + ": " + map.get(key).toString() + "\n");
            }
            resultString += "\n";
        }
        return resultString;
    }
}
