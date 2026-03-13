package com.parkingmanagement.model;

import java.time.LocalDateTime;

public class FeeRule {
    private Integer ruleId;
    private Integer typeId;
    private String typeName;
    private double hourlyRate;
    private double dailyMax;
    private boolean active;
    private LocalDateTime effectiveDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getters and Setters
    public Integer getRuleId() { return ruleId; }
    public void setRuleId(Integer ruleId) { this.ruleId = ruleId; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public double getDailyMax() { return dailyMax; }
    public void setDailyMax(double dailyMax) { this.dailyMax = dailyMax; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}