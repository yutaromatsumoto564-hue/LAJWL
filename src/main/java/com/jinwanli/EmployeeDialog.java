package com.jinwanli;

import com.jinwanli.model.Employee;
import javax.swing.*;
import java.awt.*;

public class EmployeeDialog extends JDialog {
    private JTextField nameField, phoneField, idCardField, totalSalaryField;
    private JComboBox<String> posBox;
    private boolean confirmed = false;

    public EmployeeDialog(JFrame parent) {
        super(parent, "员工信息", true);
        setLayout(new BorderLayout());
        setSize(450, 600);
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
        gbc.insets = new Insets(12, 5, 12, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        nameField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "姓名:", nameField);
        
        posBox = UIUtils.createComboBox(new String[]{"员工", "经理", "主管", "临时工"});
        addFormRow(formPanel, gbc, row++, "职位:", posBox);
        
        phoneField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "联系电话:", phoneField);
        
        idCardField = UIUtils.createTextField();
        addFormRow(formPanel, gbc, row++, "身份证号:", idCardField);
        
        totalSalaryField = UIUtils.createTextField();
        totalSalaryField.setText("0");
        addFormRow(formPanel, gbc, row++, "总工资(元):", totalSalaryField);
        
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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }
    
    public Employee getData() {
        if (!confirmed) return null;
        try {
            String name = nameField.getText().trim();
            String id = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String pos = (String) posBox.getSelectedItem();
            String phone = phoneField.getText().trim();
            String idCard = idCardField.getText().trim();
            double totalSalary = parseDouble(totalSalaryField.getText());
            
            return new Employee(id, name, pos, phone, idCard, totalSalary, 0, 0);
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
