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

        String[] columnNames = {"货主", "筐数", "每筐(斤)", "净重(斤)", "单价", "总金额", "日期"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UIUtils.FONT_NORMAL);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.getTableHeader().setBackground(UIUtils.COLOR_BG_CONTROL);
        
        JPanel queryPanel = new JPanel();
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        queryPanel.add(new JLabel("年份:"));
        yearBox = new JComboBox<>(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(yearBox);
        
        queryPanel.add(new JLabel("月份:"));
        monthBox = new JComboBox<>(new String[]{"全部", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        queryPanel.add(monthBox);
        
        JButton queryBtn = UIUtils.createButton("查询");
        queryBtn.addActionListener(e -> refreshData()); 
        queryPanel.add(queryBtn);

        refreshData(); 

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

        add(queryPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        model.setRowCount(0);
        List<SalesRecord> list = DataManager.getInstance().getSalesRecords();
        for (SalesRecord r : list) {
            model.addRow(new Object[]{
                r.getShipperName(), r.getBasketCount(), r.getWeightPerBasket(),
                r.getNetWeight(), r.getPricePerJin(), r.getTotalAmount(), r.getDate()
            });
        }
    }
}