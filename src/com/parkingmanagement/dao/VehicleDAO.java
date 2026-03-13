package com.parkingmanagement.dao;

import com.parkingmanagement.model.Vehicle;
import com.parkingmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    // 获取所有车辆
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, plate_number, color, owner_name, gender, contact, create_time, update_time " +
                "FROM vehicles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                vehicles.add(mapVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // 根据车牌号获取车辆
    public Vehicle getVehicleByPlateNumber(String plateNumber) {
        String sql = "SELECT vehicle_id, plate_number, color, owner_name, gender, contact, create_time, update_time " +
                "FROM vehicles WHERE plate_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapVehicle(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 添加车辆
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles(plate_number, color, owner_name, gender, contact, create_time, update_time) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getPlateNumber());
            pstmt.setString(2, vehicle.getColor());
            pstmt.setString(3, vehicle.getOwnerName());
            pstmt.setString(4, vehicle.getGender());
            pstmt.setString(5, vehicle.getContact());
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(vehicle.getCreateTime()));
            pstmt.setTimestamp(7, java.sql.Timestamp.valueOf(vehicle.getUpdateTime()));
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更新车辆信息
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET color = ?, owner_name = ?, gender = ?, contact = ?, update_time = ? " +
                "WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getColor());
            pstmt.setString(2, vehicle.getOwnerName());
            pstmt.setString(3, vehicle.getGender());
            pstmt.setString(4, vehicle.getContact());
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(vehicle.getUpdateTime()));
            pstmt.setInt(6, vehicle.getVehicleId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 删除车辆
    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 根据ID获取车辆
    public Vehicle getVehicleById(Integer vehicleId) {
        String sql = "SELECT vehicle_id, plate_number, color, owner_name, gender, contact, create_time, update_time " +
                "FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapVehicle(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 映射 ResultSet 到 Vehicle 对象
    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setPlateNumber(rs.getString("plate_number"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setOwnerName(rs.getString("owner_name"));
        vehicle.setGender(rs.getString("gender"));
        vehicle.setContact(rs.getString("contact"));
        vehicle.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        vehicle.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        return vehicle;
    }

    // 搜索车辆
    public List<Vehicle> searchVehicles(String keyword) {
        List<Vehicle> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String sql = "SELECT vehicle_id, plate_number, color, owner_name, gender, contact, create_time, update_time " +
                "FROM vehicles WHERE plate_number LIKE ? OR owner_name LIKE ? OR contact LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword.trim() + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}