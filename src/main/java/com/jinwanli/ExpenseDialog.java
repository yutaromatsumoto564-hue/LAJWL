package com.jinwanli;

import com.jinwanli.model.ExpenseRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseDialog extends JDialog {
    private JComboBox<String> categoryBox = new JComboBox<>(new String[]{"原材料采购", "车旅费", "伙食费", "电费", "其他支出"});
    private JTextField amountField = new JTextField(10);
    private JTextField usageField = new JTextField(10);
    private JTextField handlerField = new JTextField(10);
    private boolean confirmed = false;

    public ExpenseDialog(JFrame parent) {
        super(parent, "添加支出记录", true);
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(300, 250);
        setLocationRelativeTo(parent);

        add(new JLabel("分类:")); 
        add(categoryBox);
        add(new JLabel("金额(元):")); 
        add(amountField);
        add(new JLabel("用途:")); 
        add(usageField);
        add(new JLabel("经手人:")); 
        add(handlerField);

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

    public ExpenseRecord getData() {
        if (!confirmed) return null;
        try {
            double amount = Double.parseDouble(amountField.getText());
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            return new ExpenseRecord(date, (String)categoryBox.getSelectedItem(), amount, usageField.getText(), handlerField.getText(), "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "金额输入错误！");
            return null;
        }
    }
}