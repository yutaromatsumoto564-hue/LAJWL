package com.jinwanli;

import com.jinwanli.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SummaryPanel extends JPanel {
    private JLabel incomeLabel, expenseLabel, salaryLabel, profitLabel;
    private JPanel chartPanel;
    private List<String> last6Months;
    private List<BarRegion> clickableBars = new ArrayList<>();

    public SummaryPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIUtils.COLOR_BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        last6Months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            Calendar c = (Calendar) cal.clone();
            c.add(Calendar.MONTH, -i);
            last6Months.add(sdf.format(c.getTime()));
        }

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        incomeLabel = createKPICard(cardsPanel, "本月总收入(含注资)", "0.00 元", UIUtils.COLOR_SUCCESS_LIGHT, UIUtils.COLOR_SUCCESS, "INCOME");
        expenseLabel = createKPICard(cardsPanel, "本月杂项支出", "0.00 元", UIUtils.COLOR_DANGER_LIGHT, UIUtils.COLOR_DANGER, "EXPENSE");
        salaryLabel = createKPICard(cardsPanel, "预计薪资成本", "0.00 元", UIUtils.COLOR_WARNING_LIGHT, UIUtils.COLOR_WARNING, "SALARY");
        profitLabel = createKPICard(cardsPanel, "本月净利润", "0.00 元", UIUtils.COLOR_PRIMARY_LIGHT, UIUtils.COLOR_PRIMARY, "PROFIT");

        add(cardsPanel, BorderLayout.NORTH);

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                clickableBars.clear();

                int width = getWidth();
                int height = getHeight();
                int padding = 50;

                g2d.setColor(UIUtils.COLOR_BORDER);
                g2d.drawLine(padding, height - padding, width - padding, height - padding);
                g2d.drawLine(padding, padding, padding, height - padding);

                double maxVal = 1; 
                double[] incomes = new double[6];
                double[] expenses = new double[6];
                double[] salaries = new double[6];

                for (int i = 0; i < 6; i++) {
                    String m = last6Months.get(i);
                    for (SalesRecord s : DataManager.getInstance().getSalesRecords()) {
                        if (s.getDate().startsWith(m)) incomes[i] += s.getTotalAmount();
                    }
                    for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                        if (e.getDate().startsWith(m)) {
                            if (isIncomeCategory(e.getCategory())) {
                                incomes[i] += e.getAmount();
                            } else {
                                expenses[i] += e.getAmount();
                            }
                        }
                    }
                    for (Employee emp : DataManager.getInstance().getEmployees()) {
                        salaries[i] += emp.getTotalSalary();
                    }
                    maxVal = Math.max(maxVal, Math.max(incomes[i], Math.max(expenses[i], salaries[i])));
                }

                int barWidth = 20;
                int spacing = 15;
                int groupWidth = barWidth * 3 + spacing * 2;
                int availableWidth = width - 2 * padding;
                int groupSpacing = availableWidth / 6;

                for (int i = 0; i < 6; i++) {
                    int xBase = padding + i * groupSpacing + (groupSpacing - groupWidth) / 2;
                    
                    int hIncome = (int) ((incomes[i] / maxVal) * (height - 2 * padding));
                    g2d.setColor(UIUtils.COLOR_SUCCESS);
                    g2d.fillRect(xBase, height - padding - hIncome, barWidth, hIncome);
                    clickableBars.add(new BarRegion(new Rectangle(xBase, height - padding - hIncome, barWidth, hIncome), last6Months.get(i), "INCOME"));

                    int hExpense = (int) ((expenses[i] / maxVal) * (height - 2 * padding));
                    g2d.setColor(UIUtils.COLOR_DANGER);
                    g2d.fillRect(xBase + barWidth + spacing, height - padding - hExpense, barWidth, hExpense);
                    clickableBars.add(new BarRegion(new Rectangle(xBase + barWidth + spacing, height - padding - hExpense, barWidth, hExpense), last6Months.get(i), "EXPENSE"));

                    int hSalary = (int) ((salaries[i] / maxVal) * (height - 2 * padding));
                    g2d.setColor(UIUtils.COLOR_WARNING);
                    g2d.fillRect(xBase + 2 * (barWidth + spacing), height - padding - hSalary, barWidth, hSalary);
                    clickableBars.add(new BarRegion(new Rectangle(xBase + 2 * (barWidth + spacing), height - padding - hSalary, barWidth, hSalary), last6Months.get(i), "SALARY"));

                    g2d.setColor(UIUtils.COLOR_TEXT_SECONDARY);
                    g2d.setFont(new Font("Dialog", Font.PLAIN, 12));
                    g2d.drawString(last6Months.get(i), xBase + 5, height - padding + 20);
                }
            }
        };
        
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.COLOR_BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        chartPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (BarRegion bar : clickableBars) {
                    if (bar.bounds.contains(e.getPoint())) {
                        showDetailDialog(bar.month, bar.type);
                        break;
                    }
                }
            }
        });

        add(chartPanel, BorderLayout.CENTER);
        refreshData();
    }

    private boolean isIncomeCategory(String category) {
        if (category == null) return false;
        return category.contains("(收入)") 
            || category.contains("注资") 
            || category.contains("注入") 
            || category.contains("投资注入") 
            || category.contains("补贴");
    }

    private JLabel createKPICard(JPanel parent, String title, String value, Color bgColor, Color fgColor, String clickType) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        tLabel.setForeground(fgColor);
        
        JLabel vLabel = new JLabel(value);
        vLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        vLabel.setForeground(fgColor);

        card.add(tLabel, BorderLayout.NORTH);
        card.add(vLabel, BorderLayout.CENTER);
        
        if (clickType != null) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.setToolTipText("点击查看本月明细");
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String currentMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
                    showDetailDialog(currentMonth, clickType);
                }
            });
        }
        
        parent.add(card);
        return vLabel;
    }

    public void refreshData() {
        String currentMonth = new SimpleDateFormat("yyyy-MM").format(new Date());

        double currentIncome = 0;
        for (SalesRecord s : DataManager.getInstance().getSalesRecords()) {
            if (s.getDate().startsWith(currentMonth)) currentIncome += s.getTotalAmount();
        }

        double currentExpense = 0;
        double otherIncome = 0;
        for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
            if (e.getDate().startsWith(currentMonth)) {
                if (isIncomeCategory(e.getCategory())) {
                    otherIncome += e.getAmount(); 
                } else {
                    currentExpense += e.getAmount(); 
                }
            }
        }

        double currentSalary = 0;
        for (Employee emp : DataManager.getInstance().getEmployees()) {
            currentSalary += emp.getTotalSalary();
        }

        double totalRevenue = currentIncome + otherIncome;
        double profit = totalRevenue - currentExpense - currentSalary;

        incomeLabel.setText(String.format("%.2f 元", totalRevenue));
        expenseLabel.setText(String.format("%.2f 元", currentExpense));
        salaryLabel.setText(String.format("%.2f 元", currentSalary));
        profitLabel.setText(String.format("%.2f 元", profit));

        chartPanel.repaint();
    }

    private void showDetailDialog(String month, String type) {
        String title;
        if (type.equals("INCOME")) title = month + " 收入明细(含注资)";
        else if (type.equals("EXPENSE")) title = month + " 杂项支出明细";
        else if (type.equals("SALARY")) title = month + " 预计薪资成本明细";
        else title = month + " 净利润核算账单";

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(750, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols;
        javax.swing.table.DefaultTableModel model;

        if (type.equals("INCOME")) {
            cols = new String[]{"日期", "类型/货主", "单价/详情", "数量/权重", "总额(元)", "经手人"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            
            for (SalesRecord s : DataManager.getInstance().getSalesRecords()) {
                if (s.getDate().startsWith(month)) {
                    model.addRow(new Object[]{
                        s.getDate(), "【出货】" + s.getShipperName(), 
                        String.format("%.2f 元/斤", s.getUnitPrice()), 
                        s.getTotalWeight() + " 斤", 
                        String.format("%.2f", s.getTotalAmount()), s.getHandler()
                    });
                }
            }
            for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month) && isIncomeCategory(e.getCategory())) {
                    model.addRow(new Object[]{
                        e.getDate(), "【" + e.getCategory() + "】", 
                        e.getUsage(), "-", 
                        String.format("%.2f", e.getAmount()), e.getHandler()
                    });
                }
            }
        } else if (type.equals("EXPENSE")) {
            cols = new String[]{"日期", "分类", "金额(元)", "用途", "经手人"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month) && !isIncomeCategory(e.getCategory())) {
                    model.addRow(new Object[]{
                        e.getDate(), e.getCategory(), 
                        String.format("%.2f", e.getAmount()), e.getUsage(), e.getHandler()
                    });
                }
            }
        } else if (type.equals("SALARY")) {
            cols = new String[]{"姓名", "职位", "联系电话", "基本工资(元)", "绩效(元)", "加班补贴(元)", "预计总薪资(元)"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Employee emp : DataManager.getInstance().getEmployees()) {
                model.addRow(new Object[]{
                    emp.getName(), emp.getPosition(), emp.getPhone(),
                    String.format("%.2f", emp.getBaseSalary()), 
                    String.format("%.2f", emp.getPerformanceSalary()), 
                    String.format("%.2f", emp.getOvertimeSalary()), 
                    String.format("%.2f", emp.getTotalSalary())
                });
            }
        } else {
            cols = new String[]{"核算项目", "金额(元)", "说明"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            
            double totalInc = 0;
            for (SalesRecord s : DataManager.getInstance().getSalesRecords()) {
                if (s.getDate().startsWith(month)) totalInc += s.getTotalAmount();
            }
            for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month) && isIncomeCategory(e.getCategory())) totalInc += e.getAmount();
            }
            
            double totalExp = 0;
            for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month) && !isIncomeCategory(e.getCategory())) totalExp += e.getAmount();
            }
            
            double totalSal = 0;
            for (Employee emp : DataManager.getInstance().getEmployees()) {
                totalSal += emp.getTotalSalary();
            }
            
            model.addRow(new Object[]{"【+】本月总收入(含注资)", String.format("%.2f", totalInc), "包含农产品出货营业额与各类注资补贴"});
            model.addRow(new Object[]{"【-】本月杂项支出", String.format("%.2f", totalExp), "日常运营各类开销"});
            model.addRow(new Object[]{"【-】预计薪资成本", String.format("%.2f", totalSal), "系统录入的所有员工薪资总计"});
            model.addRow(new Object[]{"【=】本月净利润", String.format("%.2f", totalInc - totalExp - totalSal), "当月最终核算利润结余"});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dialog.setVisible(false));
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private class BarRegion {
        Rectangle bounds; String month; String type;
        public BarRegion(Rectangle bounds, String month, String type) {
            this.bounds = bounds; this.month = month; this.type = type;
        }
    }
}
