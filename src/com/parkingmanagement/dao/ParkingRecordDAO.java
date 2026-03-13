package com.parkingmanagement.dao;

import com.parkingmanagement.model.ParkingRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingRecordDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_management?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";
    private VehicleDAO vehicleDAO;
    private ParkingSpaceDAO spaceDAO;

    public ParkingRecordDAO() {
        createTableIfNotExists();
        vehicleDAO = new VehicleDAO();
        spaceDAO = new ParkingSpaceDAO();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS parking_records ("
                + "    record_id INT PRIMARY KEY AUTO_INCREMENT,"
                + "    vehicle_id INT NOT NULL,"
                + "    space_id INT NOT NULL,"
                + "    entry_time DATETIME NOT NULL,"
                + "    exit_time DATETIME,"
                + "    fee DECIMAL(10, 2),"
                + "    payment_status ENUM('未支付', '已支付', '已减免') DEFAULT '未支付',"
                + "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),"
                + "    FOREIGN KEY (space_id) REFERENCES parking_spaces(space_id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addParkingRecord(ParkingRecord record) {
        String sql = "INSERT INTO parking_records(vehicle_id, space_id, entry_time, exit_time, fee, payment_status) " +
                "VALUES(?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, record.getVehicleId());
            pstmt.setInt(2, record.getSpaceId());
            pstmt.setObject(3, record.getEntryTime());
            pstmt.setObject(4, record.getExitTime());
            pstmt.setDouble(5, record.getFee() != null ? record.getFee() : 0.0);
            pstmt.setString(6, record.getPaymentStatus() != null ? record.getPaymentStatus() : "未支付");
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setRecordId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateParkingRecord(ParkingRecord record) {
        String sql = "UPDATE parking_records SET vehicle_id = ?, space_id = ?, entry_time = ?, " +
                "exit_time = ?, fee = ?, payment_status = ? WHERE record_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, record.getVehicleId());
            pstmt.setInt(2, record.getSpaceId());
            pstmt.setObject(3, record.getEntryTime());
            pstmt.setObject(4, record.getExitTime());
            // 替换 Objects.requireNonNullElse 为 Java 8 兼容写法
            pstmt.setDouble(5, record.getFee() != null ? record.getFee() : 0.0);
            pstmt.setString(6, record.getPaymentStatus() != null ? record.getPaymentStatus() : "未支付");
            pstmt.setInt(7, record.getRecordId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteParkingRecord(int recordId) {
        String sql = "DELETE FROM parking_records WHERE record_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ParkingRecord getParkingRecordById(int recordId) {
        String sql = "SELECT pr.*, v.plate_number, ps.space_number " +
                "FROM parking_records pr " +
                "JOIN vehicles v ON pr.vehicle_id = v.vehicle_id " +
                "JOIN parking_spaces ps ON pr.space_id = ps.space_id " +
                "WHERE pr.record_id = ?";
        ParkingRecord record = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                record = mapRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }

    public List<ParkingRecord> getAllParkingRecords() {
        String sql = "SELECT pr.*, v.plate_number, ps.space_number " +
                "FROM parking_records pr " +
                "JOIN vehicles v ON pr.vehicle_id = v.vehicle_id " +
                "JOIN parking_spaces ps ON pr.space_id = ps.space_id " +
                "ORDER BY pr.entry_time DESC";
        List<ParkingRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<ParkingRecord> searchParkingRecords(Integer vehicleId, String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder("SELECT pr.*, v.plate_number, ps.space_number " +
                "FROM parking_records pr " +
                "JOIN vehicles v ON pr.vehicle_id = v.vehicle_id " +
                "JOIN parking_spaces ps ON pr.space_id = ps.space_id " +
                "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (vehicleId != null) {
            sql.append(" AND pr.vehicle_id = ?");
            params.add(vehicleId);
        }
        if (!dateFrom.isEmpty()) {
            sql.append(" AND pr.entry_time >= ?");
            params.add(dateFrom);
        }
        if (!dateTo.isEmpty()) {
            sql.append(" AND pr.entry_time <= ?");
            params.add(dateTo);
        }
        sql.append(" ORDER BY pr.entry_time DESC");
        List<ParkingRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<ParkingRecord> getActiveParkingRecords() {
        String sql = "SELECT pr.*, v.plate_number, ps.space_number " +
                "FROM parking_records pr " +
                "JOIN vehicles v ON pr.vehicle_id = v.vehicle_id " +
                "JOIN parking_spaces ps ON pr.space_id = ps.space_id " +
                "WHERE pr.exit_time IS NULL " +
                "ORDER BY pr.entry_time ASC";
        List<ParkingRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<ParkingRecord> getParkingRecordsByPaymentStatus(String paymentStatus) {
        String sql = "SELECT pr.*, v.plate_number, ps.space_number " +
                "FROM parking_records pr " +
                "JOIN vehicles v ON pr.vehicle_id = v.vehicle_id " +
                "JOIN parking_spaces ps ON pr.space_id = ps.space_id " +
                "WHERE pr.payment_status = ? " +
                "ORDER BY pr.entry_time DESC";
        List<ParkingRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, paymentStatus);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<ParkingRecord> getParkingRecordsBySpaceNumber(String spaceNumber) {
        String sql = "SELECT pr.*, v.plate_number, ps.space_number " +
                "FROM parking_records pr " +
                "JOIN vehicles v ON pr.vehicle_id = v.vehicle_id " +
                "JOIN parking_spaces ps ON pr.space_id = ps.space_id " +
                "WHERE ps.space_number = ? " +
                "ORDER BY pr.entry_time DESC";
        List<ParkingRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spaceNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    private ParkingRecord mapRecord(ResultSet rs) throws SQLException {
        ParkingRecord record = new ParkingRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setVehicleId(rs.getInt("vehicle_id"));
        record.setSpaceId(rs.getInt("space_id"));
        record.setEntryTime(rs.getTimestamp("entry_time").toLocalDateTime());
        Timestamp exitTime = rs.getTimestamp("exit_time");
        if (exitTime != null) {
            record.setExitTime(exitTime.toLocalDateTime());
        }
        record.setFee(rs.getDouble("fee"));
        record.setPaymentStatus(rs.getString("payment_status"));
        // 设置车牌号和车位编号
        record.setLicensePlate(rs.getString("plate_number"));
        record.setSpaceNumber(rs.getString("space_number"));
        // 计算停车时长
        if (record.getEntryTime() != null && record.getExitTime() != null) {
            long seconds = java.time.Duration.between(record.getEntryTime(), record.getExitTime()).getSeconds();
            record.setDuration(seconds);
        }
        return record;
    }

}