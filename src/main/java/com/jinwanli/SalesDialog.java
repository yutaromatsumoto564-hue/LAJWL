package com.jinwanli;

import com.jinwanli.model.SalesRecord;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesDialog extends JDialog {
    private JTextField shipperField = new JTextField(10);
    private JTextField basketField = new JTextField(10);
    private JTextField weightField = new JTextField(10);
    private JTextField priceField = new JTextField(10);
    private boolean confirmed = false;

    public SalesDialog(JFrame parent) {
        super(parent, "添加销售记录", true);
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(300, 250);
        setLocationRelativeTo(parent);

        add(new JLabel("货主名称:")); 
        add(shipperField);
        add(new JLabel("筐数:")); 
        add(basketField);
        add(new JLabel("每筐斤数:")); 
        add(weightField);
        add(new JLabel("单价(元/斤):")); 
        add(priceField);

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

    public SalesRecord getData() {
        if (!confirmed) return null;
        try {
            String shipper = shipperField.getText();
            int baskets = Integer.parseInt(basketField.getText());
            double weight = Double.parseDouble(weightField.getText());
            double price = Double.parseDouble(priceField.getText());
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            return new SalesRecord(shipper, baskets, weight, price, date);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "输入格式错误，请检查数字！");
            return null;
        }
    }
}