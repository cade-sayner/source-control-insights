package com.insights.client.source_control_insights.Entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="roles", uniqueConstraints = @UniqueConstraint(columnNames = { "role_name" }))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses the DB's auto-increment
    @Column(name = "role_id")
    private int roleId;

    @Column(name = "role_name")
    public String roleName;

    @Column(name = "role_description")
    public String roleDescription;

    public Role() {
    }

    public Role(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }
}