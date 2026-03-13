package com.parkingmanagement.model;
import java.time.LocalDateTime;
public class Reservation {
    // 定义与数据库枚举匹配的状态常量
    public static final String STATUS_RESERVED = "已预约";
    public static final String STATUS_USED = "已使用";
    public static final String STATUS_CANCELLED = "已取消";
    public static final String STATUS_EXPIRED = "已过期";
    private int reservationId;
    private int vehicleId;
    private int spaceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String name;
    private String gender;
    private String phone;
    private String plateNumber;
    private String spaceNumber;
    private double fee;
    private String color;

    // Getter 和 Setter 方法
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getSpaceId() { return spaceId; }
    public void setSpaceId(int spaceId) { this.spaceId = spaceId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getSpaceNumber() { return spaceNumber; }
    public void setSpaceNumber(String spaceNumber) { this.spaceNumber = spaceNumber; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}