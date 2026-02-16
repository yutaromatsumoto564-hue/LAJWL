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
    
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public LoginFrame() {
        setTitle("金万里企业管理 - 登录");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            URL imgUrl = getClass().getResource("/images/login_bg.jpg");
            if (imgUrl != null) {
                splashImage = ImageIO.read(imgUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (splashImage != null) {
                    g.drawImage(splashImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(new GradientPaint(0, 0, new Color(255, 215, 0), 0, getHeight(), Color.WHITE));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("微软雅黑", Font.BOLD, 24));
                    g.drawString("金万里企业管理系统", 140, 180);
                }
            }
        };
        
        JPanel loginPanel = createLoginPanel();
        
        mainContainer.add(splashPanel, "SPLASH");
        mainContainer.add(loginPanel, "LOGIN");
        
        add(mainContainer);
        
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContainer, "LOGIN");
                ((Timer)e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
        
        setVisible(true);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 215, 0));
        titlePanel.setPreferredSize(new Dimension(500, 80));
        titlePanel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("金万里企业管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        
        JPanel formBox = new JPanel();
        formBox.setLayout(null);
        formBox.setPreferredSize(new Dimension(320, 200));
        formBox.setBackground(Color.WHITE);
        formBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel uLabel = new JLabel("用户名:");
        uLabel.setBounds(40, 40, 60, 25);
        uLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formBox.add(uLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(100, 40, 180, 25);
        formBox.add(usernameField);
        
        JLabel pLabel = new JLabel("密  码:");
        pLabel.setBounds(40, 90, 60, 25);
        pLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formBox.add(pLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 90, 180, 25);
        formBox.add(passwordField);
        
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