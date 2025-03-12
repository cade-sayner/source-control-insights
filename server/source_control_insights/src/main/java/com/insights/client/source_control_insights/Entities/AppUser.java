package com.insights.client.source_control_insights.Entities;


import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses the DB's auto-increment
    @Column(name = "user_id")
    private int userId;

    @Column(name = "google_sub")
    private String googleSub;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "UserRole", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id" ))
    private List<Role> roles;

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setGoogleSub(String googleSub){
        this.googleSub = googleSub;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGoogleSub() {
        return this.googleSub;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public AppUser(String googleSub, String username, String email, List<Role> roles) {
        this.googleSub = googleSub;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public AppUser() {}
}
