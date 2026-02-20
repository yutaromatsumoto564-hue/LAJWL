package com.jinwanli;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel navPanel;
    private String currentPage = "SUMMARY";
    
    // 导航按钮
    private JButton summaryBtn, attendanceBtn, salesBtn, expenseBtn, backupBtn, exitBtn;

    public MainFrame() {
        setTitle(ConfigManager.getProperty("app.name"));
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        // 1. 左侧导航栏（深色）
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.WEST);
        
        // 2. 顶部标题栏
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // 3. 主内容区域
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        contentPanel.add(new SummaryPanel(), "SUMMARY");
        contentPanel.add(new AttendancePanel(), "ATTENDANCE");
        contentPanel.add(new SalesPanel(), "SALES");
        contentPanel.add(new ExpensePanel(), "EXPENSE");
        
        add(contentPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.COLOR_BG_HEADER);
        topPanel.setPreferredSize(new Dimension(0, 60));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        // 使用文本图标
        JLabel logoLabel = new JLabel("[JWL]");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(UIUtils.COLOR_PRIMARY);
        
        JLabel titleLabel = new JLabel("金万里企业管理系统");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel userLabel = new JLabel("[Admin] 管理员");
        userLabel.setFont(UIUtils.FONT_BODY);
        userLabel.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        
        rightPanel.add(userLabel);
        
        topPanel.add(leftPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        return topPanel;
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setBackground(UIUtils.COLOR_BG_SIDEBAR);
        navPanel.setPreferredSize(new Dimension(240, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        
        // Logo 区域
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(240, 80));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel logoIcon = new JLabel("JWL");
        logoIcon.setFont(new Font("Arial", Font.BOLD, 28));
        logoIcon.setForeground(UIUtils.COLOR_PRIMARY);
        
        logoPanel.add(Box.createHorizontalGlue());
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createHorizontalGlue());
        
        navPanel.add(logoPanel);
        
        // 导航按钮区域
        navPanel.add(createNavSection("导航菜单", new JComponent[] {
            summaryBtn = createNavButton("S", "经营总览", "SUMMARY"),
            attendanceBtn = createNavButton("A", "员工考勤", "ATTENDANCE"),
            salesBtn = createNavButton("X", "销量统计", "SALES"),
            expenseBtn = createNavButton("Z", "开支管理", "EXPENSE")
        }));
        
        // 操作区域
        navPanel.add(createNavSection("操作", new JComponent[] {
            backupBtn = createNavButton("B", "本地备份", "BACKUP"),
            exitBtn = createNavButton("Q", "退出系统", "EXIT")
        }));
        
        navPanel.add(Box.createVerticalGlue());
        
        JLabel versionLabel = new JLabel("v1.0.1");
        versionLabel.setFont(UIUtils.FONT_SMALL);
        versionLabel.setForeground(new Color(100, 116, 139));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        navPanel.add(versionLabel);
        
        return navPanel;
    }
    
    private JPanel createNavSection(String title, JComponent[] buttons) {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setMaximumSize(new Dimension(240, 400));
        section.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(UIUtils.FONT_SMALL);
            titleLabel.setForeground(new Color(100, 116, 139));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(titleLabel);
        }
        
        for (JComponent btn : buttons) {
            section.add(btn);
            section.add(Box.createVerticalStrut(5));
        }
        
        return section;
    }
    
    private JButton createNavButton(String icon, String text, String action) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2.setColor(UIUtils.COLOR_BG_SIDEBAR_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIUtils.RADIUS_SMALL, UIUtils.RADIUS_SMALL);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setLayout(new BorderLayout(10, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        // 左侧图标 - 用圆形背景
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.COLOR_PRIMARY);
                g2.fillOval(0, 0, 24, 24);
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(24, 24));
        iconBox.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 12));
        iconLabel.setForeground(Color.WHITE);
        iconBox.add(iconLabel);
        
        // 文字 - 白色
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(UIUtils.FONT_BODY);
        textLabel.setForeground(Color.WHITE);  // 白色文字
        
        btn.add(iconBox, BorderLayout.WEST);
        btn.add(textLabel, BorderLayout.CENTER);
        
        btn.addActionListener(e -> handleNavClick(action));
        
        return btn;
    }
    
    private void handleNavClick(String action) {
        switch (action) {
            case "BACKUP":
                BackupManager.performBackup();
                JOptionPane.showMessageDialog(this, "备份完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "EXIT":
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "确定要退出系统吗？", "确认退出", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
            default:
                cardLayout.show(contentPanel, action);
                currentPage = action;
                break;
        }
    }

}

