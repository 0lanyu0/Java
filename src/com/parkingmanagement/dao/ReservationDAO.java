package com.parkingmanagement.dao;
import com.parkingmanagement.model.ParkingSpace;
import com.parkingmanagement.model.Reservation;
import com.parkingmanagement.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ReservationDAO {
    // 获取所有预订信息
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.vehicle_id, r.space_id, r.start_time, r.end_time, r.status, " +
                "r.create_time, r.update_time, r.name, r.gender, r.phone, v.plate_number, ps.space_number " +
                "FROM reservations r " +
                "LEFT JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                "LEFT JOIN parking_spaces ps ON r.space_id = ps.space_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(mapReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
    // 获取有效预订
    public List<Reservation> getActiveReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.vehicle_id, r.space_id, r.start_time, r.end_time, r.status, " +
                "r.create_time, r.update_time, r.name, r.gender, r.phone, v.plate_number, ps.space_number " +
                "FROM reservations r " +
                "LEFT JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                "LEFT JOIN parking_spaces ps ON r.space_id = ps.space_id " +
                "WHERE r.status = '已预约'"; // 查询条件改为"已预约"
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(mapReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
    // 根据 ID 获取预订信息
    public Reservation getReservationById(int reservationId) {
        String sql = "SELECT r.reservation_id, r.vehicle_id, r.space_id, r.start_time, r.end_time, r.status, " +
                "r.create_time, r.update_time, r.name, r.gender, r.phone, v.plate_number, ps.space_number " +
                "FROM reservations r " +
                "LEFT JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                "LEFT JOIN parking_spaces ps ON r.space_id = ps.space_id " +
                "WHERE r.reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 添加预订
    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations(vehicle_id, space_id, start_time, end_time, status, " +
                "create_time, update_time, name, gender, phone) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservation.getVehicleId());
            pstmt.setInt(2, reservation.getSpaceId());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(reservation.getStartTime()));
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(reservation.getEndTime()));
            pstmt.setString(5, reservation.getStatus()); // 状态直接从对象获取（已确保合法）
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(reservation.getCreateTime()));
            pstmt.setTimestamp(7, java.sql.Timestamp.valueOf(reservation.getUpdateTime()));
            pstmt.setString(8, reservation.getName());
            pstmt.setString(9, reservation.getGender());
            pstmt.setString(10, reservation.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 更新预订
    public void updateReservation(Reservation reservation) {
        String sql = "UPDATE reservations SET vehicle_id = ?, space_id = ?, start_time = ?, end_time = ?, " +
                "status = ?, update_time = ?, name = ?, gender = ?, phone = ? WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservation.getVehicleId());
            pstmt.setInt(2, reservation.getSpaceId());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(reservation.getStartTime()));
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(reservation.getEndTime()));
            pstmt.setString(5, reservation.getStatus()); // 状态直接从对象获取（已确保合法）
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(reservation.getUpdateTime()));
            pstmt.setString(7, reservation.getName());
            pstmt.setString(8, reservation.getGender());
            pstmt.setString(9, reservation.getPhone());
            pstmt.setInt(10, reservation.getReservationId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 删除预订
    public void deleteReservation(int reservationId) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 获取可用车位
    public List<ParkingSpace> getAvailableParkingSpaces() {
        List<ParkingSpace> availableSpaces = new ArrayList<>();
        String sql = "SELECT ps.space_id, ps.space_number " +
                "FROM parking_spaces ps " +
                "LEFT JOIN reservations r ON ps.space_id = r.space_id AND r.status IN ('已预约', '已使用') " +
                "LEFT JOIN parking_records pr ON ps.space_id = pr.space_id AND pr.exit_time IS NULL " +
                "WHERE r.reservation_id IS NULL AND pr.record_id IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ParkingSpace space = new ParkingSpace();
                space.setSpaceId(rs.getInt("space_id"));
                space.setSpaceNumber(rs.getString("space_number"));
                availableSpaces.add(space);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableSpaces;
    }
    // 映射 ResultSet 到 Reservation 对象
    private Reservation mapReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setVehicleId(rs.getInt("vehicle_id"));
        reservation.setSpaceId(rs.getInt("space_id"));
        reservation.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        reservation.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        reservation.setStatus(rs.getString("status"));
        reservation.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        reservation.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        reservation.setName(rs.getString("name"));
        reservation.setGender(rs.getString("gender"));
        reservation.setPhone(rs.getString("phone"));
        reservation.setPlateNumber(rs.getString("plate_number"));
        reservation.setSpaceNumber(rs.getString("space_number"));
        return reservation;
    }

    // 根据车辆ID获取预订
    public List<Reservation> getReservationsByVehicleId(int vehicleId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE vehicle_id = ? AND status = '已预约'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                reservation.setVehicleId(rs.getInt("vehicle_id"));
                reservation.setSpaceId(rs.getInt("space_id"));
                reservation.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                reservation.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                reservation.setStatus(rs.getString("status"));
                reservation.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                reservation.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<Reservation> getReservationsByPlateNumber(String plateNumber) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.vehicle_id, r.space_id, r.start_time, r.end_time, r.status, " +
                "r.create_time, r.update_time, r.name, r.gender, r.phone, v.plate_number, ps.space_number " +
                "FROM reservations r " +
                "LEFT JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                "LEFT JOIN parking_spaces ps ON r.space_id = ps.space_id " +
                "WHERE v.plate_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
}