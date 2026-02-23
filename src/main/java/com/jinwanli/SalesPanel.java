package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SalesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public SalesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(UIUtils.COLOR_BG_MAIN);

        JButton addBtn = UIUtils.createButton("添加记录");
        addBtn.addActionListener(e -> {
            SalesDialog dialog = new SalesDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            SalesRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addSalesRecord(record);
                refreshTable();
            }
        });
        
        JButton deleteBtn = UIUtils.createSecondaryButton("删除选中");
        deleteBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                SalesRecord toDelete = getFilteredRecords().get(selected);
                DataManager.getInstance().getSalesRecords().remove(toDelete);
                DataManager.getInstance().saveSales();
                refreshTable();
            }
        });

        searchField = UIUtils.createTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setToolTipText("输入货主或经手人姓名搜索...");
        
        JButton searchBtn = UIUtils.createButton("查询");
        searchBtn.addActionListener(e -> refreshTable());
        
        JButton clearSearchBtn = UIUtils.createSecondaryButton("清除条件");
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        topPanel.add(new JLabel("  |  条件搜索:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(clearSearchBtn);

        String[] columnNames = {"日期", "货主", "单价(元/斤)", "每筐重量(斤)", "筐数(个)", "总重量(斤)", "总金额(元)", "经手人"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return true; }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                String newValue = aValue.toString().trim();
                SalesRecord record = getFilteredRecords().get(row);
                
                try {
                    switch (column) {
                        case 0: record.setDate(newValue); break;
                        case 1: record.setShipperName(newValue); break;
                        case 2: 
                            record.setUnitPrice(Double.parseDouble(newValue)); 
                            record.setTotalAmount(record.getTotalWeight() * record.getUnitPrice());
                            break;
                        case 3: 
                            record.setWeightPerBasket(Double.parseDouble(newValue)); 
                            record.setTotalWeight(record.getWeightPerBasket() * record.getBasketCount());
                            record.setTotalAmount(record.getTotalWeight() * record.getUnitPrice());
                            break;
                        case 4: 
                            record.setBasketCount(Integer.parseInt(newValue)); 
                            record.setTotalWeight(record.getWeightPerBasket() * record.getBasketCount());
                            record.setTotalAmount(record.getTotalWeight() * record.getUnitPrice());
                            break;
                        case 5: record.setTotalWeight(Double.parseDouble(newValue)); break;
                        case 6: record.setTotalAmount(Double.parseDouble(newValue)); break;
                        case 7: record.setHandler(newValue); break;
                    }
                    DataManager.getInstance().saveSales();
                    super.setValueAt(aValue, row, column);
                    
                    if (column >= 2 && column <= 4) {
                        super.setValueAt(String.format("%.2f", record.getTotalWeight()), row, 5);
                        super.setValueAt(String.format("%.2f", record.getTotalAmount()), row, 6);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "输入数字格式有误！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(UIUtils.FONT_NORMAL);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(UIUtils.COLOR_BG_HEADER);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        refreshTable();
    }

    private List<SalesRecord> getFilteredRecords() {
        List<SalesRecord> filtered = new ArrayList<>();
        String keyword = searchField.getText().trim();
        for (SalesRecord r : DataManager.getInstance().getSalesRecords()) {
            if (keyword.isEmpty() || r.getShipperName().contains(keyword) || r.getHandler().contains(keyword)) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    public void refreshTable() {
        model.setRowCount(0);
        for (SalesRecord r : getFilteredRecords()) {
            model.addRow(new Object[]{
                r.getDate(),
                r.getShipperName(),
                String.format("%.2f", r.getUnitPrice()),
                String.format("%.2f", r.getWeightPerBasket()),
                r.getBasketCount(),
                String.format("%.2f", r.getTotalWeight()),
                String.format("%.2f", r.getTotalAmount()),
                r.getHandler()
            });
        }
    }
}
