package com.parkingmanagement.ui;

import com.parkingmanagement.dao.ParkingRecordDAO;
import com.parkingmanagement.dao.ParkingSpaceDAO;
import com.parkingmanagement.dao.VehicleDAO;
import com.parkingmanagement.model.ParkingRecord;
import com.parkingmanagement.model.ParkingSpace;
import com.parkingmanagement.model.Vehicle;
import com.parkingmanagement.service.ParkingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingRecordManagementPanel extends JPanel {
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private ParkingRecordDAO recordDAO;
    private VehicleDAO vehicleDAO;
    private ParkingSpaceDAO spaceDAO;
    private ParkingService parkingService;
    private JRadioButton allRecordsRadio;
    private JRadioButton activeRecordsRadio;
    private JTextField searchField;

    public ParkingRecordManagementPanel() {
        setLayout(new BorderLayout());
        recordDAO = new ParkingRecordDAO();
        vehicleDAO = new VehicleDAO();
        spaceDAO = new ParkingSpaceDAO();
        parkingService = new ParkingService();
        initializeFilterAndSearchPanel();
        initializeTable();
        initializeButtons();
        loadRecords(true, "");
    }

    private void initializeFilterAndSearchPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));

        // 筛选面板
        JPanel filterPanel = new JPanel();
        allRecordsRadio = new JRadioButton("所有记录");
        activeRecordsRadio = new JRadioButton("在场车辆");
        allRecordsRadio.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(allRecordsRadio);
        group.add(activeRecordsRadio);
        filterPanel.add(allRecordsRadio);
        filterPanel.add(activeRecordsRadio);

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("搜索:"));
        searchField = new JTextField(15);
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            loadRecords(allRecordsRadio.isSelected(), keyword);
        });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        allRecordsRadio.addActionListener(e -> loadRecords(true, searchField.getText().trim()));
        activeRecordsRadio.addActionListener(e -> loadRecords(false, searchField.getText().trim()));
    }

    private void initializeTable() {
        // 添加"支付状态"到列名中
        String[] columnNames = {"ID", "车牌号", "车主", "车位编号", "入场时间", "出场时间", "费用", "支付状态"};
        tableModel = new DefaultTableModel(columnNames, 0);
        recordTable = new JTable(tableModel);
        recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(recordTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeButtons() {
        JPanel buttonPanel = new JPanel();
        JButton checkInButton = new JButton("车辆入场");
        JButton checkOutButton = new JButton("车辆出场");
        JButton refreshButton = new JButton("刷新");
        JButton editButton = new JButton("修改记录");
        JButton deleteButton = new JButton("删除记录");

        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        checkInButton.addActionListener(e -> showCheckInDialog());
        checkOutButton.addActionListener(e -> handleCheckOut());
        refreshButton.addActionListener(e -> loadRecords(allRecordsRadio.isSelected(), searchField.getText().trim()));
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> handleDelete());
    }

    private void loadRecords(boolean showAll, String keyword) {
        tableModel.setRowCount(0);
        List<ParkingRecord> records;

        if (showAll) {
            records = recordDAO.getAllParkingRecords();
        } else {
            records = recordDAO.getActiveParkingRecords();
        }

        // 筛选搜索关键词
        if (!keyword.isEmpty()) {
            records = records.stream()
                    .filter(r -> r.getLicensePlate() != null && r.getLicensePlate().contains(keyword) ||
                            r.getSpaceNumber() != null && r.getSpaceNumber().contains(keyword))
                    .collect(Collectors.toList());
        }

        for (ParkingRecord record : records) {
            // 获取车主信息
            String ownerName = "";
            Vehicle vehicle = vehicleDAO.getVehicleById(record.getVehicleId());
            if (vehicle != null) {
                ownerName = vehicle.getOwnerName();
            }

            tableModel.addRow(new Object[]{
                    record.getRecordId(),
                    record.getLicensePlate(),
                    ownerName,
                    record.getSpaceNumber(),
                    formatDateTime(record.getEntryTime()),
                    formatDateTime(record.getExitTime()),
                    record.getFee() != null ? record.getFee() : "未结算",
                    record.getPaymentStatus() // 显示支付状态
            });
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    private void showCheckInDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "车辆入场", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JComboBox<Vehicle> vehicleComboBox = new JComboBox<>();
        JComboBox<ParkingSpace> spaceComboBox = new JComboBox<>();

        // 加载车辆数据
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
        vehicleComboBox.addItem(null); // 空选项
        for (Vehicle vehicle : vehicles) {
            vehicleComboBox.addItem(vehicle);
        }

        // 加载可用临时车位
        List<ParkingSpace> availableSpaces = spaceDAO.getAvailableParkingSpaces();
        spaceComboBox.addItem(null); // 空选项
        for (ParkingSpace space : availableSpaces) {
            spaceComboBox.addItem(space);
        }

        if (spaceComboBox.getItemCount() == 1) {
            JOptionPane.showMessageDialog(dialog, "没有可用车位!", "提示", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        panel.add(new JLabel("选择车辆:"));
        panel.add(vehicleComboBox);
        panel.add(new JLabel("选择车位:"));
        panel.add(spaceComboBox);

        JButton checkInButton = new JButton("确认入场");
        checkInButton.addActionListener(e -> {
            Vehicle selectedVehicle = (Vehicle) vehicleComboBox.getSelectedItem();
            ParkingSpace selectedSpace = (ParkingSpace) spaceComboBox.getSelectedItem();

            if (selectedVehicle == null) {
                JOptionPane.showMessageDialog(dialog, "请选择车辆！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedSpace == null) {
                JOptionPane.showMessageDialog(dialog, "请选择可用车位！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                parkingService.checkInVehicle(selectedVehicle.getVehicleId(), selectedSpace.getSpaceId());
                JOptionPane.showMessageDialog(dialog, "车辆入场成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadRecords(allRecordsRadio.isSelected(), searchField.getText().trim());
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "车辆入场失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(checkInButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleCheckOut() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow >= 0) {
            int recordId = (int) tableModel.getValueAt(selectedRow, 0);
            ParkingRecord record = recordDAO.getParkingRecordById(recordId);

            if (record.getExitTime() != null) {
                JOptionPane.showMessageDialog(this, "该车辆已出场！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "确认 " + record.getLicensePlate() + " 出场？", "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    double fee = parkingService.checkOutVehicle(recordId);
                    JOptionPane.showMessageDialog(this,
                            "车辆出场成功！\n停车费用：" + fee, "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadRecords(allRecordsRadio.isSelected(), searchField.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "车辆出场失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要出场的车辆！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showEditDialog() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow >= 0) {
            int recordId = (int) tableModel.getValueAt(selectedRow, 0);
            ParkingRecord record = recordDAO.getParkingRecordById(recordId);

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改停车记录", true);
            dialog.setSize(500, 450); // 调整高度以容纳新增的支付状态选项
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10)); // 增加为7行，包含支付状态行

            JTextField vehicleIdField = new JTextField(String.valueOf(record.getVehicleId()));
            JTextField spaceIdField = new JTextField(String.valueOf(record.getSpaceId()));
            JTextField entryTimeField = new JTextField(formatDateTime(record.getEntryTime()));
            JTextField exitTimeField = new JTextField(record.getExitTime() != null ? formatDateTime(record.getExitTime()) : "");
            JTextField feeField = new JTextField(record.getFee() != null ? String.valueOf(record.getFee()) : "");

            // 支付状态选择框
            JComboBox<String> paymentStatusCombo = new JComboBox<>(new String[]{"未支付", "已支付"});
            paymentStatusCombo.setSelectedItem(record.getPaymentStatus());

            panel.add(new JLabel("车辆ID:"));
            panel.add(vehicleIdField);
            panel.add(new JLabel("车位ID:"));
            panel.add(spaceIdField);
            panel.add(new JLabel("入场时间:"));
            panel.add(entryTimeField);
            panel.add(new JLabel("出场时间:"));
            panel.add(exitTimeField);
            panel.add(new JLabel("费用:"));
            panel.add(feeField);
            panel.add(new JLabel("支付状态:"));
            panel.add(paymentStatusCombo); // 添加支付状态选择项

            JButton saveButton = new JButton("保存修改");
            saveButton.addActionListener(e -> {
                try {
                    record.setVehicleId(Integer.parseInt(vehicleIdField.getText()));
                    record.setSpaceId(Integer.parseInt(spaceIdField.getText()));
                    record.setEntryTime(LocalDateTime.parse(entryTimeField.getText(),
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    if (!exitTimeField.getText().trim().isEmpty()) {
                        record.setExitTime(LocalDateTime.parse(exitTimeField.getText(),
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else {
                        record.setExitTime(null);
                    }

                    record.setFee(Double.parseDouble(feeField.getText()));
                    // 设置支付状态
                    record.setPaymentStatus((String) paymentStatusCombo.getSelectedItem());

                    if (recordDAO.updateParkingRecord(record)) {
                        JOptionPane.showMessageDialog(dialog, "停车记录修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadRecords(allRecordsRadio.isSelected(), searchField.getText().trim());
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "修改失败，请检查数据！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "修改失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(saveButton, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "请选择要修改的记录！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = recordTable.getSelectedRow();
        if (selectedRow >= 0) {
            int recordId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确认删除该停车记录？", "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (recordDAO.deleteParkingRecord(recordId)) {
                    JOptionPane.showMessageDialog(this, "停车记录删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadRecords(allRecordsRadio.isSelected(), searchField.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(this, "删除失败，请重试！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
}