package com.jinwanli;

import com.jinwanli.model.Employee;
import javax.swing.*;
import java.awt.*;

public class EmployeeDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<String> posBox;
    private JTextField phoneField;
    private JTextField idCardField;
    private JTextField baseSalaryField;
    private JTextField perfSalaryField;
    private JTextField overtimeField;
    
    private boolean confirmed = false;

    public EmployeeDialog(JFrame parent) {
        super(parent, "员工信息", true);
        setLayout(new BorderLayout());
        setSize(400, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("录入员工信息");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("姓名:"), gbc);
        nameField = UIUtils.createTextField();
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("职位:"), gbc);
        posBox = UIUtils.createComboBox(new String[]{"员工", "经理", "主管", "临时工"});
        gbc.gridx = 1;
        formPanel.add(posBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("联系电话:"), gbc);
        phoneField = UIUtils.createTextField();
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("身份证号:"), gbc);
        idCardField = UIUtils.createTextField();
        gbc.gridx = 1;
        formPanel.add(idCardField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("基本工资(元):"), gbc);
        baseSalaryField = UIUtils.createTextField();
        baseSalaryField.setText("0");
        gbc.gridx = 1;
        formPanel.add(baseSalaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("绩效工资(元):"), gbc);
        perfSalaryField = UIUtils.createTextField();
        perfSalaryField.setText("0");
        gbc.gridx = 1;
        formPanel.add(perfSalaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("加班补贴(元):"), gbc);
        overtimeField = UIUtils.createTextField();
        overtimeField.setText("0");
        gbc.gridx = 1;
        formPanel.add(overtimeField, gbc);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if(nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
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
    
    public Employee getData() {
        if (!confirmed) return null;
        try {
            String name = nameField.getText().trim();
            String id = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String pos = (String) posBox.getSelectedItem();
            String phone = phoneField.getText().trim();
            String idCard = idCardField.getText().trim();
            double baseSalary = parseDouble(baseSalaryField.getText());
            double perfSalary = parseDouble(perfSalaryField.getText());
            double overtime = parseDouble(overtimeField.getText());
            
            return new Employee(id, name, pos, phone, idCard, baseSalary, perfSalary, overtime);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "工资请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}