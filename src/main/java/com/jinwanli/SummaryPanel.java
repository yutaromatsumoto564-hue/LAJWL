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

    public SummaryPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("企业经营总览"), BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(UIUtils.COLOR_BG_CONTROL);
        
        controlPanel.add(new JLabel("统计年份:"));
        yearBox = new JComboBox<>(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        controlPanel.add(yearBox);
        
        controlPanel.add(new JLabel("统计月份:"));
        monthBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        monthBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        controlPanel.add(monthBox);
        
        JButton refreshBtn = UIUtils.createButton("刷新数据");
        refreshBtn.addActionListener(e -> refreshData());
        controlPanel.add(refreshBtn);
        
        add(controlPanel, BorderLayout.SOUTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        cardsPanel.setMaximumSize(new Dimension(2000, 120));
        
        incomeLabel = createKPICard(cardsPanel, "本月总收入 (元)", new Color(231, 249, 237), new Color(46, 204, 113));
        expenseLabel = createKPICard(cardsPanel, "本月杂项支出 (元)", new Color(253, 237, 236), new Color(231, 76, 60));
        salaryLabel = createKPICard(cardsPanel, "预计薪资成本 (元)", new Color(254, 249, 231), new Color(241, 196, 15));
        profitLabel = createKPICard(cardsPanel, "本月净利润 (元)", new Color(235, 245, 251), new Color(52, 152, 219));
        
        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCharts(g, getWidth(), getHeight());
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        chartPanel.setPreferredSize(new Dimension(0, 250));
        chartPanel.setMaximumSize(new Dimension(2000, 250));
        
        contentPanel.add(new JLabel("  收支可视化分析"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(chartPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        statsPanel.setMaximumSize(new Dimension(2000, 80));
        
        empCountLabel = createStatItem(statsPanel, "在职员工数");
        salesCountLabel = createStatItem(statsPanel, "本月订单数");
        abnormalLabel = createStatItem(statsPanel, "考勤异常人次");
        
        contentPanel.add(new JLabel("  运营数据摘要"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statsPanel);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        
        refreshData();
    }
    
    private JLabel createKPICard(JPanel parent, String title, Color bgColor, Color textColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tLabel.setForeground(Color.GRAY);
        
        JLabel vLabel = new JLabel("0.00");
        vLabel.setFont(new Font("Arial", Font.BOLD, 24));
        vLabel.setForeground(textColor);
        
        card.add(tLabel, BorderLayout.NORTH);
        card.add(vLabel, BorderLayout.CENTER);
        parent.add(card);
        return vLabel;
    }

    private JLabel createStatItem(JPanel parent, String title) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        JLabel vLabel = new JLabel("0");
        vLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        vLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        item.add(tLabel, BorderLayout.WEST);
        item.add(vLabel, BorderLayout.CENTER);
        parent.add(item);
        return vLabel;
    }

    private double currentIncome = 0;
    private double currentExpense = 0;
    private double currentSalary = 0;

    private void refreshData() {
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String datePrefix = year + "-" + (month.length() == 1 ? "0"+month : month);

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
        List<AttendanceRecord> attendance = DataManager.getInstance().getAttendanceByMonth(year, month);
        long abnormalCount = attendance.stream()
                .filter(a -> "迟到".equals(a.getStatus()) || "早退".equals(a.getStatus()) || "缺勤".equals(a.getStatus()))
                .count();

        incomeLabel.setText(String.format("%.2f", currentIncome));
        expenseLabel.setText(String.format("%.2f", currentExpense));
        salaryLabel.setText(String.format("%.2f", currentSalary));
        
        double profit = currentIncome - currentExpense - currentSalary;
        profitLabel.setText(String.format("%.2f", profit));
        if (profit >= 0) profitLabel.setForeground(new Color(52, 152, 219));
        else profitLabel.setForeground(Color.RED);
        
        empCountLabel.setText(String.valueOf(employees.size()));
        salesCountLabel.setText(String.valueOf(orderCount));
        abnormalLabel.setText(String.valueOf(abnormalCount));
        
        chartPanel.repaint();
    }
    
    private void drawCharts(Graphics g, int w, int h) {
        if (currentIncome == 0 && currentExpense == 0 && currentSalary == 0) {
            g.setColor(Color.GRAY);
            g.drawString("暂无数据", w/2 - 20, h/2);
            return;
        }
        
        double max = Math.max(currentIncome, Math.max(currentExpense, currentSalary));
        if (max == 0) max = 1;
        
        int barWidth = 60;
        int spacing = 100;
        int startX = (w - (3 * barWidth + 2 * spacing)) / 2;
        int baseline = h - 50;
        int maxHeight = h - 100;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBar(g2d, startX, baseline, maxHeight, barWidth, currentIncome, max, new Color(46, 204, 113), "收入");
        drawBar(g2d, startX + barWidth + spacing, baseline, maxHeight, barWidth, currentExpense, max, new Color(231, 76, 60), "支出");
        drawBar(g2d, startX + 2 * (barWidth + spacing), baseline, maxHeight, barWidth, currentSalary, max, new Color(241, 196, 15), "薪资");
    }
    
    private void drawBar(Graphics2D g, int x, int y, int maxH, int w, double val, double maxVal, Color c, String label) {
        int h = (int) ((val / maxVal) * maxH);
        
        g.setColor(c);
        g.fillRect(x, y - h, w, h);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int labelW = fm.stringWidth(label);
        g.drawString(label, x + (w - labelW) / 2, y + 20);
        
        String valStr = String.format("%.0f", val);
        int valW = fm.stringWidth(valStr);
        g.drawString(valStr, x + (w - valW) / 2, y - h - 5);
    }
}