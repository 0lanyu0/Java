package com.parkingmanagement.dao;

import com.parkingmanagement.model.Login;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YonghuDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_management?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";

    public YonghuDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS yonghu ("
                + "user_id INT PRIMARY KEY AUTO_INCREMENT,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "password_hash VARCHAR(255) NOT NULL,"
                + "email VARCHAR(100),"
                + "phone VARCHAR(20),"
                + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Login getUserByUsername(String username) {
        String sql = "SELECT * FROM yonghu WHERE username = ?";
        Login user = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new Login();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                user.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<Login> getAllUsers() {
        String sql = "SELECT * FROM yonghu ORDER BY create_time DESC";
        List<Login> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Login user = new Login();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                user.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean addUser(Login user) {
        String sql = "INSERT INTO yonghu (username, password_hash, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(Login user) {
        String sql = "UPDATE yonghu SET password_hash = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPasswordHash());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setInt(4, user.getUserId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM yonghu WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}