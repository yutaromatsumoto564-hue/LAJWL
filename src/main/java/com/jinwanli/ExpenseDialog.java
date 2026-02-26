package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseDialog extends JDialog {
    private JComboBox<String> categoryBox;
    private JTextField amountField, usageField, handlerField, dateField;
    private boolean confirmed = false;

    public ExpenseDialog(JFrame parent, ExpenseRecord existingRecord) {
        super(parent, existingRecord == null ? "添加财务记录" : "编辑财务记录", true);
        setLayout(new BorderLayout());
        setSize(450, 480);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel(existingRecord == null ? "录入财务收支" : "编辑财务收支");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        categoryBox = UIUtils.createComboBox(new String[]{
            "材料采购", "车旅费", "伙食费", "电费", "项目投资", "其他支出", 
            "股东注资(收入)", "政府补贴(收入)", "其他(收入)"
        });
        addFormRow(formPanel, gbc, row++, "收支分类:", categoryBox);
        

        
        amountField = UIUtils.createTextField();
        amountField.setText("0");
        addFormRow(formPanel, gbc, row++, "金额(元):", amountField);
        
        usageField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "用途/备注:", usageField);
        
        handlerField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "经手人:", handlerField);
        
        dateField = UIUtils.createTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        addFormRow(formPanel, gbc, row++, "日期:", dateField);
        
        if (existingRecord != null) {
            categoryBox.setSelectedItem(existingRecord.getCategory());
            amountField.setText(String.valueOf(existingRecord.getAmount()));
            usageField.setText(existingRecord.getUsage());
            handlerField.setText(existingRecord.getHandler());
            dateField.setText(existingRecord.getDate());
        }
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if(amountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入金额！", "提示", JOptionPane.WARNING_MESSAGE);
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

    public ExpenseRecord getData() {
        if (!confirmed) return null;
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            return new ExpenseRecord(
                dateField.getText().trim(),
                (String) categoryBox.getSelectedItem(),
                amount,
                usageField.getText().trim(),
                handlerField.getText().trim(),
                ""
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "金额输入错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // 设置默认分类
    public void setDefaultCategory(String category) {
        categoryBox.setSelectedItem(category);
    }
}
