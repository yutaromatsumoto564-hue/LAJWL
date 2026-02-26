package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import com.jinwanli.model.Employee;
import com.jinwanli.model.MonthlySalaryRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ExpensePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ExpensePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);

        // 顶部按钮面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(UIUtils.COLOR_BG_MAIN);

        JButton addBtn = UIUtils.createButton("添加财务记录");
        addBtn.addActionListener(e -> {
            ExpenseDialog dialog = new ExpenseDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            ExpenseRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addExpenseRecord(record);
                refreshTable();
            }
        });

        JButton deleteBtn = UIUtils.createSecondaryButton("删除选中");
        deleteBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                DataManager.getInstance().getExpenseRecords().remove(selected);
                DataManager.getInstance().saveExpenses();
                refreshTable();
            }
        });

        topPanel.add(addBtn);
        topPanel.add(deleteBtn);

        // 分类卡片面板
        JPanel categoryPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        categoryPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 收支分类列表
        String[] categories = {
            "材料采购", "车旅费", "伙食费", "电费", "项目投资",
            "其他支出", "股东注资(收入)", "政府补贴(收入)", "其他(收入)", "员工工资"
        };

        // 创建分类卡片
        for (String category : categories) {
            JPanel card = createCategoryCard(category);
            categoryPanel.add(card);
        }

        // 表格区域
        String[] columnNames = {"日期", "收支分类", "金额(元)", "用途/备注", "经手人"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return true; }
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                String newValue = aValue.toString().trim();
                ExpenseRecord record = DataManager.getInstance().getExpenseRecords().get(row);
                try {
                    switch (column) {
                        case 0: record.setDate(newValue); break;
                        case 1: record.setCategory(newValue); break;
                        case 2: record.setAmount(Double.parseDouble(newValue)); break;
                        case 3: record.setUsage(newValue); break;
                        case 4: record.setHandler(newValue); break;
                    }
                    DataManager.getInstance().updateExpenseRecord(row, record);
                    super.setValueAt(aValue, row, column);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "输入格式有误！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(UIUtils.FONT_NORMAL);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String category = (String) table.getValueAt(row, 1);
                if (category != null && category.contains("(收入)")) {
                    c.setForeground(new Color(16, 185, 129));
                } else {
                    c.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
                }
                if (isSelected) c.setForeground(table.getSelectionForeground());
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        
        // 组装面板
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(categoryPanel, BorderLayout.CENTER);
        
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        refreshTable();
    }

    // 创建分类卡片
    private JPanel createCategoryCard(String category) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(UIUtils.COLOR_BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(120, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 卡片标题
        JLabel titleLabel = new JLabel(category);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.CENTER);
        
        // 鼠标点击事件
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCategoryDetailDialog(category);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 240, 240));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(UIUtils.COLOR_BG_CARD);
            }
        });
        
        return card;
    }
    
    // 显示分类详情对话框
    private void showCategoryDetailDialog(String category) {
        // 特殊处理员工工资卡片
        if ("员工工资".equals(category)) {
            showEmployeeSalaryDetailDialog();
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), category + " 明细", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(category + " 记录明细");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JButton addBtn = UIUtils.createButton("添加" + category + "记录");
        addBtn.addActionListener(e -> {
            ExpenseDialog expenseDialog = new ExpenseDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            expenseDialog.setDefaultCategory(category);
            expenseDialog.setVisible(true);
            ExpenseRecord record = expenseDialog.getData();
            if (record != null) {
                DataManager.getInstance().addExpenseRecord(record);
                refreshTable();
                dialog.dispose();
                showCategoryDetailDialog(category);
            }
        });
        topPanel.add(addBtn, BorderLayout.SOUTH);
        
        // 表格区域
        String[] columnNames = {"日期", "金额(元)", "用途/备注", "经手人"};
        javax.swing.table.DefaultTableModel detailModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        for (ExpenseRecord r : DataManager.getInstance().getExpenseRecords()) {
            if (category.equals(r.getCategory())) {
                detailModel.addRow(new Object[]{
                    r.getDate(),
                    String.format("%.2f", r.getAmount()),
                    r.getUsage(),
                    r.getHandler()
                });
            }
        }
        
        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(36);
        detailTable.setFont(UIUtils.FONT_NORMAL);
        detailTable.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        
        JScrollPane scrollPane = new JScrollPane(detailTable);
        
        // 底部按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton closeBtn = UIUtils.createSecondaryButton("关闭");
        closeBtn.addActionListener(e -> dialog.setVisible(false));
        btnPanel.add(closeBtn);
        
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // 显示员工工资详情对话框
    private void showEmployeeSalaryDetailDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "员工工资明细", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("员工工资明细");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 月份选择和添加按钮面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        JLabel monthLabel = new JLabel("选择月份:");
        monthLabel.setFont(UIUtils.FONT_BODY);
        controlPanel.add(monthLabel);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.List<String> months = new java.util.ArrayList<>();
        for (int i = 0; i < 12; i++) {
            java.util.Calendar c = (java.util.Calendar) cal.clone();
            c.add(java.util.Calendar.MONTH, -i);
            months.add(sdf.format(c.getTime()));
        }
        
        JComboBox<String> monthBox = new JComboBox<>(months.toArray(new String[0]));
        monthBox.setFont(UIUtils.FONT_BODY);
        controlPanel.add(monthBox);
        
        JButton addBtn = UIUtils.createButton("添加月度工资");
        addBtn.addActionListener(e -> {
            MonthlySalaryDialog salaryDialog = new MonthlySalaryDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            salaryDialog.setVisible(true);
            MonthlySalaryRecord record = salaryDialog.getData();
            if (record != null) {
                DataManager.getInstance().addMonthlySalaryRecord(record);
                dialog.dispose();
                showEmployeeSalaryDetailDialog();
            }
        });
        controlPanel.add(addBtn);
        
        topPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // 表格区域
        String[] columnNames = {"月份", "员工姓名", "职位", "基本工资(元)", "绩效工资(元)", "加班补贴(元)", "总工资(元)", "状态"};
        javax.swing.table.DefaultTableModel detailModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { 
                return col != 7; // 状态列不可编辑
            }
        };
        
        // 添加表格数据
        refreshSalaryTable(detailModel, (String) monthBox.getSelectedItem());
        
        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(36);
        detailTable.setFont(UIUtils.FONT_NORMAL);
        detailTable.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        
        // 添加表格双击事件监听器，双击状态列自动切换状态
        detailTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击
                    int row = detailTable.rowAtPoint(e.getPoint());
                    int col = detailTable.columnAtPoint(e.getPoint());
                    if (col == 7) { // 状态列
                        String month = (String) detailTable.getValueAt(row, 0);
                        String employeeName = (String) detailTable.getValueAt(row, 1);
                        String currentStatus = (String) detailTable.getValueAt(row, 7);
                        
                        // 查找对应的记录
                        List<MonthlySalaryRecord> records = DataManager.getInstance().getMonthlySalaryRecords();
                        for (int i = 0; i < records.size(); i++) {
                            MonthlySalaryRecord record = records.get(i);
                            if (record.getMonth().equals(month) && record.getEmployeeName().equals(employeeName)) {
                                // 切换状态
                                String newStatus = "已发放".equals(currentStatus) ? "未发放" : "已发放";
                                record.setStatus(newStatus);
                                DataManager.getInstance().updateMonthlySalaryRecord(i, record);
                                
                                // 更新表格
                                detailTable.setValueAt(newStatus, row, 7);
                                JOptionPane.showMessageDialog(dialog, "状态已修改为：" + newStatus, "成功", JOptionPane.INFORMATION_MESSAGE);
                                
                                // 刷新经营总览
                                MainFrame mainFrame = MainFrame.getInstance();
                                if (mainFrame != null) {
                                    SummaryPanel summaryPanel = mainFrame.getSummaryPanel();
                                    if (summaryPanel != null) {
                                        summaryPanel.refreshData();
                                    }
                                }
                                
                                // 阻止事件继续传播，避免进入编辑模式
                                e.consume();
                                return;
                            }
                        }
                    }
                }
            }
        });
        
        // 添加表格编辑监听器
        detailTable.getModel().addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    Object newValue = detailModel.getValueAt(row, col);
                    
                    // 薪资明细编辑处理
                    String monthVal = (String) detailModel.getValueAt(row, 0);
                    String nameVal = (String) detailModel.getValueAt(row, 1);
                    
                    // 查找对应的月度工资记录
                    List<MonthlySalaryRecord> records = DataManager.getInstance().getMonthlySalaryRecords();
                    for (int i = 0; i < records.size(); i++) {
                        MonthlySalaryRecord record = records.get(i);
                        if (record.getMonth().equals(monthVal) && record.getEmployeeName().equals(nameVal)) {
                            // 根据列索引更新不同字段
                            switch (col) {
                                case 0: // 月份
                                    record.setMonth((String) newValue);
                                    break;
                                case 1: // 姓名
                                    record.setEmployeeName((String) newValue);
                                    break;
                                case 2: // 职位
                                    record.setEmployeePosition((String) newValue);
                                    break;
                                case 3: // 基本工资
                                    try {
                                        double baseSalary = Double.parseDouble(newValue.toString());
                                        record.setBaseSalary(baseSalary);
                                        record.setTotalSalary(baseSalary + record.getPerformanceSalary() + record.getOvertimeSalary());
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    break;
                                case 4: // 绩效工资
                                    try {
                                        double perfSalary = Double.parseDouble(newValue.toString());
                                        record.setPerformanceSalary(perfSalary);
                                        record.setTotalSalary(record.getBaseSalary() + perfSalary + record.getOvertimeSalary());
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    break;
                                case 5: // 加班补贴
                                    try {
                                        double overtime = Double.parseDouble(newValue.toString());
                                        record.setOvertimeSalary(overtime);
                                        record.setTotalSalary(record.getBaseSalary() + record.getPerformanceSalary() + overtime);
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    break;
                                case 7: // 状态
                                    record.setStatus((String) newValue);
                                    break;
                            }
                            // 更新记录
                            DataManager.getInstance().updateMonthlySalaryRecord(i, record);
                            // 更新总薪资列
                            if (col >= 3 && col <= 5) {
                                detailModel.setValueAt(String.format("%.2f", record.getTotalSalary()), row, 6);
                            }
                            
                            // 刷新经营总览
                            MainFrame mainFrame = MainFrame.getInstance();
                            if (mainFrame != null) {
                                SummaryPanel summaryPanel = mainFrame.getSummaryPanel();
                                if (summaryPanel != null) {
                                    summaryPanel.refreshData();
                                }
                            }
                            
                            break;
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(detailTable);
        
        // 月份选择变化时刷新表格
        monthBox.addActionListener(e -> {
            refreshSalaryTable(detailModel, (String) monthBox.getSelectedItem());
        });
        
        // 底部按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton deleteBtn = UIUtils.createSecondaryButton("删除记录");
        deleteBtn.addActionListener(e -> {
            int selectedRow = detailTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(dialog, "确定要删除这条记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String month = (String) detailTable.getValueAt(selectedRow, 0);
                    String employeeName = (String) detailTable.getValueAt(selectedRow, 1);
                    
                    // 查找对应的记录
                    List<MonthlySalaryRecord> records = DataManager.getInstance().getMonthlySalaryRecords();
                    for (int i = 0; i < records.size(); i++) {
                        MonthlySalaryRecord record = records.get(i);
                        if (record.getMonth().equals(month) && record.getEmployeeName().equals(employeeName)) {
                            // 删除记录
                            DataManager.getInstance().deleteMonthlySalaryRecord(i);
                            
                            // 更新表格
                            detailModel.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(dialog, "记录已删除", "成功", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "请选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnPanel.add(deleteBtn);
        
        JButton changeStatusBtn = UIUtils.createButton("修改状态");
        changeStatusBtn.addActionListener(e -> {
            int selectedRow = detailTable.getSelectedRow();
            if (selectedRow >= 0) {
                String month = (String) detailTable.getValueAt(selectedRow, 0);
                String employeeName = (String) detailTable.getValueAt(selectedRow, 1);
                String currentStatus = (String) detailTable.getValueAt(selectedRow, 7);
                
                // 查找对应的记录
                List<MonthlySalaryRecord> records = DataManager.getInstance().getMonthlySalaryRecords();
                for (int i = 0; i < records.size(); i++) {
                    MonthlySalaryRecord record = records.get(i);
                    if (record.getMonth().equals(month) && record.getEmployeeName().equals(employeeName)) {
                        // 切换状态
                        String newStatus = "未发放".equals(currentStatus) ? "已发放" : "未发放";
                        record.setStatus(newStatus);
                        DataManager.getInstance().updateMonthlySalaryRecord(i, record);
                        
                        // 更新表格
                        detailTable.setValueAt(newStatus, selectedRow, 7);
                        JOptionPane.showMessageDialog(dialog, "状态已修改为：" + newStatus, "成功", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "请选择要修改状态的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnPanel.add(changeStatusBtn);
        
        JButton closeBtn = UIUtils.createSecondaryButton("关闭");
        closeBtn.addActionListener(e -> dialog.setVisible(false));
        btnPanel.add(closeBtn);
        
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // 刷新工资表格
    private void refreshSalaryTable(javax.swing.table.DefaultTableModel model, String selectedMonth) {
        model.setRowCount(0);
        for (MonthlySalaryRecord record : DataManager.getInstance().getMonthlySalaryRecords()) {
            if (selectedMonth == null || selectedMonth.equals(record.getMonth())) {
                model.addRow(new Object[]{
                    record.getMonth(),
                    record.getEmployeeName(),
                    record.getEmployeePosition(),
                    String.format("%.2f", record.getBaseSalary()),
                    String.format("%.2f", record.getPerformanceSalary()),
                    String.format("%.2f", record.getOvertimeSalary()),
                    String.format("%.2f", record.getTotalSalary()),
                    record.getStatus()
                });
            }
        }
    }

    public void refreshTable() {
        model.setRowCount(0);
        for (ExpenseRecord r : DataManager.getInstance().getExpenseRecords()) {
            model.addRow(new Object[]{
                r.getDate(), r.getCategory(), 
                String.format("%.2f", r.getAmount()), r.getUsage(), r.getHandler()
            });
        }
    }
}
