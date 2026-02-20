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
    private JTextField dateField;
    private JTextField hoursField;
    private boolean confirmed = false;

    public AttendanceDialog(JFrame parent) {
        super(parent, "录入考勤", true);
        setLayout(new BorderLayout());
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("手动录入考勤");
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
        
        List<Employee> emps = DataManager.getInstance().getEmployees();
        String[] empNames = new String[emps.size()];
        for (int i = 0; i < emps.size(); i++) {
            empNames[i] = emps.get(i).getName() + " (" + emps.get(i).getId() + ")";
        }
        employeeBox = UIUtils.createComboBox(empNames);
        addFormRow(formPanel, gbc, row++, "选择员工:", employeeBox);

        dateField = UIUtils.createTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        addFormRow(formPanel, gbc, row++, "日期(YYYY-MM-DD):", dateField);

        hoursField = UIUtils.createTextField();
        hoursField.setText("8.0");
        addFormRow(formPanel, gbc, row++, "出勤时长(h):", hoursField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));

        JButton saveBtn = UIUtils.createButton("保存");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.addActionListener(e -> {
            if (hoursField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入时长！", "提示", JOptionPane.WARNING_MESSAGE);
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
        gbc.gridx = 0; 
        gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }

    public AttendanceRecord getData() {
        if (!confirmed) return null;
        try {
            String selectedEmp = (String) employeeBox.getSelectedItem();
            String empId = selectedEmp.substring(selectedEmp.lastIndexOf("(") + 1, selectedEmp.lastIndexOf(")"));
            String date = dateField.getText().trim();
            double hours = Double.parseDouble(hoursField.getText().trim());

            return new AttendanceRecord(empId, date, hours);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "时长必须是有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
