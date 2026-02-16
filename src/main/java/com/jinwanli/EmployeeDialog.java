package com.jinwanli;

import com.jinwanli.model.Employee;
import javax.swing.*;
import java.awt.*;

public class EmployeeDialog extends JDialog {
    private JTextField nameField = new JTextField(10);
    private JComboBox<String> posBox = new JComboBox<>(new String[]{"员工", "经理", "主管", "临时工"});
    private JTextField phoneField = new JTextField(10);
    private JTextField idCardField = new JTextField(10);
    private JTextField salaryField = new JTextField(10);
    
    private boolean confirmed = false;

    public EmployeeDialog(JFrame parent) {
        super(parent, "录入员工信息", true);
        setLayout(new GridLayout(6, 2, 10, 10));
        setSize(320, 300);
        setLocationRelativeTo(parent);

        add(new JLabel("姓名:")); add(nameField);
        add(new JLabel("职位:")); add(posBox);
        add(new JLabel("联系电话:")); add(phoneField);
        add(new JLabel("身份证号:")); add(idCardField);
        add(new JLabel("基本工资(元):")); add(salaryField);

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            if(nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名不能为空");
                return;
            }
            confirmed = true;
            setVisible(false);
        });
        add(saveBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> setVisible(false));
        add(cancelBtn);
    }

    public Employee getData() {
        if (!confirmed) return null;
        try {
            String name = nameField.getText();
            String pos = (String) posBox.getSelectedItem();
            String phone = phoneField.getText();
            String idCard = idCardField.getText();
            double salary = Double.parseDouble(salaryField.getText());
            
            String id = "E" + (System.currentTimeMillis() % 100000);
            
            return new Employee(id, name, pos, phone, idCard, salary, 0, 0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "工资请输入数字！");
            return null;
        }
    }
}