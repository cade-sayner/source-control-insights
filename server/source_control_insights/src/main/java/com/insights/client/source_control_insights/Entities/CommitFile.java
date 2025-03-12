package com.insights.client.source_control_insights.Entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "commit_files")
public class CommitFile {
    @Id
    @GeneratedValue
    private UUID fileId;

    @ManyToOne
    @JoinColumn(name = "commit_id", nullable = false)
    private Commit commit;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String changeType;
}
