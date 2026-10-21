package com.co.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    private String adminId; // This is the ID used in the Repository
    
    private String password;
    private boolean isTemporary;

    // --- GETTERS AND SETTERS ---
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isTemporary() { return isTemporary; }
    public void setTemporary(boolean isTemporary) { this.isTemporary = isTemporary; }
}