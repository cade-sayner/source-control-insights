package com.insights.client.source_control_insights.Models;

public class ActivitySummaryRepo extends ActivitySummary {
    private float percentile;

    public float getPercentile() {
        return this.percentile;
    }

    public void setPercentile(float percentile) {
        this.percentile = percentile;
    }

    public ActivitySummaryRepo(ActivitySummary summary, float percentile){ 
        super(summary.getUsername(), summary.getTotalCommits(), summary.getCommitVelocityPerDay(), summary.getCommitVelocityPerWeek(),summary.getActiveDays(), summary.getMostActiveDay(), summary.getLastCommitDate(), summary.getFilesChanged(), summary.getInsertions(), summary.getDeletions(), summary.getNetChanges());
        this.percentile = percentile;
    }
}
