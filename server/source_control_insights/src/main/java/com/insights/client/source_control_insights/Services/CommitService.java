package com.insights.client.source_control_insights.Services;
import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Repositories.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommitService {

    @Autowired
    private CommitRepository commitRepository;

    public Map<String, Long> getCodeFrequency() {
        List<Commit> commits = commitRepository.findAll();
        
        // Initialize map to store frequency counts
        Map<String, Long> frequencyMap = new HashMap<>();
        frequencyMap.put("endpoints", 0L);
        frequencyMap.put("changes", 0L);
        frequencyMap.put("updates", 0L);

        // Parse commit messages
        for (Commit commit : commits) {
            String message = commit.getMessage();
            if (message != null) {
                // Check for codes at the end of the commit message
                for (String code : frequencyMap.keySet()) {
                    if (message.endsWith(code)) {
                        frequencyMap.put(code, frequencyMap.get(code) + 1);
                    }
                }
            }
        }
        return frequencyMap;
    }

    //  public List<Commit> getCommitsByCode(String code) {
    //      return commitRepository.findByCommitMessageContaining(code);
    //  }
}

