package com.jinwanli;

import com.jinwanli.model.AttendanceRecord;
import com.jinwanli.model.Employee;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AttendanceDialog extends JDialog {
    private JComboBox<String> employeeBox;
    private List<Employee> employeeList;
    private JComboBox<String> statusBox;
    private JTextField dateField;
    private JTextField overtimeField;
    private boolean confirmed = false;

    public AttendanceDialog(JFrame parent) {
        super(parent, "录入考勤", true);
        setLayout(new BorderLayout());
        setSize(380, 340);
        setLocationRelativeTo(parent);
        setResizable(false);

        // 标题
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("录入考勤记录");
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
        
        // 员工选择
        employeeList = DataManager.getInstance().getEmployees();
        String[] empNames = new String[employeeList.size()];
        for (int i = 0; i < employeeList.size(); i++) {
            empNames[i] = employeeList.get(i).getName() + " (" + employeeList.get(i).getId() + ")";
        }
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("员工:"), gbc);
        employeeBox = UIUtils.createComboBox(empNames);
        gbc.gridx = 1;
        formPanel.add(employeeBox, gbc);
        
        // 日期
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("日期(YYYY-MM-DD):"), gbc);
        dateField = UIUtils.createTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        // 状态
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("状态:"), gbc);
        statusBox = UIUtils.createComboBox(new String[]{"正常", "迟到", "早退", "缺勤"});
        gbc.gridx = 1;
        formPanel.add(statusBox, gbc);
        
        // 加班
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("加班(小时):"), gbc);
        overtimeField = UIUtils.createTextField();
        overtimeField.setText("0");
        gbc.gridx = 1;
        formPanel.add(overtimeField, gbc);
        
        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if(employeeBox.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "请选择员工！", "提示", JOptionPane.WARNING_MESSAGE);
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

    public AttendanceRecord getData() {
        if (!confirmed) return null;
        try {
            int selectedIndex = employeeBox.getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= employeeList.size()) return null;
            
            Employee emp = employeeList.get(selectedIndex);
            
            double overtime = Double.parseDouble(overtimeField.getText().trim());
            return new AttendanceRecord(emp.getId(), emp.getName(), dateField.getText().trim(), 
                (String)statusBox.getSelectedItem(), overtime);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "输入格式错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
