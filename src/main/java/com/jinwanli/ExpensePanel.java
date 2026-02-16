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

        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
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

        add(queryPanel, BorderLayout.NORTH);
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