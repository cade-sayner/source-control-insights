package com.insights.client.source_control_insights_cli;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import io.jsonwebtoken.Jwts;

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
    File file = new File(fileName);
    return file;
  }

  private String getFileName() {
    String os = System.getProperty("os.name").toLowerCase();
    String fileName = os.contains("win")
      ? String.format("%s\\%s\\%s", this.homeDir, this.applicationDirInput, this.fileNameInput)
      : String.format("%s/%s/%s", this.homeDir, this.applicationDirInput, this.fileNameInput);
    return fileName;
  }

  private File getCliDirectory() {
    File cliDirectory = new File(String.format("%s/%s", this.homeDir, this.applicationDirInput));
    return  cliDirectory;
  }

  public void createConfigFile() {
    try {
      File file = getFile();
      File cliDirectory = getCliDirectory();
      getCliDirectory().mkdir();
      file.createNewFile();
    }catch(IOException e) {

   }
  }

  public void writeToConfigFile(String content) {
    try{
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(getFile()))) {
            bufferedWriter.write(content);
        }
    } catch(IOException e) {

    }
  }

  private String getToken() {
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
}
