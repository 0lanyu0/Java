package com.parkingmanagement.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private int roleId;
    private String roleName;
    private boolean active;

    public void setPasswordHash(String passwordHash) {}
    public void setSalt(String salt) {}
    public void setEmail(String email) {}
    public void setPhone(String phone) {}
    public void setRole(String role) {}
    public String getEmail() { return null; }
    public String getPasswordHash() { return ""; }
    public String getSalt() { return ""; }
    public String getStatus() { return null; }
    public void setCreateTime(LocalDateTime createTime) {}
    public void setUpdateTime(LocalDateTime updateTime) {}
    public void setStatus(String status) {}
    public String getRole() { return ""; }
    public String getPhone() { return ""; }

    // Getter & Setter
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}