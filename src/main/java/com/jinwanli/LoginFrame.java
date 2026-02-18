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
    
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private Timer timer;

    public LoginFrame() {
        setTitle("金万里企业管理 - 登录");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        // 开场动画面板
        JPanel splashPanel = createSplashPanel();
        
        // 登录面板
        JPanel loginPanel = createLoginPanel();
        
        mainContainer.add(splashPanel, "SPLASH");
        mainContainer.add(loginPanel, "LOGIN");
        
        add(mainContainer);
        
        // 2秒后切换到登录
        timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContainer, "LOGIN");
                timer.stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
        
        setVisible(true);
    }
    
    private JPanel createSplashPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 渐变背景
                GradientPaint gradient = new GradientPaint(0, 0, new Color(37, 99, 235), 0, getHeight(), new Color(147, 197, 253));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // 装饰圆
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 150, getHeight() - 200, 250, 250);
                g2d.fillOval(getWidth() - 100, -30, 120, 120);
                
                // Logo
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 60));
                g2d.drawString("💼", getWidth()/2 - 40, getHeight()/2 - 40);
                
                g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 32));
                String title = "金万里";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, getHeight()/2 + 30);
                
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
                g2d.setColor(new Color(255, 255, 255, 200));
                String subtitle = "企业管理系统";
                fm = g2d.getFontMetrics();
                g2d.drawString(subtitle, (getWidth() - fm.stringWidth(subtitle)) / 2, getHeight()/2 + 60);
                
                // 加载提示
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
                g2d.setColor(new Color(255, 255, 255, 150));
                String loading = "正在加载...";
                fm = g2d.getFontMetrics();
                g2d.drawString(loading, (getWidth() - fm.stringWidth(loading)) / 2, getHeight() - 50);
            }
        };
        
        panel.setPreferredSize(new Dimension(450, 550));
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);

        // 顶部装饰
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, UIUtils.COLOR_PRIMARY, 0, 180, UIUtils.COLOR_PRIMARY_HOVER);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // 装饰
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(0, -50, 200, 200);
                g2d.fillOval(getWidth() - 150, 50, 180, 180);
                
                // 标题
                g2d.setColor(Color.WHITE);
                g2d.setFont(UIUtils.FONT_TITLE);
                String title = "欢迎登录";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 100);
                
                g2d.setFont(UIUtils.FONT_BODY);
                g2d.setColor(new Color(255, 255, 255, 200));
                String subtitle = "金万里企业管理系统";
                fm = g2d.getFontMetrics();
                g2d.drawString(subtitle, (getWidth() - fm.stringWidth(subtitle)) / 2, 130);
            }
        };
        topPanel.setPreferredSize(new Dimension(450, 180));
        
        panel.add(topPanel, BorderLayout.NORTH);

        // 登录表单
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        // 用户名
        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(350, 50));
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        userIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        usernameField = UIUtils.createTextField();
        usernameField.setPreferredSize(new Dimension(300, 44));
        usernameField.setFont(UIUtils.FONT_BODY);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.COLOR_BORDER),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        userPanel.add(userIcon, BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);
        
        // 密码
        JPanel pwdPanel = new JPanel(new BorderLayout(10, 0));
        pwdPanel.setOpaque(false);
        pwdPanel.setMaximumSize(new Dimension(350, 50));
        pwdPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel pwdIcon = new JLabel("🔒");
        pwdIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        pwdIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        passwordField = new JPasswordField();
        passwordField.setFont(UIUtils.FONT_BODY);
        passwordField.setPreferredSize(new Dimension(300, 44));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.COLOR_BORDER),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        pwdPanel.add(pwdIcon, BorderLayout.WEST);
        pwdPanel.add(passwordField, BorderLayout.CENTER);
        
        // 登录按钮
        loginButton = UIUtils.createButton("登 录");
        loginButton.setMaximumSize(new Dimension(350, 48));
        loginButton.setFont(UIUtils.FONT_BODY_BOLD);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        
        // 回车登录
        passwordField.addActionListener(e -> doLogin());
        
        // 版权信息
        JLabel footerLabel = new JLabel("© 2026 金万里企业管理");
        footerLabel.setFont(UIUtils.FONT_SMALL);
        footerLabel.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        formPanel.add(userPanel);
        formPanel.add(pwdPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginButton);
        formPanel.add(footerLabel);
        
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
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
            if (timer != null) timer.stop();
            new MainFrame();
        } else {
            JOptionPane.showMessageDialog(this, "❌ 用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
