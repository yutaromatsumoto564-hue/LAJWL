package com.jinwanli;

import com.jinwanli.model.AttendanceRecord;
import com.jinwanli.model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class AttendancePanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JComboBox<String> detailMonthBox;
    private JComboBox<String> detailYearBox;

    private JTable statsTable;
    private DefaultTableModel statsModel;
    private JComboBox<String> statsMonthBox;
    private JComboBox<String> statsYearBox;

    private JTable empTable;
    private DefaultTableModel empModel;

    public AttendancePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("员工考勤管理"), BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_TAB);
        
        tabbedPane.addTab("月考勤表", createMonthlyDetailPanel());
        tabbedPane.addTab("月统计", createMonthlyStatsPanel());
        tabbedPane.addTab("员工管理", createEmployeePanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMonthlyDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        detailYearBox = new JComboBox<>(UIUtils.getRecentYears());
        detailYearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(detailYearBox);
        
        queryPanel.add(new JLabel("月份:"));
        detailMonthBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        detailMonthBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        queryPanel.add(detailMonthBox);
        
        JButton queryBtn = UIUtils.createButton("查询");
        queryBtn.addActionListener(e -> refreshDetailTable());
        queryPanel.add(queryBtn);
        
        JButton addRecordBtn = UIUtils.createButton("录入考勤");
        addRecordBtn.addActionListener(e -> {
            AttendanceDialog dialog = new AttendanceDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            AttendanceRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addAttendanceRecord(record);
                refreshDetailTable();
                refreshStatsTable();
            }
        });
        queryPanel.add(addRecordBtn);

        String[] columns = new String[33];
        columns[0] = "员工姓名";
        for(int i=1; i<=31; i++) columns[i] = String.valueOf(i);
        columns[32] = "ID(隐藏)";

        detailModel = new DefaultTableModel(columns, 0);
        detailTable = new JTable(detailModel);
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        for(int i=1; i<=31; i++) detailTable.getColumnModel().getColumn(i).setPreferredWidth(40);
        
        panel.add(queryPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        
        refreshDetailTable();
        return panel;
    }

    private void refreshDetailTable() {
        detailModel.setRowCount(0);
        String year = (String) detailYearBox.getSelectedItem();
        String month = (String) detailMonthBox.getSelectedItem();
        
        List<Employee> employees = DataManager.getInstance().getEmployees();
        List<AttendanceRecord> records = DataManager.getInstance().getAttendanceByMonth(year, month);
        
        for (Employee emp : employees) {
            Object[] row = new Object[33];
            row[0] = emp.getName();
            row[32] = emp.getId();
            
            List<AttendanceRecord> empRecords = records.stream()
                .filter(r -> r.getEmployeeId().equals(emp.getId()))
                .collect(Collectors.toList());
            
            for (AttendanceRecord r : empRecords) {
                int day = r.getDay();
                if (day >= 1 && day <= 31) {
                    String symbol = "√";
                    if(r.getStatus().equals("迟到")) symbol = "迟";
                    else if(r.getStatus().equals("早退")) symbol = "退";
                    else if(r.getStatus().equals("缺勤")) symbol = "X";
                    row[day] = symbol;
                }
            }
            detailModel.addRow(row);
        }
    }

    private JPanel createMonthlyStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        statsYearBox = new JComboBox<>(UIUtils.getRecentYears());
        statsYearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(statsYearBox);
        
        queryPanel.add(new JLabel("月份:"));
        statsMonthBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        statsMonthBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        queryPanel.add(statsMonthBox);
        
        JButton queryBtn = UIUtils.createButton("计算统计");
        queryBtn.addActionListener(e -> refreshStatsTable());
        queryPanel.add(queryBtn);

        String[] columns = {"工号", "姓名", "出勤天数", "迟到次数", "早退次数", "缺勤天数", "加班(小时)"};
        statsModel = new DefaultTableModel(columns, 0);
        statsTable = new JTable(statsModel);
        statsTable.setRowHeight(30);
        
        panel.add(queryPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(statsTable), BorderLayout.CENTER);
        
        refreshStatsTable();
        return panel;
    }

    private void refreshStatsTable() {
        statsModel.setRowCount(0);
        String year = (String) statsYearBox.getSelectedItem();
        String month = (String) statsMonthBox.getSelectedItem();
        
        List<Employee> employees = DataManager.getInstance().getEmployees();
        List<AttendanceRecord> records = DataManager.getInstance().getAttendanceByMonth(year, month);
        
        for (Employee emp : employees) {
            List<AttendanceRecord> empRecords = records.stream()
                .filter(r -> r.getEmployeeId().equals(emp.getId()))
                .collect(Collectors.toList());
            
            long presentDays = empRecords.stream().filter(r -> !r.getStatus().equals("缺勤")).count();
            long lateCount = empRecords.stream().filter(r -> r.getStatus().equals("迟到")).count();
            long earlyCount = empRecords.stream().filter(r -> r.getStatus().equals("早退")).count();
            long absentCount = empRecords.stream().filter(r -> r.getStatus().equals("缺勤")).count();
            double overtimeTotal = empRecords.stream().mapToDouble(AttendanceRecord::getOvertimeHours).sum();
            
            statsModel.addRow(new Object[]{
                emp.getId(), emp.getName(), presentDays, lateCount, earlyCount, absentCount, overtimeTotal
            });
        }
    }

    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        String[] columnNames = {"工号", "姓名", "职位", "基本工资", "总工资", "状态"};
        empModel = new DefaultTableModel(columnNames, 0);
        empTable = new JTable(empModel);
        empTable.setRowHeight(30);
        empTable.getTableHeader().setBackground(UIUtils.COLOR_BG_CONTROL);

        refreshEmpData();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        JButton addBtn = UIUtils.createButton("添加员工");
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("请输入员工姓名:");
            if (name != null && !name.trim().isEmpty()) {
                Employee newEmp = new Employee("E" + System.currentTimeMillis()%1000, name, "员工", 5000, 0, 0);
                DataManager.getInstance().addEmployee(newEmp);
                refreshEmpData();
                refreshDetailTable();
                refreshStatsTable();
            }
        });
        JButton delBtn = UIUtils.createButton("删除员工");
        delBtn.addActionListener(e -> {
            int row = empTable.getSelectedRow();
            if (row >= 0) {
                DataManager.getInstance().removeEmployee(row);
                refreshEmpData();
                refreshDetailTable();
                refreshStatsTable();
            }
        });
        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        panel.add(new JScrollPane(empTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshEmpData() {
        empModel.setRowCount(0);
        List<Employee> list = DataManager.getInstance().getEmployees();
        for (Employee e : list) {
            empModel.addRow(new Object[]{e.getId(), e.getName(), e.getPosition(), e.getBaseSalary(), e.getTotalSalary(), e.getStatus()});
        }
    }
}