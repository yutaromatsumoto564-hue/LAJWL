package com.jinwanli;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle(ConfigManager.getProperty("app.name")); // 从配置读取标题
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 顶部标题栏
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(255, 215, 0));
        topPanel.setPreferredSize(new Dimension(1024, 80));
        JLabel titleLabel = new JLabel("金万里企业管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        // 2. 主内容区域 (使用 CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 添加各个功能面板
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(new AttendancePanel(), "ATTENDANCE");
        contentPanel.add(new SalesPanel(), "SALES");
        contentPanel.add(new ExpensePanel(), "EXPENSE");

        add(contentPanel, BorderLayout.CENTER);

        // 3. 左侧导航栏
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);

        setVisible(true);
    }

    private JPanel createHomePanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 248, 220));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(255, 165, 0));
                g.setFont(new Font("微软雅黑", Font.BOLD, 48));
                g.drawString("金万里企业管理", 300, 200);
                g.setFont(new Font("微软雅黑", Font.PLAIN, 24));
                g.drawString("专业的企业管理解决方案", 320, 250);
            }
        };
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(240, 240, 240));
        navPanel.setPreferredSize(new Dimension(200, 688));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(createNavButton("员工考勤", "ATTENDANCE"));
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(createNavButton("销量统计", "SALES"));
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(createNavButton("开支管理", "EXPENSE"));
        navPanel.add(Box.createVerticalStrut(10));

        JButton backupBtn = createBaseNavButton("本地备份");
        backupBtn.addActionListener(e -> {
            BackupManager.performBackup();
            JOptionPane.showMessageDialog(this, "备份完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        navPanel.add(backupBtn);

        navPanel.add(Box.createVerticalStrut(10));
        JButton exitBtn = createBaseNavButton("退出系统");
        exitBtn.addActionListener(e -> System.exit(0));
        navPanel.add(exitBtn);

        navPanel.add(Box.createVerticalGlue());
        return navPanel;
    }

    // 导航切换按钮
    private JButton createNavButton(String text, String cardName) {
        JButton btn = createBaseNavButton(text);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    // 基础按钮样式
    private JButton createBaseNavButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40)); // BoxLayout下使用MaximumSize
        button.setPreferredSize(new Dimension(180, 40));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        button.setBackground(new Color(255, 215, 0));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
}