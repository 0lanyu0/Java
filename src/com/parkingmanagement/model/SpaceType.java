package com.parkingmanagement.model;

public enum SpaceType {
    NORMAL(1, "永久专用车位"),
    HANDICAPPED(2, "专用车位"),
    LARGE(3, "临时车位"),
    ELECTRIC(4, "普通车位");

    private int id;
    private String name;

    SpaceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SpaceType getById(int typeId) {
        for (SpaceType type : values()) {
            if (type.getId() == typeId) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}