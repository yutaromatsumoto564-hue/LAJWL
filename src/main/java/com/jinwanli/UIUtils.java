package com.jinwanli;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Calendar;

public class UIUtils {
    // ========== 现代配色方案 ==========
    // 主色调 - 专业蓝
    public static final Color COLOR_PRIMARY = new Color(59, 130, 246);
    public static final Color COLOR_PRIMARY_HOVER = new Color(37, 99, 235);
    public static final Color COLOR_PRIMARY_LIGHT = new Color(239, 246, 255);
    
    // 功能色
    public static final Color COLOR_SUCCESS = new Color(34, 197, 94);
    public static final Color COLOR_SUCCESS_LIGHT = new Color(240, 253, 244);
    public static final Color COLOR_WARNING = new Color(251, 191, 36);
    public static final Color COLOR_WARNING_LIGHT = new Color(254, 249, 232);
    public static final Color COLOR_DANGER = new Color(239, 68, 68);
    public static final Color COLOR_DANGER_LIGHT = new Color(254, 242, 242);
    
    // 背景色
    public static final Color COLOR_BG_MAIN = new Color(248, 250, 252);
    public static final Color COLOR_BG_CARD = new Color(255, 255, 255);
    public static final Color COLOR_BG_SIDEBAR = new Color(15, 23, 42);
    public static final Color COLOR_BG_SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color COLOR_BG_HEADER = new Color(255, 255, 255);
    public static final Color COLOR_BG_CONTROL = new Color(240, 240, 240); // 兼容旧版
    
    // 文字色
    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color COLOR_TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color COLOR_TEXT_LIGHT = new Color(255, 255, 255);
    
    // 边框色
    public static final Color COLOR_BORDER = new Color(226, 232, 240);
    public static final Color COLOR_BORDER_LIGHT = new Color(241, 245, 249);
    
    // ========== 字体 ==========
    public static final Font FONT_TITLE = new Font("Microsoft YaHei", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Microsoft YaHei", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Microsoft YaHei", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Microsoft YaHei", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Microsoft YaHei", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Microsoft YaHei", Font.PLAIN, 14); // 兼容旧版
    public static final Font FONT_TAB = new Font("Microsoft YaHei", Font.PLAIN, 16); // 兼容旧版
    public static final Font FONT_SMALL = new Font("Microsoft YaHei", Font.PLAIN, 12);
    public static final Font FONT_NUMBER = new Font("Arial", Font.BOLD, 28);
    
    // ========== 间距常量 ==========
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    public static final int PADDING_XLARGE = 32;
    
    public static final int RADIUS_SMALL = 6;
    public static final int RADIUS_MEDIUM = 10;
    public static final int RADIUS_LARGE = 16;
    
    // ========== 兼容旧版常量 ==========
    public static final Color COLOR_BG_TITLE = new Color(255, 248, 220);
    public static final Font FONT_BOLD = new Font("Microsoft YaHei", Font.BOLD, 16);
    
    // ========== 通用方法 ==========
    
    public static String[] getRecentYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        return years;
    }
    
    /**
     * 创建带圆角的按钮
     */
    public static JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(COLOR_PRIMARY_HOVER);
                } else if (getModel().isRollover()) {
                    g2.setColor(COLOR_PRIMARY_HOVER);
                } else {
                    g2.setColor(COLOR_PRIMARY);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_SMALL * 2, RADIUS_SMALL * 2);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(FONT_BODY);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    /**
     * 创建次要按钮（边框样式）
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = COLOR_BG_CARD;
                if (getModel().isPressed() || getModel().isRollover()) {
                    bg = new Color(248, 250, 252);
                }
                
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_SMALL * 2, RADIUS_SMALL * 2);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS_SMALL * 2, RADIUS_SMALL * 2);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(FONT_BODY);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    /**
     * 创建危险按钮（红色）
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = COLOR_DANGER;
                if (getModel().isPressed()) {
                    bg = new Color(220, 38, 38);
                } else if (getModel().isRollover()) {
                    bg = new Color(185, 28, 28);
                }
                
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_SMALL * 2, RADIUS_SMALL * 2);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(FONT_BODY);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    /**
     * 创建带阴影的卡片面板
     */
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 背景
                g2.setColor(COLOR_BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS_MEDIUM, RADIUS_MEDIUM);
                
                // 阴影
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 4, getWidth(), getHeight(), RADIUS_MEDIUM, RADIUS_MEDIUM);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM));
        return card;
    }
    
    /**
     * 创建带标题的面板
     */
    public static JPanel createTitlePanel(String titleText) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_BG_HEADER);
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        titlePanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, PADDING_LARGE, 0, 0));
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 右侧可以添加用户信息或操作按钮
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, PADDING_MEDIUM));
        rightPanel.setOpaque(false);
        titlePanel.add(rightPanel, BorderLayout.EAST);
        
        return titlePanel;
    }
    
    /**
     * 创建现代风格的表格
     */
    public static JScrollPane createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(44);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBackground(COLOR_BG_CARD);
        
        // 表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(COLOR_BG_MAIN);
        header.setForeground(COLOR_TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 48));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        
        // 表头渲染器
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_BG_CARD);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCorner());
        
        return scrollPane;
    }
    
    private static Component createCorner() {
        JPanel corner = new JPanel();
        corner.setBackground(COLOR_BG_MAIN);
        return corner;
    }
    
    /**
     * 创建下拉框
     */
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(COLOR_BG_CARD);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return combo;
    }
    
    /**
     * 创建输入框
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }
    
    /**
     * 创建搜索框（带图标）
     */
    public static JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        
        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建标签页面板
     */
    public static JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        tabs.setBackground(COLOR_BG_MAIN);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        return tabs;
    }
    
    /**
     * 创建通用标签
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(COLOR_TEXT_PRIMARY);
        return label;
    }
    
    /**
     * 创建次要标签
     */
    public static JLabel createSecondaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL);
        label.setForeground(COLOR_TEXT_SECONDARY);
        return label;
    }
}
