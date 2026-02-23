package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesDialog extends JDialog {
    private JTextField shipperField, priceField, weightField, basketField, handlerField, dateField;
    private boolean confirmed = false;

    public SalesDialog(JFrame parent) {
        super(parent, "新增销量记录", true);
        setLayout(new BorderLayout());
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("录入销量记录");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 5, 12, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        shipperField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "货主:", shipperField);
        
        priceField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "单价(元/斤):", priceField);
        
        weightField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "每筐重量(斤):", weightField);
        
        basketField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "筐数(个):", basketField);
        
        handlerField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "经手人:", handlerField);
        
        dateField = UIUtils.createTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        addFormRow(formPanel, gbc, row++, "日期:", dateField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));

        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if(shipperField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "必填项不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }

    public SalesRecord getData() {
        if (!confirmed) return null;
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            int baskets = Integer.parseInt(basketField.getText().trim());
            
            double totalWeight = weight * baskets;
            double totalAmount = totalWeight * price;
            
            return new SalesRecord(
                dateField.getText().trim(),
                shipperField.getText().trim(),
                price,
                weight,
                baskets,
                totalWeight,
                totalAmount,
                handlerField.getText().trim()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "单价、重量和筐数必须输入数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
