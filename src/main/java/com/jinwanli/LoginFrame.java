package com.jinwanli;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private Image backgroundImage;

    public LoginFrame() {
        setTitle(ConfigManager.getProperty("app.name") + " - 登录");
        setSize(500, 400); // 稍微调大一点以适应背景
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- 1. 加载背景图片 ---
        try {
            // 尝试从资源路径加载图片 (对应 resources/images/login_bg.jpg)
            URL imageUrl = getClass().getResource("/images/login_bg.jpg");
            if (imageUrl != null) {
                backgroundImage = ImageIO.read(imageUrl);
            } else {
                System.err.println("警告: 未找到背景图片 /images/login_bg.jpg，将使用默认背景色。");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("警告: 读取背景图片失败。");
        }

        // --- 2. 创建带有背景图的自定义面板作为主容器 ---
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 如果图片加载成功，则绘制图片
                if (backgroundImage != null) {
                    // 将图片缩放以填充整个面板
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        // 如果没有图片，设置一个默认背景色
        if (backgroundImage == null) {
            mainPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        }
        setContentPane(mainPanel); // 将此面板设置为主内容窗格

        // --- 3. 构建登录表单 ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel titleLabel = new JLabel(ConfigManager.getProperty("app.name"));
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // 关键：设置透明，让背景透出来
        titleLabel.setOpaque(false);
        // 为了让标题在深色背景上更清晰，可以设置前景色为白色（可选）
        // titleLabel.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // 表单面板区域
        JPanel formPanel = new JPanel(new GridBagLayout());
        // 关键：设置表单面板透明
        formPanel.setOpaque(false);
        // 可选：加一个半透明的白色背景让文字更清晰
        // formPanel.setBackground(new Color(255, 255, 255, 200));
        // formPanel.setOpaque(true);
        // formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 5, 10, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(UIUtils.FONT_NORMAL);
        // userLabel.setForeground(Color.WHITE); // 可选：根据背景调整文字颜色
        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(userLabel, formGbc);

        userField = new JTextField(15);
        userField.setFont(UIUtils.FONT_NORMAL);
        formGbc.gridx = 1; formGbc.gridy = 0;
        formPanel.add(userField, formGbc);

        // 密码
        JLabel passLabel = new JLabel("密  码:");
        passLabel.setFont(UIUtils.FONT_NORMAL);
        // passLabel.setForeground(Color.WHITE); // 可选：根据背景调整文字颜色
        formGbc.gridx = 0; formGbc.gridy = 1;
        formPanel.add(passLabel, formGbc);

        passField = new JPasswordField(15);
        passField.setFont(UIUtils.FONT_NORMAL);
        formGbc.gridx = 1; formGbc.gridy = 1;
        formPanel.add(passField, formGbc);

        // 将表单面板添加到主面板
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(formPanel, gbc);

        // 登录按钮
        loginButton = UIUtils.createButton("登 录");
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.addActionListener(this);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(30, 10, 10, 10);
        mainPanel.add(loginButton, gbc);

        // 支持回车登录
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (ConfigManager.validateLogin(username, password)) {
            new MainFrame();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
        }
    }
}