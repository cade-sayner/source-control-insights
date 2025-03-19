package com.insights.client.source_control_insights.Controllers;

import org.springframework.web.bind.annotation.RestController;
import com.insights.client.source_control_insights.Repositories.CommitRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;

import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/")
public class CommitController {
    CommitRepository commitRepository;

    public CommitController(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @GetMapping("commits")
    public ResponseEntity<?> getCommits(@AuthenticationPrincipal Jwt jwt) {
        try {
            return ResponseEntity.ok(commitRepository.findByContributor_GoogleId(jwt.getClaim("sub")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occured while fetching the commits.");
        }
    }

    @Autowired
    private CommitService commitService;

    @GetMapping("commits/frequencies")
    public ResponseEntity<Map<String, Long>> getCommitCodeFrequencies(@RequestParam("repoId") UUID repoId,@RequestParam("code") String code) {
        Map<String, Long> frequencies = commitService.getCodeFrequency(repoId,code);
        return ResponseEntity.ok(frequencies);
    }
}
