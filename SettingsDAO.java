package com.parkingmanagement.dao;

import com.parkingmanagement.model.Settings;

import java.sql.*;

public class SettingsDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_management?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";

    public SettingsDAO() {
        createTableIfNotExists();
        initializeDefaultSettings();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS settings ("
                + "    id INT PRIMARY KEY AUTO_INCREMENT,"
                + "    parking_name VARCHAR(100),"
                + "    address VARCHAR(200),"
                + "    contact_phone VARCHAR(20),"
                + "    contact_email VARCHAR(50),"
                + "    base_fee DECIMAL(10, 2),"
                + "    hourly_rate DECIMAL(10, 2),"
                + "    daily_max_fee DECIMAL(10, 2),"
                + "    currency VARCHAR(10)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDefaultSettings() {
        String checkSql = "SELECT COUNT(*) FROM settings";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO settings(parking_name, address, contact_phone, contact_email, base_fee, hourly_rate, daily_max_fee, currency) VALUES(?,?,?,?,?,?,?,?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "智慧停车场");
                    pstmt.setString(2, "中国北京市朝阳区");
                    pstmt.setString(3, "010-12345678");
                    pstmt.setString(4, "contact@parking.com");
                    pstmt.setBigDecimal(5, new java.math.BigDecimal("10.00"));
                    pstmt.setBigDecimal(6, new java.math.BigDecimal("5.00"));
                    pstmt.setBigDecimal(7, new java.math.BigDecimal("50.00"));
                    pstmt.setString(8, "元");
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        Settings settings = null;
        String sql = "SELECT * FROM settings LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                settings = new Settings();
                settings.setId(rs.getInt("id"));
                settings.setParkingName(rs.getString("parking_name"));
                settings.setAddress(rs.getString("address"));
                settings.setContactPhone(rs.getString("contact_phone"));
                settings.setContactEmail(rs.getString("contact_email"));
                settings.setBaseFee(rs.getBigDecimal("base_fee"));
                settings.setHourlyRate(rs.getBigDecimal("hourly_rate"));
                settings.setDailyMaxFee(rs.getBigDecimal("daily_max_fee"));
                settings.setCurrency(rs.getString("currency"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public boolean saveSettings(Settings settings) {
        String sql = "UPDATE settings SET parking_name = ?, address = ?, contact_phone = ?, contact_email = ?, base_fee = ?, hourly_rate = ?, daily_max_fee = ?, currency = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, settings.getParkingName());
            pstmt.setString(2, settings.getAddress());
            pstmt.setString(3, settings.getContactPhone());
            pstmt.setString(4, settings.getContactEmail());
            pstmt.setBigDecimal(5, settings.getBaseFee());
            pstmt.setBigDecimal(6, settings.getHourlyRate());
            pstmt.setBigDecimal(7, settings.getDailyMaxFee());
            pstmt.setString(8, settings.getCurrency());
            pstmt.setInt(9, settings.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}