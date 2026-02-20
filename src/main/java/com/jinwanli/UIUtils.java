package com.jinwanli;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIUtils {
    public static final Color COLOR_BG_MAIN = new Color(240, 240, 240);
    public static final Color COLOR_BG_CARD = new Color(255, 255, 255);
    public static final Color COLOR_BG_HEADER = new Color(255, 255, 255);
    public static final Color COLOR_BG_SIDEBAR = new Color(15, 23, 42);
    public static final Color COLOR_BG_SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color COLOR_PRIMARY = new Color(59, 130, 246);
    public static final Color COLOR_PRIMARY_LIGHT = new Color(239, 246, 255);
    public static final Color COLOR_BORDER = new Color(200, 200, 200);
    public static final Color COLOR_TEXT_PRIMARY = new Color(0, 0, 0);
    public static final Color COLOR_TEXT_SECONDARY = new Color(100, 100, 100);
    public static final Color COLOR_BG_CONTROL = new Color(230, 230, 230);
    public static final Color COLOR_SUCCESS = new Color(34, 197, 94);
    public static final Color COLOR_SUCCESS_LIGHT = new Color(240, 253, 244);
    public static final Color COLOR_DANGER = new Color(239, 68, 68);
    public static final Color COLOR_DANGER_LIGHT = new Color(254, 242, 242);
    public static final Color COLOR_WARNING = new Color(251, 191, 36);
    public static final Color COLOR_WARNING_LIGHT = new Color(254, 249, 232);

    public static final Font FONT_TITLE = new Font("Dialog", Font.BOLD, 18);
    public static final Font FONT_HEADING = new Font("Dialog", Font.BOLD, 16);
    public static final Font FONT_SUBHEADING = new Font("Dialog", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Dialog", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Dialog", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Dialog", Font.PLAIN, 14);
    public static final Font FONT_TAB = new Font("Dialog", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Dialog", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Dialog", Font.BOLD, 16);
    public static final Font FONT_NUMBER = new Font("Dialog", Font.BOLD, 28);

    public static final int RADIUS_SMALL = 6;
    public static final int RADIUS_MEDIUM = 10;

    public static String[] getRecentYears() {
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        return years;
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(COLOR_BG_CARD);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(new Color(220, 53, 69));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return card;
    }

    public static JPanel createTitlePanel(String titleText) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_BG_HEADER);
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        titlePanel.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }

    public static JScrollPane createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBackground(COLOR_BG_CARD);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(COLOR_BG_MAIN);
        header.setForeground(COLOR_TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_BG_CARD);

        return scrollPane;
    }

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

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    public static JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        JLabel iconLabel = new JLabel("搜索");
        iconLabel.setFont(FONT_BODY);

        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    public static JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_TAB);
        tabs.setBackground(COLOR_BG_MAIN);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        return tabs;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(COLOR_TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSecondaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL);
        label.setForeground(COLOR_TEXT_SECONDARY);
        return label;
    }
}
