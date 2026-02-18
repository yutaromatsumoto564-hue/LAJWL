package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesDialog extends JDialog {
    private JTextField shipperField;
    private JTextField basketField;
    private JTextField weightField;
    private JTextField priceField;
    private JTextField dateField;
    private boolean confirmed = false;

    public SalesDialog(JFrame parent) {
        super(parent, "添加销售记录", true);
        setLayout(new BorderLayout());
        setSize(380, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        // 标题
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("添加销售记录");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // 表单
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 货主名称
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("货主名称:"), gbc);
        shipperField = UIUtils.createTextField();
        gbc.gridx = 1;
        formPanel.add(shipperField, gbc);
        
        // 筐数
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("筐数:"), gbc);
        basketField = UIUtils.createTextField();
        basketField.setText("0");
        gbc.gridx = 1;
        formPanel.add(basketField, gbc);
        
        // 每筐斤数
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("每筐斤数:"), gbc);
        weightField = UIUtils.createTextField();
        weightField.setText("0");
        gbc.gridx = 1;
        formPanel.add(weightField, gbc);
        
        // 单价
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("单价(元/斤):"), gbc);
        priceField = UIUtils.createTextField();
        priceField.setText("0");
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        // 日期
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("日期(YYYY-MM-DD):"), gbc);
        dateField = UIUtils.createTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if(shipperField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入货主名称！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            confirmed = true;
            setVisible(false);
        });
        
        JButton cancelBtn = UIUtils.createSecondaryButton("取消");
        cancelBtn.setPreferredSize(new Dimension(100, 36));
        cancelBtn.addActionListener(e -> setVisible(false));
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public SalesRecord getData() {
        if (!confirmed) return null;
        try {
            String shipper = shipperField.getText().trim();
            int baskets = Integer.parseInt(basketField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String date = dateField.getText().trim();
            
            return new SalesRecord(shipper, baskets, weight, price, date);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "输入格式错误，请检查数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
