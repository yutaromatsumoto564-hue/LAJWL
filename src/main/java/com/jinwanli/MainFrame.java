package com.jinwanli;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel navPanel;
    private String currentPage = "HOME";
    
    // 导航按钮
    private JButton homeBtn, summaryBtn, attendanceBtn, salesBtn, expenseBtn, backupBtn, exitBtn;

    public MainFrame() {
        setTitle(ConfigManager.getProperty("app.name"));
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 使用 BorderLayout
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
        
        // 添加各个功能面板
        contentPanel.add(createHomePanel(), "HOME");
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
        
        // 左侧 Logo + 标题
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        JLabel logoLabel = new JLabel("🏢");
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel("金万里企业管理系统");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        // 右侧：当前时间 + 用户
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel userLabel = new JLabel("👤 管理员");
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
        
        JLabel logoIcon = new JLabel("💼");
        logoIcon.setFont(new Font("Arial", Font.PLAIN, 32));
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoText = new JLabel("JWL");
        logoText.setFont(new Font("Arial", Font.BOLD, 24));
        logoText.setForeground(UIUtils.COLOR_TEXT_LIGHT);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(Box.createHorizontalGlue());
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createHorizontalStrut(10));
        logoPanel.add(logoText);
        logoPanel.add(Box.createHorizontalGlue());
        
        navPanel.add(logoPanel);
        
        // 导航按钮区域
        navPanel.add(createNavSection("导航菜单", new JComponent[] {
            homeBtn = createNavButton("🏠", "经营概览", "HOME"),
            summaryBtn = createNavButton("📊", "经营总览", "SUMMARY"),
            attendanceBtn = createNavButton("📅", "员工考勤", "ATTENDANCE"),
            salesBtn = createNavButton("📈", "销量统计", "SALES"),
            expenseBtn = createNavButton("💰", "开支管理", "EXPENSE")
        }));
        
        // 操作区域
        navPanel.add(createNavSection("操作", new JComponent[] {
            backupBtn = createNavButton("💾", "本地备份", "BACKUP"),
            exitBtn = createNavButton("🚪", "退出系统", "EXIT")
        }));
        
        // 添加弹性空间
        navPanel.add(Box.createVerticalGlue());
        
        // 底部版本信息
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(UIUtils.FONT_SMALL);
        versionLabel.setForeground(new Color(100, 116, 139));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        navPanel.add(versionLabel);
        
        // 默认选中首页
        updateNavButtonState(homeBtn);
        
        return navPanel;
    }
    
    private JPanel createNavSection(String title, JComponent[] buttons) {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setMaximumSize(new Dimension(240, 400));
        section.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 标题
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(UIUtils.FONT_SMALL);
            titleLabel.setForeground(new Color(100, 116, 139));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(titleLabel);
        }
        
        // 按钮
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
        btn.setFont(UIUtils.FONT_BODY);
        
        // 图标
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        // 文字
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(UIUtils.FONT_BODY);
        
        btn.add(iconLabel, BorderLayout.WEST);
        btn.add(textLabel, BorderLayout.CENTER);
        
        // 事件处理
        btn.addActionListener(e -> handleNavClick(action));
        
        return btn;
    }
    
    private void handleNavClick(String action) {
        switch (action) {
            case "BACKUP":
                BackupManager.performBackup();
                JOptionPane.showMessageDialog(this, "✅ 备份完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
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
                updateNavButtons();
                break;
        }
    }
    
    private void updateNavButtons() {
        updateNavButtonState(homeBtn);
        updateNavButtonState(summaryBtn);
        updateNavButtonState(attendanceBtn);
        updateNavButtonState(salesBtn);
        updateNavButtonState(expenseBtn);
    }
    
    private void updateNavButtonState(JButton btn) {
        // 简化版：更新按钮文字颜色
        // 实际可以根据 currentPage 判断是否选中
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 渐变背景
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(59, 130, 246), 0, getHeight(), new Color(147, 197, 253));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // 添加一些装饰圆形
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(50, 50, 200, 200);
                g2.fillOval(getWidth() - 300, getHeight() - 250, 300, 300);
                g2.fillOval(getWidth() - 150, 100, 100, 100);
            }
        };
        panel.setLayout(new GridBagLayout());
        
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        card.setPreferredSize(new Dimension(500, 300));
        
        // 标题
        JLabel title = new JLabel("欢迎使用");
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 36));
        title.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("金万里企业管理系统");
        subtitle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 24));
        subtitle.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel desc = new JLabel("专业的企业管理解决方案");
        desc.setFont(UIUtils.FONT_BODY);
        desc.setForeground(new Color(148, 163, 184));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        // 快速操作按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton startBtn = UIUtils.createButton("开始使用 →");
        startBtn.addActionListener(e -> handleNavClick("SUMMARY"));
        
        btnPanel.add(startBtn);
        
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(desc);
        card.add(btnPanel);
        
        panel.add(card);
        
        return panel;
    }
}
