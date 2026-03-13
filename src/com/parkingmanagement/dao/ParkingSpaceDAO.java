package com.parkingmanagement.dao;

import com.parkingmanagement.model.ParkingSpace;
import com.parkingmanagement.model.SpaceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpaceDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_management?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";

    public ParkingSpaceDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS parking_spaces ("
                + "    space_id INT PRIMARY KEY AUTO_INCREMENT,"
                + "    space_number VARCHAR(20) NOT NULL UNIQUE,"
                + "    type_id INT NOT NULL,"
                + "    vehicle_id INT,"
                + "    is_occupied TINYINT NOT NULL DEFAULT 0,"
                + "    is_available TINYINT NOT NULL DEFAULT 1,"
                + "    create_time DATETIME,"
                + "    update_time DATETIME,"
                + "    FOREIGN KEY (type_id) REFERENCES space_types(type_id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addParkingSpace(ParkingSpace space) {
        String sql = "INSERT INTO parking_spaces(space_number, type_id, vehicle_id, is_occupied, is_available, create_time, update_time) VALUES(?,?,?,?,?,NOW(),NOW())";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, space.getSpaceNumber());
            pstmt.setInt(2, space.getTypeId());
            pstmt.setInt(3, space.getVehicleId());
            pstmt.setInt(4, space.isOccupied() ? 1 : 0);
            pstmt.setInt(5, space.isAvailable() ? 1 : 0);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateParkingSpace(ParkingSpace space) {
        String sql = "UPDATE parking_spaces SET space_number = ?, type_id = ?, vehicle_id = ?, is_occupied = ?, is_available = ?, update_time = NOW() WHERE space_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, space.getSpaceNumber());
            pstmt.setInt(2, space.getTypeId());
            pstmt.setInt(3, space.getVehicleId());
            pstmt.setInt(4, space.isOccupied() ? 1 : 0);
            pstmt.setInt(5, space.isAvailable() ? 1 : 0);
            pstmt.setInt(6, space.getSpaceId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteParkingSpace(int spaceId) {
        String sql = "DELETE FROM parking_spaces WHERE space_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, spaceId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ParkingSpace getParkingSpaceById(int spaceId) {
        String sql = "SELECT * FROM parking_spaces WHERE space_id = ?";
        ParkingSpace space = null;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, spaceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                space = new ParkingSpace();
                space.setSpaceId(rs.getInt("space_id"));
                space.setSpaceNumber(rs.getString("space_number"));
                space.setTypeId(rs.getInt("type_id"));
                space.setVehicleId(rs.getInt("vehicle_id"));
                space.setOccupied(rs.getInt("is_occupied") == 1);
                space.setAvailable(rs.getInt("is_available") == 1);
                space.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                space.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());

                space.setTypeName(space.getTypeId() != 0 ?
                        SpaceType.getById(space.getTypeId()).getName() : "未知类型");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return space;
    }

    public List<ParkingSpace> getAllParkingSpaces() {
        String sql = "SELECT * FROM parking_spaces";
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ParkingSpace space = new ParkingSpace();
                space.setSpaceId(rs.getInt("space_id"));
                space.setSpaceNumber(rs.getString("space_number"));
                space.setTypeId(rs.getInt("type_id"));
                space.setVehicleId(rs.getInt("vehicle_id"));
                space.setOccupied(rs.getInt("is_occupied") == 1);
                space.setAvailable(rs.getInt("is_available") == 1);
                space.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                space.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());

                space.setTypeName(space.getTypeId() != 0 ?
                        SpaceType.getById(space.getTypeId()).getName() : "未知类型");
                spaces.add(space);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spaces;
    }

    public List<ParkingSpace> getAvailableParkingSpaces() {
        String sql = "SELECT * FROM parking_spaces WHERE is_available = 1 AND is_occupied = 0";
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ParkingSpace space = new ParkingSpace();
                space.setSpaceId(rs.getInt("space_id"));
                space.setSpaceNumber(rs.getString("space_number"));
                space.setTypeId(rs.getInt("type_id"));
                space.setVehicleId(rs.getInt("vehicle_id"));
                space.setOccupied(rs.getInt("is_occupied") == 1);
                space.setAvailable(rs.getInt("is_available") == 1);
                space.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                space.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());

                space.setTypeName(space.getTypeId() != 0 ?
                        SpaceType.getById(space.getTypeId()).getName() : "未知类型");
                spaces.add(space);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spaces;
    }

    public List<ParkingSpace> searchParkingSpaces(String keyword) {
        String sql = "SELECT * FROM parking_spaces WHERE space_number LIKE ?";
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ParkingSpace space = new ParkingSpace();
                space.setSpaceId(rs.getInt("space_id"));
                space.setSpaceNumber(rs.getString("space_number"));
                space.setTypeId(rs.getInt("type_id"));
                space.setVehicleId(rs.getInt("vehicle_id"));
                space.setOccupied(rs.getInt("is_occupied") == 1);
                space.setAvailable(rs.getInt("is_available") == 1);
                space.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                space.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());

                space.setTypeName(space.getTypeId() != 0 ?
                        SpaceType.getById(space.getTypeId()).getName() : "未知类型");
                spaces.add(space);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spaces;
    }

}