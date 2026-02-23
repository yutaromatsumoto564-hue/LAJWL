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

        String[] columnNames = {"客户/收货方", "商品/型号", "单价(元)", "数量", "总金额(元)", "经手人", "日期"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                String newValue = aValue.toString().trim();
                SalesRecord record = DataManager.getInstance().getSalesRecords().get(row);
                
                try {
                    switch (column) {
                        case 0: record.setShipperName(newValue); break;
                        case 1: record.setProductName(newValue); break;
                        case 2: record.setPricePerJin(Double.parseDouble(newValue)); break;
                        case 3: record.setBasketCount(Integer.parseInt(newValue)); break;
                        case 4: break;
                        case 5: record.setHandler(newValue); break;
                        case 6: record.setDate(newValue); break;
                    }
                    DataManager.getInstance().updateSalesRecord(row, record);
                    super.setValueAt(aValue, row, column);
                    
                    if (column == 2 || column == 3) {
                        super.setValueAt(String.format("%.2f", record.getTotalAmount()), row, 4);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "输入数字格式有误！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
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
        
        JButton printBtn = UIUtils.createButton("打印/预览单据");
        printBtn.setForeground(new Color(0, 102, 204));
        printBtn.addActionListener(e -> previewSelectedRecord());

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(printBtn);

        add(queryPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        model.setRowCount(0);
        List<SalesRecord> list = DataManager.getInstance().getSalesRecords();
        for (SalesRecord r : list) {
            model.addRow(new Object[]{
                r.getShipperName(), r.getProductName(), r.getPricePerJin(),
                r.getBasketCount(), r.getTotalAmount(), r.getHandler(), r.getDate()
            });
        }
    }

    private void previewSelectedRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条销售记录进行打印！");
            return;
        }

        String customer = table.getValueAt(row, 0).toString();
        String product = table.getValueAt(row, 1).toString();
        String price = table.getValueAt(row, 2).toString();
        String qty = table.getValueAt(row, 3).toString();
        String total = table.getValueAt(row, 4).toString();
        String handler = table.getValueAt(row, 5).toString();
        String date = table.getValueAt(row, 6).toString();

        Map<String, String> content = new LinkedHashMap<>();
        content.put("客户名称:", customer);
        content.put("交易日期:", date);
        content.put("经手人:", handler);
        content.put("----------------", "--------------------");
        content.put("商品名称:", product);
        content.put("销售单价:", price + " 元");
        content.put("销售数量:", qty);
        content.put("----------------", "--------------------");
        content.put("合计金额:", total + " 元");

        String footer = "金万里企业管理系统\n联系电话: 138-xxxx-xxxx\n谢谢惠顾！";

        PdfUtils.generateAndOpenPdf("Sales_" + date, "金万里 - 销售出货单", content, footer);
    }
}