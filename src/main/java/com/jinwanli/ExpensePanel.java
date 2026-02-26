package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ExpensePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ExpensePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);

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
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        refreshTable();
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
