package com.insights.client.source_control_insights.Models;

public class RepoActivity{ 
    private int totalCommits;
    private float commitVelocityPerDay;
    private float commitVelocityPerWeek;
    private int activeDays;
    private String mostActiveDay;
    private String lastCommitDate;
    private int filesChanged;
    private int insertions;
    private int deletions;
    private int netChanges;

    public RepoActivity(int totalCommits, float commitVelocityPerDay, float commitVelocityPerWeek, 
    int activeDays, String mostActiveDay, String lastCommitDate, int filesChanged, int insertions,
    int deletions, int netChanges){
        this.totalCommits = totalCommits;
        this.commitVelocityPerDay = commitVelocityPerDay;
        this.activeDays = activeDays;
        this.mostActiveDay = mostActiveDay;
        this.lastCommitDate = lastCommitDate;
        this.filesChanged = filesChanged;
        this.insertions = insertions;
        this.deletions = deletions;
        this.netChanges = netChanges;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public float getCommitVelocityPerDay() {
        return commitVelocityPerDay;
    }

    public void setCommitVelocityPerDay(float commitVelocityPerDay) {
        this.commitVelocityPerDay = commitVelocityPerDay;
    }

    public float getCommitVelocityPerWeek() {
        return commitVelocityPerWeek;
    }

    public void setCommitVelocityPerWeek(float commitVelocityPerWeek) {
        this.commitVelocityPerWeek = commitVelocityPerWeek;
    }

    public int getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }

    public String getMostActiveDay() {
        return mostActiveDay;
    }

    public void setMostActiveDay(String mostActiveDay) {
        this.mostActiveDay = mostActiveDay;
    }

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    public void setLastCommitDate(String lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    public int getFilesChanged() {
        return filesChanged;
    }

    public void setFilesChanged(int filesChanged) {
        this.filesChanged = filesChanged;
    }

    public int getInsertions() {
        return insertions;
    }

    public void setInsertions(int insertions) {
        this.insertions = insertions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void setDeletions(int deletions) {
        this.deletions = deletions;
    }

    public int getNetChanges() {
        return netChanges;
    }

    public void setNetChanges(int netChanges) {
        this.netChanges = netChanges;
    }
}