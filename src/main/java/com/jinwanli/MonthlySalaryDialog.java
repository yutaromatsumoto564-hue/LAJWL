package com.jinwanli;

import com.jinwanli.model.Employee;
import com.jinwanli.model.MonthlySalaryRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MonthlySalaryDialog extends JDialog {
    private JComboBox<String> employeeBox;
    private JTextField totalSalaryField, monthField;
    private boolean confirmed = false;
    private List<Employee> employees;

    public MonthlySalaryDialog(JFrame parent) {
        this(parent, new SimpleDateFormat("yyyy-MM").format(new Date()));
    }

    public MonthlySalaryDialog(JFrame parent, String selectedMonth) {
        super(parent, "录入月度工资", true);
        setLayout(new BorderLayout());
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        employees = DataManager.getInstance().getEmployees();

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("录入月度工资 - " + selectedMonth);
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
        
        // 月份字段 - 只读，显示从外部传递的月份
        monthField = UIUtils.createTextField();
        monthField.setText(selectedMonth);
        monthField.setEditable(false); // 设置为只读
        monthField.setBackground(UIUtils.COLOR_BG_MAIN); // 设置背景色为灰色
        addFormRow(formPanel, gbc, row++, "月份:", monthField);
        
        String[] employeeNames = new String[employees.size()];
        for (int i = 0; i < employees.size(); i++) {
            employeeNames[i] = employees.get(i).getName();
        }
        employeeBox = UIUtils.createComboBox(employeeNames);
        addFormRow(formPanel, gbc, row++, "员工:", employeeBox);
        
        totalSalaryField = UIUtils.createTextField();
        totalSalaryField.setText("0");
        addFormRow(formPanel, gbc, row++, "总工资(元):", totalSalaryField);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if(employeeBox.getSelectedItem() == null) {
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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }
    
    public MonthlySalaryRecord getData() {
        if (!confirmed) return null;
        try {
            String month = monthField.getText().trim();
            String employeeName = (String) employeeBox.getSelectedItem();
            Employee selectedEmployee = null;
            for (Employee emp : employees) {
                if (emp.getName().equals(employeeName)) {
                    selectedEmployee = emp;
                    break;
                }
            }
            
            if (selectedEmployee == null) return null;
            
            String id = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            double totalSalary = parseDouble(totalSalaryField.getText());
            
            return new MonthlySalaryRecord(id, selectedEmployee.getId(), selectedEmployee.getName(), 
                                          selectedEmployee.getPosition(), month, totalSalary, 0, 0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
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
