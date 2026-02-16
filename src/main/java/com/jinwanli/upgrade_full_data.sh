#!/bin/bash

echo "正在升级 LoginFrame.java 以实现 PS 风格启动页效果..."

cat > LoginFrame.java <<EOF
package com.jinwanli;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.IOException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Image splashImage;
    
    // 使用 CardLayout 实现界面的切换
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public LoginFrame() {
        setTitle("金万里企业管理 - 登录");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 1. 加载启动图 (原背景图)
        try {
            URL imgUrl = getClass().getResource("/images/login_bg.jpg");
            if (imgUrl != null) {
                splashImage = ImageIO.read(imgUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. 初始化主容器 (CardLayout)
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        // --- 界面 A: 启动画面 (Splash Screen) ---
        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (splashImage != null) {
                    // 图片铺满全屏
                    g.drawImage(splashImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // 没图就显示个渐变色
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(new GradientPaint(0, 0, new Color(255, 215, 0), 0, getHeight(), Color.WHITE));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("微软雅黑", Font.BOLD, 24));
                    g.drawString("金万里企业管理系统", 140, 180);
                }
            }
        };
        
        // --- 界面 B: 登录表单 (干净背景) ---
        JPanel loginPanel = createLoginPanel();
        
        // 将两个界面加入容器
        mainContainer.add(splashPanel, "SPLASH");
        mainContainer.add(loginPanel, "LOGIN");
        
        add(mainContainer);
        
        // 3. 设置定时器: 2秒后自动切换
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到登录界面
                cardLayout.show(mainContainer, "LOGIN");
                // 停止定时器，防止重复触发
                ((Timer)e.getSource()).stop();
            }
        });
        timer.setRepeats(false); // 只执行一次
        timer.start();
        
        setVisible(true);
    }
    
    private JPanel createLoginPanel() {
        // 创建一个干净的登录界面，使用单一背景色，确保文字清晰
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245)); // 浅灰色背景，护眼且清晰

        // 顶部标题
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 215, 0)); // 品牌金色
        titlePanel.setPreferredSize(new Dimension(500, 80));
        titlePanel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("金万里企业管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK); // 黑色文字
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);

        // 表单区域
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false); // 透明，显示底部的浅灰背景
        
        JPanel formBox = new JPanel();
        formBox.setLayout(null);
        formBox.setPreferredSize(new Dimension(320, 200));
        formBox.setBackground(Color.WHITE); // 纯白色的表单区域
        formBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // 细边框
        
        // 用户名
        JLabel uLabel = new JLabel("用户名:");
        uLabel.setBounds(40, 40, 60, 25);
        uLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formBox.add(uLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(100, 40, 180, 25);
        formBox.add(usernameField);
        
        // 密码
        JLabel pLabel = new JLabel("密  码:");
        pLabel.setBounds(40, 90, 60, 25);
        pLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formBox.add(pLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 90, 180, 25);
        formBox.add(passwordField);
        
        // 登录按钮
        loginButton = new JButton("立即登录");
        loginButton.setBounds(40, 145, 240, 35);
        loginButton.setBackground(new Color(255, 165, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
        loginButton.setFocusPainted(false);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        
        formBox.add(loginButton);
        formContainer.add(formBox);
        
        panel.add(formContainer, BorderLayout.CENTER);
        
        // 底部版权 (可选)
        JLabel footerLabel = new JLabel("© 2026 Jinwanli Enterprise Management");
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(footerLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void doLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (ConfigManager.validateLogin(username, password)) {
            dispose();
            new MainFrame();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
        }
    }
}
EOF

echo "LoginFrame.java 升级完成！启动时将展示2秒背景图。"