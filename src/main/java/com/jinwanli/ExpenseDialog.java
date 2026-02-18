package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseDialog extends JDialog {
    private JComboBox<String> categoryBox;
    private JTextField amountField;
    private JTextField usageField;
    private JTextField handlerField;
    private JTextField dateField;
    private boolean confirmed = false;

    public ExpenseDialog(JFrame parent) {
        super(parent, "添加支出记录", true);
        setLayout(new BorderLayout());
        setSize(380, 360);
        setLocationRelativeTo(parent);
        setResizable(false);

        // 标题
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("添加支出记录");
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
        
        // 分类
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("分类:"), gbc);
        categoryBox = UIUtils.createComboBox(new String[]{"原材料采购", "车旅费", "伙食费", "电费", "房租", "设备维护", "其他支出"});
        gbc.gridx = 1;
        formPanel.add(categoryBox, gbc);
        
        // 金额
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("金额(元):"), gbc);
        amountField = UIUtils.createTextField();
        amountField.setText("0");
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        // 用途
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("用途:"), gbc);
        usageField = UIUtils.createTextField();
        gbc.gridx = 1;
        formPanel.add(usageField, gbc);
        
        // 经手人
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("经手人:"), gbc);
        handlerField = UIUtils.createTextField();
        gbc.gridx = 1;
        formPanel.add(handlerField, gbc);
        
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

    public ExpenseRecord getData() {
        if (!confirmed) return null;
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            String date = dateField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            String usage = usageField.getText().trim();
            String handler = handlerField.getText().trim();
            
            return new ExpenseRecord(date, category, amount, usage, handler, "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "金额输入错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
