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

  public CliClientFilesHelper(String applicationDir, String fileName) {
    this.fileNameInput = fileName.toLowerCase();
    this.applicationDirInput = applicationDir.toLowerCase();
    this.homeDir = System.getProperty("user.home");
  }

  private File getFile() {
    String fileName = getFileName();
    return new File(fileName);
  }

  private String getFileName() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.contains("win")
      ? String.format("%s\\%s\\%s", this.homeDir, this.applicationDirInput, this.fileNameInput)
      : String.format("%s/%s/%s", this.homeDir, this.applicationDirInput, this.fileNameInput);
  }

  private File getCliDirectory() {
    return new File(String.format("%s/%s", this.homeDir, this.applicationDirInput));
  }

  public void createConfigFile() {
    try {
      File file = getFile();
      File cliDirectory = getCliDirectory();
      getCliDirectory().mkdir();
      file.createNewFile();
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
      return false;
    }
  }
}
