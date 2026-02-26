package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
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
            "其他支出", "股东注资(收入)", "政府补贴(收入)", "其他(收入)"
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
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIUtils.COLOR_BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(UIUtils.COLOR_BORDER, 1));
        card.setPreferredSize(new Dimension(120, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 卡片标题
        JLabel titleLabel = new JLabel(category);
        titleLabel.setFont(UIUtils.FONT_BODY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.CENTER);
        
        // 鼠标点击事件
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ExpenseDialog dialog = new ExpenseDialog((JFrame) SwingUtilities.getWindowAncestor(ExpensePanel.this), null);
                // 设置默认分类
                dialog.setDefaultCategory(category);
                dialog.setVisible(true);
                ExpenseRecord record = dialog.getData();
                if (record != null) {
                    DataManager.getInstance().addExpenseRecord(record);
                    refreshTable();
                }
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
