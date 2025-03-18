package com.insights.client.source_control_insights.Services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Models.ActivitySummary;
import com.insights.client.source_control_insights.Models.RepoActivity;

@Service
public class RepositoryService {
   public RepoActivity getRepoActivitySummary(List<Commit> commits) {
        int totalCommits = commits.size();
        List<Instant> dates = commits.stream().map(c -> c.getCommitTimestamp()).sorted().collect(Collectors.toList());
        long daysBetween = ChronoUnit.DAYS.between(dates.getFirst().atZone(ZoneId.systemDefault()).toLocalDate(),
                dates.getLast().atZone(ZoneId.systemDefault()).toLocalDate());
        long weeksBetween = ChronoUnit.WEEKS.between(dates.getFirst().atZone(ZoneId.systemDefault()).toLocalDate(),
                dates.getLast().atZone(ZoneId.systemDefault()).toLocalDate());
        float commitVelocityDaily = daysBetween != 0 ? commits.size() / daysBetween : commits.size();
        float commitVelocityWeekly = weeksBetween != 0 ? commits.size() / weeksBetween : commits.size();

        Map<LocalDate, Long> groupedByDay = commits.stream()
                .collect(Collectors.groupingBy(
                        commit -> commit.getCommitTimestamp().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.counting()));
        int activeDays = groupedByDay.size();

        var mostActiveDay = groupedByDay.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null).getKey()
                .toString();
        var lastCommitDay = dates.getLast().toString();

        int totalFilesChanged = commits.stream().mapToInt(Commit::getFilesChanged).sum();
        int totalInsertions = commits.stream().mapToInt(Commit::getInsertions).sum();
        int totalDeletions = commits.stream().mapToInt(Commit::getDeletions).sum();
        int netChanges = totalInsertions - totalDeletions;
        return new RepoActivity(totalCommits, commitVelocityDaily, commitVelocityWeekly, activeDays,
                mostActiveDay, lastCommitDay,totalFilesChanged, totalInsertions, totalDeletions, netChanges);
        } 
}
