package com.parkingmanagement.dao;

import com.parkingmanagement.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_management?serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";

    public UserDAO() {
        createTableIfNotExists();
        createAdminUserIfNotExists();
    }

    // 创建表（如果不存在）
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "    user_id INT PRIMARY KEY AUTO_INCREMENT,"
                + "    username VARCHAR(50) NOT NULL UNIQUE,"
                + "    password VARCHAR(100) NOT NULL,"
                + "    full_name VARCHAR(100) NOT NULL,"
                + "    role_id INT NOT NULL,"
                + "    role_name VARCHAR(50) NOT NULL,"
                + "    is_active BOOLEAN NOT NULL DEFAULT TRUE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 初始化管理员用户（如果不存在）
    private void createAdminUserIfNotExists() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO users(username, password, full_name, role_id, role_name, is_active) " +
                        "VALUES(?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, "admin123"); // 实际应加密存储，这里简化示例
                    pstmt.setString(3, "系统管理员");
                    pstmt.setInt(4, 1);
                    pstmt.setString(5, "管理员");
                    pstmt.setBoolean(6, true);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询所有用户
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, role_id, role_name, is_active FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 搜索用户（按用户名/姓名模糊匹配）
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, role_id, role_name, is_active " +
                "FROM users " +
                "WHERE username LIKE ? OR full_name LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 添加用户
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, full_name, role_id, role_name, is_active) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setInt(4, user.getRoleId());
            pstmt.setString(5, user.getRoleName());
            pstmt.setBoolean(6, user.isActive());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 根据ID查询用户
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password, full_name, role_id, role_name, is_active " +
                "FROM users WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setActive(rs.getBoolean("is_active"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 更新用户信息
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET " +
                "username = ?, " +
                "password = ?, " +
                "full_name = ?, " +
                "role_id = ?, " +
                "role_name = ?, " +
                "is_active = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setInt(4, user.getRoleId());
            pstmt.setString(5, user.getRoleName());
            pstmt.setBoolean(6, user.isActive());
            pstmt.setInt(7, user.getUserId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 删除用户
    public boolean deleteUser(int userId) {
        // 保护管理员用户，禁止删除
        if (userId == 1) {
            return false;
        }

        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}