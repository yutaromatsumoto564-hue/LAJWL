package com.jinwanli;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;

public class UIUtils {
    public static final Color COLOR_PRIMARY = new Color(255, 215, 0);
    public static final Color COLOR_BG_MAIN = new Color(255, 255, 255);
    public static final Color COLOR_BG_TITLE = new Color(255, 248, 220);
    public static final Color COLOR_BG_CONTROL = new Color(240, 240, 240);
    
    public static final Font FONT_TITLE = new Font("微软雅黑", Font.BOLD, 24);
    public static final Font FONT_NORMAL = new Font("微软雅黑", Font.PLAIN, 14);
    public static final Font FONT_TAB = new Font("微软雅黑", Font.PLAIN, 16);
    public static final Font FONT_BOLD = new Font("微软雅黑", Font.BOLD, 16);

    public static String[] getRecentYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        return years;
    }

    public static JScrollPane createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(FONT_NORMAL);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.getTableHeader().setBackground(COLOR_BG_CONTROL);
        table.setSelectionBackground(COLOR_PRIMARY);
        
        return new JScrollPane(table);
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NORMAL);
        btn.setFocusPainted(false);
        return btn;
    }

    public static JPanel createTitlePanel(String titleText) {
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(COLOR_BG_TITLE);
        titlePanel.add(titleLabel);
        return titlePanel;
    }
}