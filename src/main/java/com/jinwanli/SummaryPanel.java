package com.jinwanli;

import com.jinwanli.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SummaryPanel extends JPanel {
    private JComboBox<String> yearBox;
    private JComboBox<String> monthBox;
    
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel salaryLabel;
    private JLabel profitLabel;
    
    private JLabel empCountLabel;
    private JLabel salesCountLabel;
    private JLabel abnormalLabel;
    
    private JPanel chartPanel;
    private java.util.List<BarRegion> clickableBars = new java.util.ArrayList<>();

    public SummaryPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("企业经营总览"), BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(UIUtils.COLOR_BG_CARD);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        controlPanel.add(new JLabel(""));
        yearBox = UIUtils.createComboBox(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        controlPanel.add(yearBox);
        
        controlPanel.add(new JLabel("年 "));
        monthBox = UIUtils.createComboBox(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        monthBox.setSelectedItem(String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1));
        controlPanel.add(monthBox);
        
        controlPanel.add(new JLabel("月 "));
        
        JButton refreshBtn = UIUtils.createButton("刷新数据");
        refreshBtn.addActionListener(e -> refreshData());
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(refreshBtn);
        
        add(controlPanel, BorderLayout.SOUTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // KPI 卡片行
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(2000, 140));
        
        // 收入卡片 - 绿色
        incomeLabel = createKPICard(cardsPanel, "本月总收入", "0.00 元", 
            UIUtils.COLOR_SUCCESS_LIGHT, UIUtils.COLOR_SUCCESS);
        expenseLabel = createKPICard(cardsPanel, "本月杂项支出", "0.00 元", 
            UIUtils.COLOR_DANGER_LIGHT, UIUtils.COLOR_DANGER);
        salaryLabel = createKPICard(cardsPanel, "预计薪资成本", "0.00 元", 
            UIUtils.COLOR_WARNING_LIGHT, UIUtils.COLOR_WARNING);
        profitLabel = createKPICard(cardsPanel, "本月净利润", "0.00 元", 
            UIUtils.COLOR_PRIMARY_LIGHT, UIUtils.COLOR_PRIMARY);
        
        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createVerticalStrut(24));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCharts(g, getWidth(), getHeight());
            }
        };
        chartPanel.setBackground(UIUtils.COLOR_BG_CARD);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.COLOR_BORDER),
            new EmptyBorder(20, 20, 20, 20)
        ));
        chartPanel.setPreferredSize(new Dimension(0, 280));
        chartPanel.setMaximumSize(new Dimension(2000, 280));
        chartPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chartPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                for (BarRegion bar : clickableBars) {
                    if (bar.bounds.contains(e.getPoint())) {
                        showDetailDialog(bar.month, bar.type);
                        break;
                    }
                }
            }
        });
        
        JLabel chartTitle = new JLabel("收支可视化分析");
        chartTitle.setFont(UIUtils.FONT_SUBHEADING);
        chartTitle.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel chartContainer = new JPanel(new BorderLayout(0, 10));
        chartContainer.setOpaque(false);
        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        contentPanel.add(chartContainer);
        contentPanel.add(Box.createVerticalStrut(24));

        // 运营数据摘要
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 100));
        
        empCountLabel = createStatCard(statsPanel, "在职员工", "0 人", UIUtils.COLOR_PRIMARY);
        salesCountLabel = createStatCard(statsPanel, "本月订单", "0 单", UIUtils.COLOR_SUCCESS);
        abnormalLabel = createStatCard(statsPanel, "考勤异常", "0 人次", UIUtils.COLOR_DANGER);
        
        JLabel statsTitle = new JLabel("运营数据摘要");
        statsTitle.setFont(UIUtils.FONT_SUBHEADING);
        statsTitle.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        statsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel statsContainer = new JPanel(new BorderLayout(0, 10));
        statsContainer.setOpaque(false);
        statsContainer.add(statsTitle, BorderLayout.NORTH);
        statsContainer.add(statsPanel, BorderLayout.CENTER);
        
        contentPanel.add(statsContainer);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        
        refreshData();
    }
    
    /**
     * 创建 KPI 卡片 - 现代风格
     */
    private JLabel createKPICard(JPanel parent, String title, String value, Color bgColor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 背景
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIUtils.RADIUS_MEDIUM, UIUtils.RADIUS_MEDIUM);
                
                // 左侧强调条
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), UIUtils.RADIUS_SMALL, UIUtils.RADIUS_SMALL);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel tLabel = new JLabel(title);
        tLabel.setFont(UIUtils.FONT_BODY);
        tLabel.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        tLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // 值
        JLabel vLabel = new JLabel(value);
        vLabel.setFont(UIUtils.FONT_NUMBER);
        vLabel.setForeground(accentColor);
        vLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        vLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        card.add(tLabel);
        card.add(vLabel);
        parent.add(card);
        
        return vLabel;
    }
    
    /**
     * 创建统计卡片
     */
    private JLabel createStatCard(JPanel parent, String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(UIUtils.COLOR_BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIUtils.RADIUS_MEDIUM, UIUtils.RADIUS_MEDIUM);
                
                // 底部强调线
                g2.setColor(accentColor);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel tLabel = new JLabel(title);
        tLabel.setFont(UIUtils.FONT_BODY);
        tLabel.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        
        JLabel vLabel = new JLabel(value);
        vLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        vLabel.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        
        card.add(tLabel, BorderLayout.NORTH);
        card.add(vLabel, BorderLayout.CENTER);
        parent.add(card);
        
        return vLabel;
    }

    private double currentIncome = 0;
    private double currentExpense = 0;
    private double currentSalary = 0;

    public void refreshData() {
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String datePrefix = year + "-" + month;

        List<SalesRecord> sales = DataManager.getInstance().getSalesRecords();
        currentIncome = sales.stream()
                .filter(s -> s.getDate().startsWith(datePrefix))
                .mapToDouble(SalesRecord::getTotalAmount)
                .sum();
        
        List<ExpenseRecord> expenses = DataManager.getInstance().getExpenseRecords();
        currentExpense = expenses.stream()
                .filter(e -> e.getDate().startsWith(datePrefix))
                .mapToDouble(ExpenseRecord::getAmount)
                .sum();
        
        List<Employee> employees = DataManager.getInstance().getEmployees();
        currentSalary = employees.stream().mapToDouble(Employee::getTotalSalary).sum();
        
        long orderCount = sales.stream().filter(s -> s.getDate().startsWith(datePrefix)).count();
        long abnormalCount = 0;

        incomeLabel.setText(String.format("%.2f 元", currentIncome));
        expenseLabel.setText(String.format("%.2f 元", currentExpense));
        salaryLabel.setText(String.format("%.2f 元", currentSalary));
        
        double profit = currentIncome - currentExpense - currentSalary;
        profitLabel.setText(String.format("%.2f 元", profit));
        if (profit >= 0) {
            profitLabel.setForeground(UIUtils.COLOR_PRIMARY);
        } else {
            profitLabel.setForeground(UIUtils.COLOR_DANGER);
        }
        
        empCountLabel.setText(String.valueOf(employees.size()) + " 人");
        salesCountLabel.setText(String.valueOf(orderCount) + " 单");
        abnormalLabel.setText(String.valueOf(abnormalCount) + " 人次");
        
        chartPanel.repaint();
    }
    
    private void drawCharts(Graphics g, int w, int h) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        clickableBars.clear();
        
        if (currentIncome == 0 && currentExpense == 0 && currentSalary == 0) {
            g2d.setColor(UIUtils.COLOR_TEXT_SECONDARY);
            g2d.setFont(UIUtils.FONT_BODY);
            FontMetrics fm = g2d.getFontMetrics();
            String msg = "暂无数据，请添加销售记录";
            g2d.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }
        
        double max = Math.max(currentIncome, Math.max(currentExpense, currentSalary));
        if (max == 0) max = 1;
        
        int barWidth = 80;
        int spacing = 60;
        int startX = (w - (3 * barWidth + 2 * spacing)) / 2;
        int baseline = h - 40;
        int maxHeight = h - 80;
        
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String currentMonth = year + "-" + month;
        
        drawBar(g2d, startX, baseline, maxHeight, barWidth, currentIncome, max, UIUtils.COLOR_SUCCESS, "收入", currentMonth, "INCOME");
        drawBar(g2d, startX + barWidth + spacing, baseline, maxHeight, barWidth, currentExpense, max, UIUtils.COLOR_DANGER, "支出", currentMonth, "EXPENSE");
        drawBar(g2d, startX + 2 * (barWidth + spacing), baseline, maxHeight, barWidth, currentSalary, max, UIUtils.COLOR_WARNING, "薪资", currentMonth, "SALARY");
        
        drawLegend(g2d, 20, 20);
    }
    
    private void drawBar(Graphics2D g, int x, int y, int maxH, int w, double val, double maxVal, Color c, String label, String month, String type) {
        int h = (int) ((val / maxVal) * maxH);
        h = Math.max(h, 10);
        
        g.setColor(c);
        g.fillRoundRect(x, y - h, w, h, UIUtils.RADIUS_SMALL, UIUtils.RADIUS_SMALL);
        
        clickableBars.add(new BarRegion(new Rectangle(x, y - h, w, h), month, type));
        
        g.setColor(UIUtils.COLOR_TEXT_SECONDARY);
        g.setFont(UIUtils.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        int labelW = fm.stringWidth(label);
        g.drawString(label, x + (w - labelW) / 2, y + 20);
        
        String valStr = String.format("%.0f", val);
        g.setColor(UIUtils.COLOR_TEXT_PRIMARY);
        g.setFont(UIUtils.FONT_BODY_BOLD);
        fm = g.getFontMetrics();
        int valW = fm.stringWidth(valStr);
        g.drawString(valStr, x + (w - valW) / 2, y - h - 8);
    }
    
    private void drawLegend(Graphics2D g, int x, int y) {
        g.setFont(UIUtils.FONT_SMALL);
        
        // 收入
        g.setColor(UIUtils.COLOR_SUCCESS);
        g.fillRect(x, y, 12, 12);
        g.setColor(UIUtils.COLOR_TEXT_SECONDARY);
        g.drawString("收入", x + 18, y + 10);
        
        // 支出
        x += 80;
        g.setColor(UIUtils.COLOR_DANGER);
        g.fillRect(x, y, 12, 12);
        g.setColor(UIUtils.COLOR_TEXT_SECONDARY);
        g.drawString("支出", x + 18, y + 10);
        
        // 薪资
        x += 80;
        g.setColor(UIUtils.COLOR_WARNING);
        g.fillRect(x, y, 12, 12);
        g.setColor(UIUtils.COLOR_TEXT_SECONDARY);
        g.drawString("薪资", x + 18, y + 10);
    }

    private class BarRegion {
        Rectangle bounds;
        String month;
        String type;

        public BarRegion(Rectangle bounds, String month, String type) {
            this.bounds = bounds;
            this.month = month;
            this.type = type;
        }
    }

    private void showDetailDialog(String month, String type) {
        String title;
        if (type.equals("INCOME")) title = month + " 收入明细";
        else if (type.equals("EXPENSE")) title = month + " 杂项支出明细";
        else title = month + " 预计薪资成本明细";

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(750, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols;
        javax.swing.table.DefaultTableModel model;

        if (type.equals("INCOME")) {
            cols = new String[]{"日期", "客户/收货方", "商品", "单价(元)", "数量", "总金额(元)", "经手人"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (com.jinwanli.model.SalesRecord s : DataManager.getInstance().getSalesRecords()) {
                if (s.getDate().startsWith(month)) {
                    model.addRow(new Object[]{
                        s.getDate(), s.getShipperName(), s.getProductName(), 
                        String.format("%.2f", s.getPricePerJin()), s.getBasketCount(), 
                        String.format("%.2f", s.getTotalAmount()), s.getHandler()
                    });
                }
            }
        } else if (type.equals("EXPENSE")) {
            cols = new String[]{"日期", "分类", "投资项目", "金额(元)", "用途", "经手人"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (com.jinwanli.model.ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month)) {
                    model.addRow(new Object[]{
                        e.getDate(), e.getCategory(), 
                        (e.getTargetProject() == null || e.getTargetProject().isEmpty()) ? "-" : e.getTargetProject(),
                        String.format("%.2f", e.getAmount()), e.getUsage(), e.getHandler()
                    });
                }
            }
        } else {
            cols = new String[]{"姓名", "职位", "联系电话", "基本工资(元)", "绩效(元)", "加班补贴(元)", "预计总薪资(元)"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (com.jinwanli.model.Employee emp : DataManager.getInstance().getEmployees()) {
                model.addRow(new Object[]{
                    emp.getName(), 
                    emp.getPosition(), 
                    emp.getPhone(),
                    String.format("%.2f", emp.getBaseSalary()), 
                    String.format("%.2f", emp.getPerformanceSalary()), 
                    String.format("%.2f", emp.getOvertimeSalary()), 
                    String.format("%.2f", emp.getTotalSalary())
                });
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UIUtils.FONT_NORMAL);
        
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton closeBtn = UIUtils.createSecondaryButton("关闭");
        closeBtn.addActionListener(e -> dialog.setVisible(false));
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
