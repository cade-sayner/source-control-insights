package com.insights.client.source_control_insights.Models;

public class ActivitySummary {
    private String username;
    private int totalCommits;
    private float commitVelocityPerDay;
    private float commitVelocityPerWeek;
    private int activeDays;
    private String mostActiveDay;
    private String lastCommitDate;

    
        // Constructor
    public ActivitySummary() {}

    public ActivitySummary(String username, int totalCommits, float commitVelocityPerDay, 
                           float commitVelocityPerWeek, int activeDays, 
                           String mostActiveDay, String lastCommitDate) {
        this.username = username;
        this.totalCommits = totalCommits;
        this.commitVelocityPerDay = commitVelocityPerDay;
        this.commitVelocityPerWeek = commitVelocityPerWeek;
        this.activeDays = activeDays;
        this.mostActiveDay = mostActiveDay;
        this.lastCommitDate = lastCommitDate;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public float getCommitVelocityPerDay() {
        return commitVelocityPerDay;
    }

    public float getCommitVelocityPerWeek() {
        return commitVelocityPerWeek;
    }

    public int getActiveDays() {
        return activeDays;
    }

    public String getMostActiveDay() {
        return mostActiveDay;
    }

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public void setCommitVelocityPerDay(float commitVelocityPerDay) {
        this.commitVelocityPerDay = commitVelocityPerDay;
    }

    public void setCommitVelocityPerWeek(float commitVelocityPerWeek) {
        this.commitVelocityPerWeek = commitVelocityPerWeek;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }

    public void setMostActiveDay(String mostActiveDay) {
        this.mostActiveDay = mostActiveDay;
    }

    public void setLastCommitDate(String lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    // Optional: Override toString() for debugging
    @Override
    public String toString() {
        return "ActivitySummary{" +
                "username='" + username + '\'' +
                ", totalCommits=" + totalCommits +
                ", commitVelocityPerDay=" + commitVelocityPerDay +
                ", commitVelocityPerWeek=" +
                ", activeDays=" + activeDays +
                ", mostActiveDay='" + mostActiveDay + '\'' +
                ", lastCommitDate='" + lastCommitDate + '\'' +
                '}';
    }
}

