package com.parkingmanagement.dao;

import com.parkingmanagement.model.FeeRule;
import com.parkingmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeRuleDAO {
    public void addFeeRule(FeeRule rule) {
        String sql = "INSERT INTO fee_rules (type_id, hourly_rate, daily_max, is_active, effective_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rule.getTypeId());
            pstmt.setDouble(2, rule.getHourlyRate());
            pstmt.setDouble(3, rule.getDailyMax());
            pstmt.setBoolean(4, rule.isActive());
            pstmt.setTimestamp(5, Timestamp.valueOf(rule.getEffectiveDate()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rule.setRuleId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFeeRule(FeeRule rule) {
        String sql = "UPDATE fee_rules SET type_id=?, hourly_rate=?, daily_max=?, is_active=?, effective_date=? WHERE rule_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rule.getTypeId());
            pstmt.setDouble(2, rule.getHourlyRate());
            pstmt.setDouble(3, rule.getDailyMax());
            pstmt.setBoolean(4, rule.isActive());
            pstmt.setTimestamp(5, Timestamp.valueOf(rule.getEffectiveDate()));
            pstmt.setInt(6, rule.getRuleId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFeeRule(int ruleId) {
        String sql = "DELETE FROM fee_rules WHERE rule_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ruleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FeeRule getFeeRuleById(int ruleId) {
        FeeRule rule = null;
        String sql = "SELECT fr.*, st.type_name FROM fee_rules fr " +
                "JOIN space_types st ON fr.type_id = st.type_id " +
                "WHERE fr.rule_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ruleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rule = mapFeeRule(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rule;
    }

    public List<FeeRule> getAllFeeRules() {
        List<FeeRule> rules = new ArrayList<>();
        String sql = "SELECT fr.*, st.type_name FROM fee_rules fr " +
                "JOIN space_types st ON fr.type_id = st.type_id " +
                "ORDER BY fr.type_id, fr.effective_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rules.add(mapFeeRule(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rules;
    }

    public FeeRule getActiveFeeRuleByType(int typeId) {
        FeeRule rule = null;
        String sql = "SELECT fr.*, st.type_name FROM fee_rules fr " +
                "JOIN space_types st ON fr.type_id = st.type_id " +
                "WHERE fr.type_id = ? AND fr.is_active = TRUE " +
                "ORDER BY fr.effective_date DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, typeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rule = mapFeeRule(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rule;
    }

    private FeeRule mapFeeRule(ResultSet rs) throws SQLException {
        FeeRule rule = new FeeRule();
        rule.setRuleId(rs.getInt("rule_id"));
        rule.setTypeId(rs.getInt("type_id"));
        rule.setTypeName(rs.getString("type_name"));
        rule.setHourlyRate(rs.getDouble("hourly_rate"));
        rule.setDailyMax(rs.getDouble("daily_max"));
        rule.setActive(rs.getBoolean("is_active"));
        rule.setEffectiveDate(rs.getTimestamp("effective_date").toLocalDateTime());
        rule.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
        rule.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
        return rule;
    }
}