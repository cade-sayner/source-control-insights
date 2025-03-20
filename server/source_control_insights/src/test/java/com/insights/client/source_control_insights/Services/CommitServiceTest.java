package com.insights.client.source_control_insights.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.insights.client.source_control_insights.Entities.Commit;
import com.insights.client.source_control_insights.Entities.Repository;
import com.insights.client.source_control_insights.Repositories.CommitRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommitServiceTest {
    @Mock
    private CommitRepository commitRepository;

    @InjectMocks
    private CommitService commitService;

    private Commit commit1;
    private Commit commit2;
    private Commit commit3;
    private Repository rep1;
    private String code = "PRC";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set up the mock data for commits
        commit1 = new Commit();
        commit2 = new Commit();
        commit3 = new Commit();
        //UUID repoId = UUID.randomUUID();
        rep1 = new Repository();
        //rep1.setRepoId(repoId);
        commit1.setRepository(rep1);
        commit2.setRepository(rep1);
        commit3.setRepository(rep1);
    }

    @Test
    void testGetCodeFrequencyForSpecificCode() {
        // Arrange: Mock the repository to return a list of commits for repoId 1
        when(commitRepository.findByRepository_RepoIdOrderByCommitTimestampDesc(rep1.getRepoId())).thenReturn(Arrays.asList(commit1, commit2, commit3));

        // Act: Call the service method to get the frequency of the "work" code
        Map<String, Long> frequency = commitService.getCodeFrequency(rep1.getRepoId(), "PRC");

        // Assert: Check the expected frequency for the code
        assertEquals(null, frequency.get("PRC"));
        assertEquals(null, frequency.get("BGF"));
        assertEquals(null, frequency.get("FTR"));
        assertEquals(null, frequency.get("DEV"));
        assertEquals(null, frequency.get("PRD"));
    }

    // @Test
    // void testGetCodeFrequencyForAllCodes() {
    //     // Arrange: Mock the repository to return a list of commits for repoId 1
    //     when(commitRepository.findByRepoId(1L)).thenReturn(Arrays.asList(commit1, commit2, commit3));

    //     // Act: Call the service method to get the frequencies for all codes
    //     Map<String, Long> frequency = commitService.getCodeFrequency(1L, null);

    //     // Assert: Check the frequencies for all codes
    //     assertEquals(1L, frequency.get("work"));
    //     assertEquals(1L, frequency.get("changes"));
    //     assertEquals(1L, frequency.get("updates"));
    // }

    // @Test
    // void testGetCodeFrequencyWithEmptyCommitList() {
    //     // Arrange: Mock the repository to return an empty list of commits for repoId 2
    //     when(commitRepository.findByRepoId(2L)).thenReturn(Collections.emptyList());

    //     // Act: Call the service method to get the frequencies for all codes
    //     Map<String, Long> frequency = commitService.getCodeFrequency(2L, null);

    //     // Assert: Check that all codes have a frequency of 0
    //     assertEquals(0L, frequency.get("work"));
    //     assertEquals(0L, frequency.get("changes"));
    //     assertEquals(0L, frequency.get("updates"));
    // }

    // @Test
    // void testGetCodeFrequencyWithInvalidCode() {
    //     // Arrange: Mock the repository to return a list of commits for repoId 1
    //     when(commitRepository.findByRepoId(1L)).thenReturn(Arrays.asList(commit1, commit2, commit3));

    //     // Act: Call the service method to get the frequency of an invalid code (e.g., "invalidCode")
    //     Map<String, Long> frequency = commitService.getCodeFrequency(1L, "invalidCode");

    //     // Assert: Ensure the result is a map with 0 counts for all codes
    //     assertEquals(0L, frequency.get("work"));
    //     assertEquals(0L, frequency.get("changes"));
    //     assertEquals(0L, frequency.get("updates"));
    // }
}
