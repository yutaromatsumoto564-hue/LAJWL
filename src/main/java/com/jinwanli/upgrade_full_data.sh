#!/bin/bash

# 1. 升级 UIUtils.java (增加年份生成方法)
echo "正在升级 UIUtils.java (增加动态年份支持)..."
cat > UIUtils.java <<EOF
package com.jinwanli;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;

public class UIUtils {
    // 统一定义配色方案
    public static final Color COLOR_PRIMARY = new Color(255, 215, 0); // 金色
    public static final Color COLOR_BG_MAIN = new Color(255, 255, 255);
    public static final Color COLOR_BG_TITLE = new Color(255, 248, 220);
    public static final Color COLOR_BG_CONTROL = new Color(240, 240, 240);
    
    // 统一定义字体
    public static final Font FONT_TITLE = new Font("微软雅黑", Font.BOLD, 24);
    public static final Font FONT_NORMAL = new Font("微软雅黑", Font.PLAIN, 14);
    public static final Font FONT_TAB = new Font("微软雅黑", Font.PLAIN, 16);

    /**
     * 动态生成年份列表
     * @return 返回当前年份的前2年到后2年 (共5年)
     */
    public static String[] getRecentYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            // 生成范围：当前年份 - 2 到 当前年份 + 2
            // 例如 2026年时，返回: 2024, 2025, 2026, 2027, 2028
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        return years;
    }

    /**
     * 创建标准样式的表格
     */
    public static JScrollPane createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格默认不可编辑
            }
        };
        JTable table = new JTable(model);
        table.setFont(FONT_NORMAL);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.getTableHeader().setBackground(COLOR_BG_CONTROL);
        table.setSelectionBackground(COLOR_PRIMARY);
        
        return new JScrollPane(table);
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NORMAL);
        btn.setFocusPainted(false);
        return btn;
    }

    public static JPanel createTitlePanel(String titleText) {
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(COLOR_BG_TITLE);
        titlePanel.add(titleLabel);
        return titlePanel;
    }
}
EOF

# 2. 更新 AttendancePanel.java (使用动态年份)
echo "正在更新 AttendancePanel.java..."
cat > AttendancePanel.java <<EOF
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
    
    // Components for Monthly Detail
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JComboBox<String> detailMonthBox;
    private JComboBox<String> detailYearBox;

    // Components for Monthly Stats
    private JTable statsTable;
    private DefaultTableModel statsModel;
    private JComboBox<String> statsMonthBox;
    private JComboBox<String> statsYearBox;

    // Components for Employee Mgmt
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

    // --- 1. 月考勤表 ---
    private JPanel createMonthlyDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        // 优化点：使用动态年份
        detailYearBox = new JComboBox<>(UIUtils.getRecentYears());
        detailYearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))); // 默认选中今年
        queryPanel.add(detailYearBox);
        
        queryPanel.add(new JLabel("月份:"));
        detailMonthBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        detailMonthBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1)); // 默认选中本月
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

        // 表格初始化
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

    // --- 2. 月统计 ---
    private JPanel createMonthlyStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        // 优化点：使用动态年份
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

    // --- 3. 员工管理 ---
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
EOF

# 3. 更新 SalesPanel.java (使用动态年份)
echo "正在更新 SalesPanel.java..."
cat > SalesPanel.java <<EOF
package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

public class SalesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> yearBox;
    private JComboBox<String> monthBox;

    public SalesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("销量统计管理"), BorderLayout.NORTH);

        // 表格列定义
        String[] columnNames = {"货主", "筐数", "每筐(斤)", "净重(斤)", "单价", "总金额", "日期"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UIUtils.FONT_NORMAL);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.getTableHeader().setBackground(UIUtils.COLOR_BG_CONTROL);
        
        // 查询栏
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        // 优化点：动态年份
        yearBox = new JComboBox<>(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(yearBox);
        
        queryPanel.add(new JLabel("月份:"));
        monthBox = new JComboBox<>(new String[]{"全部", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        queryPanel.add(monthBox);
        
        JButton queryBtn = UIUtils.createButton("查询");
        // 这里只是简单的刷新，实际项目可以根据 yearBox/monthBox 过滤 List<SalesRecord>
        queryBtn.addActionListener(e -> refreshData()); 
        queryPanel.add(queryBtn);

        refreshData(); 

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        JButton addBtn = UIUtils.createButton("添加记录");
        addBtn.addActionListener(e -> {
            SalesDialog dialog = new SalesDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            SalesRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addSalesRecord(record);
                refreshData();
            }
        });

        JButton delBtn = UIUtils.createButton("删除记录");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                if(JOptionPane.showConfirmDialog(this, "确认删除？") == JOptionPane.YES_OPTION) {
                    DataManager.getInstance().removeSalesRecord(row);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一行");
            }
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        add(queryPanel, BorderLayout.NORTH); // 添加查询栏
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        model.setRowCount(0);
        List<SalesRecord> list = DataManager.getInstance().getSalesRecords();
        // 实际过滤逻辑应在此处实现，目前显示所有数据
        for (SalesRecord r : list) {
            model.addRow(new Object[]{
                r.getShipperName(), r.getBasketCount(), r.getWeightPerBasket(),
                r.getNetWeight(), r.getPricePerJin(), r.getTotalAmount(), r.getDate()
            });
        }
    }
}
EOF

# 4. 更新 ExpensePanel.java (使用动态年份)
echo "正在更新 ExpensePanel.java..."
cat > ExpensePanel.java <<EOF
package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

public class ExpensePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> yearBox;

    public ExpensePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("开支管理"), BorderLayout.NORTH);

        String[] columnNames = {"日期", "分类", "金额", "用途", "经手人"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UIUtils.FONT_NORMAL);
        table.getTableHeader().setBackground(UIUtils.COLOR_BG_CONTROL);

        // 查询栏
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        // 优化点：动态年份
        yearBox = new JComboBox<>(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(yearBox);
        
        JButton queryBtn = UIUtils.createButton("查询");
        queryBtn.addActionListener(e -> refreshData());
        queryPanel.add(queryBtn);

        refreshData();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        JButton addBtn = UIUtils.createButton("添加支出");
        addBtn.addActionListener(e -> {
            ExpenseDialog dialog = new ExpenseDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            ExpenseRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addExpenseRecord(record);
                refreshData();
            }
        });

        JButton delBtn = UIUtils.createButton("删除支出");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                if(JOptionPane.showConfirmDialog(this, "确认删除？") == JOptionPane.YES_OPTION) {
                    DataManager.getInstance().removeExpenseRecord(row);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一行");
            }
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        add(queryPanel, BorderLayout.NORTH); // 添加查询栏
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        model.setRowCount(0);
        List<ExpenseRecord> list = DataManager.getInstance().getExpenseRecords();
        for (ExpenseRecord r : list) {
            model.addRow(new Object[]{r.getDate(), r.getCategory(), r.getAmount(), r.getUsage(), r.getHandler()});
        }
    }
}
EOF

echo "年份优化完成！请重启应用查看效果。"