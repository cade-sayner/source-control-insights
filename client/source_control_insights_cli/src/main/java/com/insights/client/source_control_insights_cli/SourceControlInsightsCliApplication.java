package com.insights.client.source_control_insights_cli;

import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SourceControlInsightsCliApplication {

	public static void main(String[] args) {
		createInsightConfigDir();
		SpringApplication.run(SourceControlInsightsCliApplication.class, args);
	}

	public static void createInsightConfigDir() {
			try 
			{
				String homeDirName = String.format("%s/.insights",System.getProperty("user.home"));
				File homeDir = new File(homeDirName);
				if(!homeDir.exists()) 
				{
					homeDir.mkdir();
					String insightsConfig = String.format("%s/insights.config", homeDir);
					String insightsLog = String.format("%s/insights.log", homeDir);
					File insightsConfigFile = new File(insightsConfig);
					File insightsLogFile = new File(insightsLog);

					insightsConfigFile.createNewFile();
					insightsLogFile.createNewFile();
				}
			} catch (Exception e) {

			}
		}
	}

