package com.lab.system.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private String username;
    private String password;
    private String role; // Administrator, Equipment Staff, Borrower, Department Head
    private String department;

    public User(String userId, String name, String username, String password, String role, String department) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return String.format("ID: %-8s | Name: %-18s | Role: %-15s | Dept: %s",
                userId, name, role, department);
    }
}