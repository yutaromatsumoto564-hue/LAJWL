package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import com.jinwanli.model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        dialog.setSize(900, 550);
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
        
        JButton addBtn = UIUtils.createButton("添加员工");
        addBtn.addActionListener(e -> {
            EmployeeDialog employeeDialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            employeeDialog.setVisible(true);
            Employee employee = employeeDialog.getData();
            if (employee != null) {
                DataManager.getInstance().addEmployee(employee);
                dialog.dispose();
                showEmployeeSalaryDetailDialog();
            }
        });
        topPanel.add(addBtn, BorderLayout.SOUTH);
        
        // 表格区域
        String[] columnNames = {"姓名", "职位", "联系电话", "基本工资(元)", "绩效工资(元)", "加班补贴(元)", "预计总薪资(元)"};
        javax.swing.table.DefaultTableModel detailModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        for (Employee emp : DataManager.getInstance().getEmployees()) {
            detailModel.addRow(new Object[]{
                emp.getName(),
                emp.getPosition(),
                emp.getPhone(),
                String.format("%.2f", emp.getBaseSalary()),
                String.format("%.2f", emp.getPerformanceSalary()),
                String.format("%.2f", emp.getOvertimeSalary()),
                String.format("%.2f", emp.getTotalSalary())
            });
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
