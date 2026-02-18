package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SalesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> yearBox;
    private JComboBox<String> monthBox;

    public SalesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("销量统计管理"), BorderLayout.NORTH);

        // 查询工具栏
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        queryPanel.setBackground(UIUtils.COLOR_BG_CARD);
        queryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER));
        
        queryPanel.add(new JLabel("年份:"));
        yearBox = UIUtils.createComboBox(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        queryPanel.add(yearBox);
        
        queryPanel.add(new JLabel("月份:"));
        monthBox = UIUtils.createComboBox(new String[]{"全部", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        queryPanel.add(monthBox);
        
        JButton queryBtn = UIUtils.createButton("查询");
        queryBtn.addActionListener(e -> refreshData()); 
        queryPanel.add(queryBtn);
        
        add(queryPanel, BorderLayout.NORTH);

        // 表格
        String[] columnNames = {"货主", "筐数", "每筐(斤)", "净重(斤)", "单价", "总金额", "日期"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(UIUtils.FONT_BODY);
        table.setSelectionBackground(UIUtils.COLOR_PRIMARY_LIGHT);
        table.setSelectionForeground(UIUtils.COLOR_PRIMARY);
        
        // 表头样式
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UIUtils.COLOR_BG_MAIN);
        table.getTableHeader().setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        
        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(UIUtils.COLOR_BG_CARD);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
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

        JButton delBtn = UIUtils.createDangerButton("删除记录");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                if(JOptionPane.showConfirmDialog(this, "确认删除？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DataManager.getInstance().removeSalesRecord(row);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一行", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton printBtn = UIUtils.createButton("打印/预览单据");
        printBtn.addActionListener(e -> previewSelectedRecord());

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(printBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshData(); 
    }

    private void refreshData() {
        model.setRowCount(0);
        List<SalesRecord> list = DataManager.getInstance().getSalesRecords();
        for (SalesRecord r : list) {
            model.addRow(new Object[]{
                r.getShipperName(), r.getBasketCount(), r.getWeightPerBasket(),
                r.getNetWeight(), r.getPricePerJin(), String.format("%.2f", r.getTotalAmount()), r.getDate()
            });
        }
    }

    private void previewSelectedRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条销售记录进行打印！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipper = table.getValueAt(row, 0).toString();
        String baskets = table.getValueAt(row, 1).toString();
        String weightPer = table.getValueAt(row, 2).toString();
        String netWeight = table.getValueAt(row, 3).toString();
        String price = table.getValueAt(row, 4).toString();
        String total = table.getValueAt(row, 5).toString();
        String date = table.getValueAt(row, 6).toString();

        Map<String, String> content = new LinkedHashMap<>();
        content.put("客户名称:", shipper);
        content.put("交易日期:", date);
        content.put("----------------", "--------------------");
        content.put("商品筐数:", baskets + " 筐");
        content.put("单筐重量:", weightPer + " 斤");
        content.put("商品净重:", netWeight + " 斤");
        content.put("销售单价:", price + " 元/斤");
        content.put("----------------", "--------------------");
        content.put("合计金额:", "￥ " + total);

        String footer = "金万里企业管理系统\n联系电话: 138-xxxx-xxxx\n谢谢惠顾！";

        PdfUtils.generateAndOpenPdf("Sales_" + date, "金万里 - 销售出货单", content, footer);
    }
}
