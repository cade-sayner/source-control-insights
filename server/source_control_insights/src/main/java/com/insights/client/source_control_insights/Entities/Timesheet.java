package com.insights.client.source_control_insights.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "timesheets")
class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer timesheetId;
    
    @Column(nullable = false)
    private Integer duration;
    
    @ManyToOne
    @JoinColumn(name = "contributor_id", nullable = false)
    private User contributor;
    
    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;
}