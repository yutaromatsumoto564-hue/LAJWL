package com.jinwanli;

import com.jinwanli.model.AttendanceRecord;
import com.jinwanli.model.Employee;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttendanceDialog extends JDialog {
    private JComboBox<String> employeeBox;
    private List<Employee> employeeList;
    private JComboBox<String> statusBox = new JComboBox<>(new String[]{"正常", "迟到", "早退", "缺勤"});
    private JTextField dateField = new JTextField(10);
    private JTextField overtimeField = new JTextField("0");
    private boolean confirmed = false;

    public AttendanceDialog(JFrame parent) {
        super(parent, "录入考勤", true);
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(300, 250);
        setLocationRelativeTo(parent);

        employeeList = DataManager.getInstance().getEmployees();
        
        String[] empNames = new String[employeeList.size()];
        for (int i = 0; i < employeeList.size(); i++) {
            empNames[i] = employeeList.get(i).getName();
        }
        employeeBox = new JComboBox<>(empNames);

        add(new JLabel("员工:")); 
        add(employeeBox);
        add(new JLabel("日期(YYYY-MM-DD):")); 
        dateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        add(dateField);
        add(new JLabel("状态:")); 
        add(statusBox);
        add(new JLabel("加班(小时):")); 
        add(overtimeField);

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        add(saveBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> setVisible(false));
        add(cancelBtn);
    }

    public AttendanceRecord getData() {
        if (!confirmed) return null;
        try {
            int selectedIndex = employeeBox.getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= employeeList.size()) return null;
            
            Employee emp = employeeList.get(selectedIndex);
            
            double overtime = Double.parseDouble(overtimeField.getText());
            return new AttendanceRecord(emp.getId(), emp.getName(), dateField.getText(), (String)statusBox.getSelectedItem(), overtime);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "输入格式错误！");
            return null;
        }
    }
}