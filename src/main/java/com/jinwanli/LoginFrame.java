package com.jinwanli;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("金万里企业管理 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("金万里企业管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel userLabel = new JLabel("账号:");
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));

        JLabel pwdLabel = new JLabel("密码:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));

        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(pwdLabel);
        formPanel.add(passwordField);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("登录");
        loginButton.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        btnPanel.add(loginButton);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (ConfigManager.validateLogin(username, password)) {
            dispose();
            new MainFrame();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
