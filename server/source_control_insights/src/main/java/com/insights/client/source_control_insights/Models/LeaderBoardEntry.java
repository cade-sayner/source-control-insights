package com.insights.client.source_control_insights.Models;

import java.util.Comparator;

public class LeaderBoardEntry {
    public int ranking;
    public String username;
    public int totalCommits;
    public int commitDays;
    public float commitVelocityPerDay;
    public float commitVelocityPerWeek;

    public static final Comparator<LeaderBoardEntry> BY_TOTAL_COMMITS = Comparator.comparing(LeaderBoardEntry::getTotalCommits);
    public static final Comparator<LeaderBoardEntry> BY_COMMIT_DAYS = Comparator.comparing(LeaderBoardEntry::getCommitDays);
    public static final Comparator<LeaderBoardEntry> BY_COMMIT_VELOCITY_DAYS = Comparator.comparing(LeaderBoardEntry::getCommitVelocityPerDay);
    public static final Comparator<LeaderBoardEntry> BY_COMMIT_VELOCITY_WEEKS = Comparator.comparing(LeaderBoardEntry::getCommitVelocityPerWeek);
    
    public int getTotalCommits(){ 
        return this.totalCommits;
    }

    public int getCommitDays(){
        return this.commitDays;
    }

    public float getCommitVelocityPerDay(){
        return this.commitVelocityPerDay;
    }

    public float getCommitVelocityPerWeek(){
        return this.commitVelocityPerWeek;
    }

    public LeaderBoardEntry(ActivitySummary summary){
        this.totalCommits = summary.getTotalCommits();
        this.username = summary.getUsername();
        this.commitDays = summary.getActiveDays();
        this.commitVelocityPerDay = summary.getCommitVelocityPerDay();
        this.commitVelocityPerWeek = summary.getCommitVelocityPerWeek();
    }

    

}
