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

        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        queryPanel.setBackground(UIUtils.COLOR_BG_CARD);
        queryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER));
        
        queryPanel.add(new JLabel("年份:"));
        yearBox = UIUtils.createComboBox(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(yearBox);
        
        JButton queryBtn = UIUtils.createButton("查询");
        queryBtn.addActionListener(e -> refreshData());
        queryPanel.add(queryBtn);
        
        add(queryPanel, BorderLayout.NORTH);

        String[] columnNames = {"日期", "分类", "关联项目", "金额", "用途", "经手人"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(UIUtils.FONT_BODY);
        table.setSelectionBackground(UIUtils.COLOR_PRIMARY_LIGHT);
        table.setSelectionForeground(UIUtils.COLOR_PRIMARY);
        
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UIUtils.COLOR_BG_MAIN);
        table.getTableHeader().setForeground(UIUtils.COLOR_TEXT_SECONDARY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UIUtils.COLOR_BG_CARD);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton addBtn = UIUtils.createButton("添加支出");
        addBtn.addActionListener(e -> {
            ExpenseDialog dialog = new ExpenseDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            ExpenseRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addExpenseRecord(record);
                refreshData();
            }
        });

        JButton editBtn = UIUtils.createButton("编辑记录");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                ExpenseRecord existing = DataManager.getInstance().getExpenseRecords().get(row);
                ExpenseDialog dialog = new ExpenseDialog((JFrame) SwingUtilities.getWindowAncestor(this), existing);
                dialog.setVisible(true);
                ExpenseRecord updatedRecord = dialog.getData();
                if (updatedRecord != null) {
                    DataManager.getInstance().updateExpenseRecord(row, updatedRecord);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择需要编辑的行", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton delBtn = UIUtils.createDangerButton("删除支出");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                if(JOptionPane.showConfirmDialog(this, "确认删除？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DataManager.getInstance().removeExpenseRecord(row);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一行", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(delBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshData();
    }

    private void refreshData() {
        model.setRowCount(0);
        List<ExpenseRecord> list = DataManager.getInstance().getExpenseRecords();
        for (ExpenseRecord r : list) {
            model.addRow(new Object[]{
                r.getDate(), 
                r.getCategory(), 
                r.getTargetProject() != null ? r.getTargetProject() : "-", 
                String.format("%.2f", r.getAmount()), 
                r.getUsage(), 
                r.getHandler()
            });
        }
    }
}