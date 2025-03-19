package com.insights.client.source_control_insights_cli;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.lang.reflect.Method;
import java.util.Arrays;

import jakarta.annotation.PostConstruct;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights_cli.Services.LoginService;
import com.insights.client.source_control_insights_cli.lib.AuthenticatedApiClient;
import com.insights.client.source_control_insights_cli.lib.CliClientFilesHelper;
import com.insights.client.source_control_insights_cli.lib.GitLogFetcher;
import com.insights.client.source_control_insights_cli.lib.Commits;

@ShellComponent()
public class Commands {

    private final AuthenticatedApiClient authenticatedApiClient;
    private final LoginService loginService;
    private final Commits commits;
    private final CliClientFilesHelper cliClientFilesHelper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token = "";

    public Commands(LoginService loginService, AuthenticatedApiClient authenticatedApiClient, Commits commits) {
        this.loginService = loginService;
        this.authenticatedApiClient = authenticatedApiClient;
        this.commits = commits;
        this.cliClientFilesHelper = new CliClientFilesHelper(".insights", "config");
    }

    @PostConstruct
    public void checkForJwt() {
        this.cliClientFilesHelper.createConfigFile();
        this.token = loginService.isValidToken(this.cliClientFilesHelper.getToken())
                ? this.cliClientFilesHelper.getToken() : "";
        this.authenticatedApiClient.setJwt(this.token);
    }

    @ShellMethod("Logs in a user")
    public String login() {
        try {
            if(loginService.isValidToken(token)) {
                authenticatedApiClient.setJwt(token);
                return "You are already logged in";
            };
            this.token = loginService.login();
            // write the jwt to a local file, set the jwt on the authenticated client
            this.cliClientFilesHelper.writeToConfigFile(this.token);
            authenticatedApiClient.setJwt(this.token);
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

    @ShellMethod(value = "Creates a repository (-n <name>, -u <URL>)", key = "create-repo")
    public String createRepo(@ShellOption(value = "-n", help = "The name of the repository") String name,
            @ShellOption(value = "-u", help = "The URL of the repository") String repoUrl) {
        if (!loginService.isValidToken(this.token))
            return "You must be logged in to access this command";
        try {
            authenticatedApiClient.createRepository(name, repoUrl);
            return "Repository successfully created";
        } catch (Exception e) {
            return "Something went wrong creating a repository";
        }
    }

    @ShellMethod(value = "Gets the repository activity (-r <repoId>)", key = "repo-activity")
    public String getRepoActivity(@ShellOption(value = "-r", help = "The repository id") String repoId) {
        if (!loginService.isValidToken(this.token))
            return "You must be logged in to access this command";
        try {
            String jsonResponse = authenticatedApiClient.getRepositoryActivity(repoId);
            JsonNode json = objectMapper.readTree(jsonResponse);
            return String.format(
                    "========================================\n" +
                            "        REPOSITORY ACTIVITY REPORT         \n" +
                            "========================================\n" +
                            " Total Commits     : %-5d\n" +
                            " Active Days       : %-5d\n" +
                            " Most Active Day   : %-10s\n" +
                            " Last Commit Date  : %-19s\n" +
                            "----------------------------------------\n" +
                            " Commit Velocity\n" +
                            "----------------------------------------\n" +
                            " Per Day          : %-4.1f\n" +
                            " Per Week         : %-4.1f\n" +
                            "----------------------------------------\n" +
                            " Code Changes\n" +
                            "----------------------------------------\n" +
                            " Files Changed     : %-5d\n" +
                            " Insertions        : %-6d\n" +
                            " Deletions         : %-5d\n" +
                            " Net Changes       : %+6d\n" +
                            "========================================\n",
                    json.get("totalCommits").asInt(),
                    json.get("activeDays").asInt(),
                    json.get("mostActiveDay").asText(),
                    json.get("lastCommitDate").asText(),
                    json.get("commitVelocityPerDay").asDouble(),
                    json.get("commitVelocityPerWeek").asDouble(),
                    json.get("filesChanged").asInt(),
                    json.get("insertions").asInt(),
                    json.get("deletions").asInt(),
                    json.get("netChanges").asInt());
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing JSON data.";
        }
    }

    @ShellMethod(value = "Gets commit breakdown grouped by date (-r <repoId>)", key = "my-breakdown")
    public String getBreakdown(
            @ShellOption(value = "-r", defaultValue = "ALL", help = "The repositories ID") String repoId) {
        if (!loginService.isValidToken(this.token))
            return "You must be logged in to access this command";

        StringBuilder output = new StringBuilder();
        try {
            String jsonResponse = null;
            if (repoId.equals("ALL")) {
                jsonResponse = authenticatedApiClient.getBreakdown();
            } else {
                jsonResponse = authenticatedApiClient.getBreakdown(repoId);
            }

            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            Map<String, Integer> commits = new TreeMap<>();
            rootNode.fields().forEachRemaining(entry -> commits.put(entry.getKey(), entry.getValue().asInt()));

            int maxCommits = commits.values().stream().max(Integer::compareTo).orElse(1);
            int scaleFactor = Math.max(1, maxCommits / 50); 

            output.append("\nCommits Over Time\n");
            output.append("-----------------\n");

            for (Map.Entry<String, Integer> entry : commits.entrySet()) {
                String date = entry.getKey();
                int count = entry.getValue();
                String bar = "#".repeat(count / scaleFactor); 
                output.append(String.format("%s | %s (%d)\n", date, bar, count));
            }
        } catch (Exception e) {
            return "Error parsing JSON data.";
        }
        return output.toString();
    }

    @ShellMethod(value = "Gets your repos", key = "my-repos")
    public String getRepos() {
        if (!loginService.isValidToken(this.token))
            return "You must be logged in to access this command";

        String repositoriesResponse = authenticatedApiClient.getRepositories();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            var resp = objectMapper.readValue(repositoriesResponse, new TypeReference<List<Map<String, Object>>>() {
            });
            StringBuilder sb = new StringBuilder();

            sb.append("+---------------------------------------------------------+\n")
                    .append("|                      Your Repositories                  |\n")
                    .append("+---------------------------------------------------------+\n");

            for (var repo : resp) {
                sb.append("Repo Name   : ").append(repo.get("repoName")).append("\n")
                        .append("Repo ID     : ").append(repo.get("repoId")).append("\n")
                        .append("Repo URL    : ").append(repo.get("repoUrl")).append("\n")
                        .append("Provider    : ").append(repo.get("provider")).append("\n")
                        .append("Created At  : ").append(repo.get("createdAt")).append("\n")
                        .append("+---------------------------------------------------------+\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return "The response from the server was not valid JSON.";
        }
    }

    @ShellMethod(value = "Gets your activity summary (-r <repoId>)", key = "my-activity")
    public String getActivity(@ShellOption(value = "-r", defaultValue = "ALL") String repoId) {
        if (!loginService.isValidToken(this.token))
            return "ERROR: You must be logged in to access this command.";

        try {
            String jsonResponse = null;
            if (repoId.equals("ALL")) {
                jsonResponse = authenticatedApiClient.getMyActivity();
            } else {
                jsonResponse = authenticatedApiClient.getMyActivity(repoId);
            }
            JsonNode json = objectMapper.readTree(jsonResponse);

            return String.format(
                    "========================================\n" +
                            "        USER ACTIVITY REPORT         \n" +
                            "========================================\n" +
                            " Username          : %-15s\n" +
                            "----------------------------------------\n" +
                            " Total Commits     : %-5d\n" +
                            " Active Days       : %-5d\n" +
                            " Most Active Day   : %-10s\n" +
                            " Last Commit Date  : %-19s\n" +
                            "----------------------------------------\n" +
                            " Commit Velocity\n" +
                            "----------------------------------------\n" +
                            " Per Day          : %-4.1f\n" +
                            " Per Week         : %-4.1f\n" +
                            "----------------------------------------\n" +
                            " Code Changes\n" +
                            "----------------------------------------\n" +
                            " Files Changed     : %-5d\n" +
                            " Insertions        : %-6d\n" +
                            " Deletions         : %-5d\n" +
                            " Net Changes       : %+6d\n" +
                            "========================================\n",
                    json.get("username").asText(),
                    json.get("totalCommits").asInt(),
                    json.get("activeDays").asInt(),
                    json.get("mostActiveDay").asText(),
                    json.get("lastCommitDate").asText(),
                    json.get("commitVelocityPerDay").asDouble(),
                    json.get("commitVelocityPerWeek").asDouble(),
                    json.get("filesChanged").asInt(),
                    json.get("insertions").asInt(),
                    json.get("deletions").asInt(),
                    json.get("netChanges").asInt());
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Could not fetch profile. Please try again.";
        }
    }

    @ShellMethod(value = "Gets the information pertaining to the currently signed in user", key = "whoami")
    public String me() {
        if (!loginService.isValidToken(this.token))
            return "ERROR: You must be logged in to access this command.";

        try {
            String jsonResponse = authenticatedApiClient.getMe(); 

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode user = objectMapper.readTree(jsonResponse);

            StringBuilder output = new StringBuilder();
            output.append("Your Profile:\n")
                    .append("========================================\n")
                    .append(String.format("Username : %s\n", user.get("username").asText()))
                    .append(String.format("Email    : %s\n", user.get("email").asText()))
                    .append(String.format("Google ID: %s\n", user.get("googleId").asText()))
                    .append("\nRoles:\n");

            for (JsonNode role : user.get("roles")) {
                output.append(String.format("  - %s: %s\n",
                        role.get("roleName").asText(),
                        role.get("roleDescription").asText()));
            }

            output.append("========================================");

            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Could not fetch profile. Please try again.";
        }
    }

    @ShellMethod(value = "Gets the leaderboard for the specified repository, (-r <repoId> -o <orderBy>)", key = "repo-leaderboard")
    public String getLeaderboard(@ShellOption(value = "-r") String repoId, @ShellOption(value="-o") String orderBy) {
        if (!loginService.isValidToken(this.token))
            return "ERROR: You must be logged in to access this command.";
        try {
            String jsonResponse = authenticatedApiClient.getRepoLeaderboard(repoId, orderBy); 

            StringBuilder output = new StringBuilder();
        output.append("===================================================================\n");
        output.append("                            LEADERBOARD                            \n");
        output.append("===================================================================\n");
        output.append(String.format("%-5s %-20s %-10s %-10s %-10s %-10s\n",
                "Rank", "Username", "Commits", "Days", "Per Day", "Per Week"));
        output.append("-------------------------------------------------------------------\n");
        JsonNode jsonArray = objectMapper.readTree(jsonResponse);
        for (JsonNode user : jsonArray) {
            output.append(String.format("%-5d %-20s %-10d %-10d %-10.1f %-10.1f\n",
                    user.get("ranking").asInt(),
                    user.get("username").asText(),
                    user.get("totalCommits").asInt(),
                    user.get("commitDays").asInt(),
                    user.get("commitVelocityPerDay").asDouble(),
                    user.get("commitVelocityPerWeek").asDouble()));
        }

        output.append("===================================================================\n");
        return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Could not fetch profile. Please try again.";
        }
    }

    @ShellMethod(value = "Gets the commits that belong to the signed-in user", key = "my-commits")
    public String myCommits() {
        if (!loginService.isValidToken(this.token))
            return "ERROR: You must be logged in to access this command.";

        try {
            String jsonResponse = authenticatedApiClient.getCommits();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode commits = objectMapper.readTree(jsonResponse);

            if (commits.isEmpty())
                return "INFO: No commits found for the current user.";

            StringBuilder output = new StringBuilder();
            output.append("Your Commits:\n")
                    .append("=================================================\n");

            for (JsonNode commit : commits) {
                output.append(String.format("[ %s ] %s\n",
                        commit.get("commitHash").asText(),
                        commit.get("message").asText()))
                        .append(String.format("  Time: %s | Files Changed: %d | Insertions: \u001B[32m %d \u001B[0m | Deletions: \u001B[31m %d \u001B[0m\n",
                                commit.get("commitTimestamp").asText(),
                                commit.get("filesChanged").asInt(),
                                commit.get("insertions").asInt(),
                                commit.get("deletions").asInt()))
                        .append("-------------------------------------------------\n");
            }

            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Could not fetch commits. Please try again.";
        }
    }

    @ShellMethod(value = "Updates the specified repo with the most up to date information at the git repo specified by the path provided", key = "update-repo")
    public String updateRepo(@ShellOption(value="-r") String repoId, @ShellOption(value = "-p") String path) {
        if (!loginService.isValidToken(this.token))
            return "You must be logged in to access this command";
        try {
            if (!loginService.isValidToken(this.token))
                return "You must be logged in to access this command";
            return authenticatedApiClient.updateRepo(repoId, GitLogFetcher.getGitLogAsCSV(path));
        } catch (Exception e) {
            e.printStackTrace();
            return "File does not exist or something idk man do better";
        }
    }

    @ShellMethod(value = "Get the date of the latest commit to a repository", key = "latest-commit")
    public String getLatest(@ShellOption(value = "-r") String repoId) {
        if (!loginService.isValidToken(this.token))
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

    @ShellMethod(value = "Get the frequency of commit codes", key = "commit-code")
    public String getCommitCodeFrequency(@ShellOption(value = "-r") String repoId, @ShellOption(value = "-c", defaultValue = ShellOption.NULL) String code) {
        if (code == null) {
            code = "";
        }
        if (authenticatedApiClient.getJwt() == null)
            return "You must be logged in to access this command";
        return authenticatedApiClient.getCommitCodeFrequency(repoId, code);
    }

    @ShellMethod(key = "logout", value = "Clears user token for them to authenticate again next time they login")
    public String logout() {
        this.cliClientFilesHelper.writeToConfigFile("");
        this.authenticatedApiClient.setJwt("");
        this.token = "";
        return "Successfully logged out, type \"quit\" and return to quit cli :)";
    }

    @ShellMethod("Displays a list of all available commands")
    public String help() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("Available Commands:\n");
        
        // Get all methods annotated with @ShellMethod
        Method[] methods = Commands.class.getDeclaredMethods();
        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(ShellMethod.class)) 
                .forEach(method -> {
                    ShellMethod shellMethod = method.getAnnotation(ShellMethod.class);
                    helpText.append(String.format("%-20s : %s\n", shellMethod.key()[0], shellMethod.value()));
                });

        return helpText.toString();
    }
}
