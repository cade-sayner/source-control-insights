package com.insights.client.source_control_insights.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Models.ActivitySummary;

@Service
public class RepositoryService {
//    public ActivitySummary getRepoActivitySummary(List<Commit> commits) {
//         String username = user.getUsername();
//         int totalCommits = commits.size();
//         List<Instant> dates = commits.stream().map(c -> c.getCommitTimestamp()).sorted().collect(Collectors.toList());
//         long daysBetween = ChronoUnit.DAYS.between(dates.getFirst().atZone(ZoneId.systemDefault()).toLocalDate(),
//                 dates.getLast().atZone(ZoneId.systemDefault()).toLocalDate());
//         long weeksBetween = ChronoUnit.WEEKS.between(dates.getFirst().atZone(ZoneId.systemDefault()).toLocalDate(),
//                 dates.getLast().atZone(ZoneId.systemDefault()).toLocalDate());
//         float commitVelocityDaily = daysBetween != 0 ? commits.size() / daysBetween : commits.size();
//         float commitVelocityWeekly = weeksBetween != 0 ? commits.size() / weeksBetween : commits.size();

//         Map<LocalDate, Long> groupedByDay = commits.stream()
//                 .collect(Collectors.groupingBy(
//                         commit -> commit.getCommitTimestamp().atZone(ZoneId.systemDefault()).toLocalDate(),
//                         Collectors.counting()));
//         int activeDays = groupedByDay.size();

//         var mostActiveDay = groupedByDay.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null).getKey()
//                 .toString();
//         var lastCommitDay = dates.getLast().toString();

//         int totalFilesChanged = commits.stream().mapToInt(Commit::getFilesChanged).sum();
//         int totalInsertions = commits.stream().mapToInt(Commit::getInsertions).sum();
//         int totalDeletions = commits.stream().mapToInt(Commit::getDeletions).sum();
//         int netChanges = totalInsertions - totalDeletions;
//         return new ActivitySummary(username, totalCommits, commitVelocityDaily, commitVelocityWeekly, activeDays,
//                 mostActiveDay, lastCommitDay,totalFilesChanged, totalInsertions, totalDeletions, netChanges);
//         } 
}
