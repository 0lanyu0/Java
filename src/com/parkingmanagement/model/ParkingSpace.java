package com.parkingmanagement.model;

import java.time.LocalDateTime;

public class ParkingSpace {
    private int spaceId;
    private String spaceNumber;
    private int typeId;
    private String typeName;
    private boolean occupied;
    private boolean available;

    public ParkingSpace() {
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(String spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return spaceNumber + " (" + typeName + ")";
    }

    public void setVehicleId(Integer vehicleId) {
    }

    public void setUpdateTime(LocalDateTime now) {
    }

    public int getVehicleId() {
        return 0;
    }

    public Object getCreateTime() {
        return null;
    }

    public Object getUpdateTime() {
        return null;
    }

    public void setCreateTime(LocalDateTime now) {

    }
}
