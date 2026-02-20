package com.jinwanli;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("金万里企业管理系统 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG_MAIN);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.COLOR_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel titleLabel = new JLabel("系统登录");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        
        gbc.gridy = row++; 
        gbc.gridx = 0; gbc.weightx = 0.0;
        JLabel userLabel = new JLabel("账  号:");
        userLabel.setFont(UIUtils.FONT_BODY);
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        usernameField = UIUtils.createTextField();
        formPanel.add(usernameField, gbc);
        
        gbc.gridy = row++; 
        gbc.gridx = 0; gbc.weightx = 0.0;
        JLabel pwdLabel = new JLabel("密  码:");
        pwdLabel.setFont(UIUtils.FONT_BODY);
        formPanel.add(pwdLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setFont(UIUtils.FONT_BODY);
        passwordField.setPreferredSize(new Dimension(200, 36));
        formPanel.add(passwordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton loginBtn = UIUtils.createButton("登  录");
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.addActionListener(e -> {
            String uname = usernameField.getText();
            String pwd = new String(passwordField.getPassword());
            
            if ("admin".equals(uname) && "123456".equals(pwd)) {
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "账号或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(loginBtn);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}
