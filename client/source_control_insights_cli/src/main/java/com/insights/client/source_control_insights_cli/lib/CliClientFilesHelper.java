package com.insights.client.source_control_insights_cli.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CliClientFilesHelper {

  private final String fileNameInput;
  private final String applicationDirInput;
  private final String homeDir;
  private final String userOperatingSystem;

  public CliClientFilesHelper(String applicationDir, String fileName) {
    this.fileNameInput = fileName.toLowerCase();
    this.applicationDirInput = applicationDir.toLowerCase();
    this.homeDir = System.getProperty("user.home");
    this.userOperatingSystem = System.getProperty("os.name").toLowerCase();
  }

  private File getFile() {
    return new File(getFileName());
  }

  private String getFileName() {
    return this.userOperatingSystem.contains("win")
      ? String.format("%s\\%s\\%s", this.homeDir, this.applicationDirInput, this.fileNameInput)
      : String.format("%s/%s/%s", this.homeDir, this.applicationDirInput, this.fileNameInput);
  }

  private File getCliDirectory() {
    return this.userOperatingSystem.contains("win")
            ? new File(String.format("%s\\%s", this.homeDir, this.applicationDirInput))
            : new File(String.format("%s/%s", this.homeDir, this.applicationDirInput));
  }

  public void createConfigFile() {
    try {
      getCliDirectory().mkdir();
      getFile().createNewFile();
    }catch(IOException _) {

   }
  }

  public void writeToConfigFile(String content) {
    try{
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(getFile()))) {
            bufferedWriter.write(content);
        }
    } catch(IOException _) {

    }
  }

  public String getToken() {
    String filePath = getFileName();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      StringBuilder stringBuilder = new StringBuilder();
      while ((line = br.readLine()) != null) {
        stringBuilder.append(line);
      }
      return stringBuilder.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return "No token";
    }
  }

  public static boolean isGitRepo() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("git", "--version");
      processBuilder.start();
      return true;
    } catch(IOException _) {
      System.out.println("This is not a git repo, ensure you are in a git repo before running this command.");
      return false;
    }
  }
}
