package com.insights.client.source_control_insights.Entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    public String googleId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id" ))
    private List<Role> roles;

    public User(String email, String name, String google_id, List<Role> roles){
        this.email = email;
        this.name = name;
        this.googleId = google_id;
        this.roles = roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setGoogleId(String googleSub){
        this.googleId = googleSub;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getGoogleId() {
        return this.googleId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return name;
    }

   public User(){}
}


