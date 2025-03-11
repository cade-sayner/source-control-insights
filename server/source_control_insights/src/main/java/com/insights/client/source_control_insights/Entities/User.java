package com.insights.client.source_control_insights.Entities;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="User")
public class User {
    @Id
    @Column(name = "google_sub")
    private String GoogleSub;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "UserRole", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "google_sub"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id" ))
    private List<Role> roles;

    @OneToMany(mappedBy = "contributor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Worklog> worklogs = new ArrayList<>();

    @OneToMany(mappedBy = "contributor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commit> commits = new ArrayList<>();

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setGoogleSub(String googleSub){
        this.GoogleSub = googleSub;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGoogleSub() {
        return this.GoogleSub;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public User(String googleSub, String username, String email, List<Role> roles) {
        this.GoogleSub = googleSub;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public User() {}
}
