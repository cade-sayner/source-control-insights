package com.insights.client.source_control_insights.Services;
import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Repositories.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CommitService {

    @Autowired
    private CommitRepository commitRepository;

    public Map<String, Long> getCodeFrequency(UUID repoId ,String code) {
        // Get commits for the given repoId
        List<Commit> commits = commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(repoId);
        
        // Store frequency counts for the predefined codes
        Map<String, Long> frequencyMap = new HashMap<>();
        frequencyMap.put("PRC", 0L);
        frequencyMap.put("BGF", 0L);
        frequencyMap.put("FTR", 0L);
        frequencyMap.put("DEV", 0L);
        frequencyMap.put("PRD", 0L);
    

        if (code != null && !code.isEmpty()) {
            
            if (!frequencyMap.containsKey(code)) {
                throw new IllegalArgumentException("Invalid code: " + code);
            }
    
            
            long count = 0;
    
            // Parse commit messages for the specified code
            for (Commit commit : commits) {
                String message = commit.getMessage();
                if (message != null && message.endsWith(code)) {
                    count++;
                }
            }
    
            // Return a map with the specified code and its count
            Map<String, Long> result = new HashMap<>();
            result.put(code, count);
            return result;
        } else {
            // Parse commit messages for all codes and update their counts
            for (Commit commit : commits) {
                String message = commit.getMessage();
                if (message != null) {
                    // Check for codes at the end of the commit message
                    for (String predefinedCode : frequencyMap.keySet()) {
                        if (message.endsWith(predefinedCode)) {
                            frequencyMap.put(predefinedCode, frequencyMap.get(predefinedCode) + 1);
                        }
                    }
                }
            }
            return frequencyMap;  
        }
    }

}

