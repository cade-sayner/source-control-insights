package com.insights.client.source_control_insights.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.insights.client.source_control_insights.Entities.Commit;

import java.util.List;
import java.util.UUID;

public interface CommitRepository extends JpaRepository<Commit, UUID> {
    List<Commit> findByRepository_RepoIdOrderByCommitTimestampDesc(UUID repoId);
}
