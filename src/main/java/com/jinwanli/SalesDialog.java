package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesDialog extends JDialog {
    private JTextField customerField, productField, priceField, qtyField, handlerField, dateField;
    private boolean confirmed = false;

    public SalesDialog(JFrame parent) {
        super(parent, "新增销售记录", true);
        setLayout(new BorderLayout());
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("录入销售订单");
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
        customerField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "客户/收货方:", customerField);
        
        productField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "商品/型号:", productField);
        
        priceField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "单价(元):", priceField);
        
        qtyField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "数量:", qtyField);
        
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
            if(customerField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
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
            int qty = Integer.parseInt(qtyField.getText().trim());
            double total = price * qty;
            
            return new SalesRecord(
                dateField.getText().trim(),
                customerField.getText().trim(),
                productField.getText().trim(),
                price,
                qty,
                total,
                handlerField.getText().trim()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "单价和数量必须输入数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
