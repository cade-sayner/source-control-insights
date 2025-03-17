package com.insights.client.source_control_insights.Services;
import java.time.ZoneId;
import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Entities.User;
import com.insights.client.source_control_insights.Models.ActivitySummary;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Repositories.CommitRepository;
import java.time.LocalDate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ContributorService {

    private final UserRepository userRepository;
    private final CommitRepository commitRepository;
    public ContributorService(UserRepository userRepository, CommitRepository commitRepository){ 
        this.userRepository = userRepository;
        this.commitRepository = commitRepository;
    }

    public ActivitySummary getActivitySummary(User user){ 
        List<Commit> commits =  commitRepository.findByContributor_GoogleId(user.googleId);
        // need to get the relevant data now
        return getActivitySummary(commits, user);
       
    }

    public ActivitySummary getActivityForRepo(User user, UUID repoId){ 
        List<Commit> commits = commitRepository.findByContributor_GoogleId(user.getGoogleId());
        List<Commit> filteredCommits = commits.stream().filter(commit -> commit.getRepository().getRepoId().equals(repoId)).collect(Collectors.toList());
        return getActivitySummary(filteredCommits, user);
    }

    public ActivitySummary getActivitySummary(List<Commit> commits, User user){ 
        String username = user.getUsername();
        int totalCommits = commits.size();

        List<Instant> dates = commits.stream().map(c -> c.getCommitTimestamp()).sorted().collect(Collectors.toList());
        System.out.println(dates);
        long daysBetween = ChronoUnit.DAYS.between(dates.getFirst().atZone(ZoneId.systemDefault()).toLocalDate(), dates.getLast().atZone(ZoneId.systemDefault()).toLocalDate());
        long weeksBetween = ChronoUnit.WEEKS.between(dates.getFirst().atZone(ZoneId.systemDefault()).toLocalDate(), dates.getLast().atZone(ZoneId.systemDefault()).toLocalDate());
        System.out.println("The days between");
        System.out.println(daysBetween);
        float commitVelocityDaily = daysBetween != 0 ? commits.size()/daysBetween : commits.size();
        float commitVelocityWeekly = weeksBetween != 0 ? commits.size()/weeksBetween : commits.size();
        
        Map<LocalDate, Long> groupedByDay = commits.stream().collect(Collectors.groupingBy(commit -> commit.getCommitTimestamp().atZone(ZoneId.systemDefault()).toLocalDate(), Collectors.counting()));
        int activeDays = groupedByDay.size();
         
        var mostActiveDay = groupedByDay.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null).getKey().toString();
        var lastCommitDay = dates.getLast().toString();
        return new ActivitySummary(username, totalCommits, commitVelocityDaily, commitVelocityWeekly, activeDays, mostActiveDay, lastCommitDay);
    }
}
