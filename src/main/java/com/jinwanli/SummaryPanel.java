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
    private String currentDisplayMonth; // 当前显示的月份

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
        
        // 初始化当前显示月份为当前系统月份
        currentDisplayMonth = new SimpleDateFormat("yyyy-MM").format(new Date());

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        incomeLabel = createKPICard(cardsPanel, "本月总收入(含注资)", "0.00 元", UIUtils.COLOR_SUCCESS_LIGHT, UIUtils.COLOR_SUCCESS, "INCOME");
        expenseLabel = createKPICard(cardsPanel, "本月杂项支出", "0.00 元", UIUtils.COLOR_DANGER_LIGHT, UIUtils.COLOR_DANGER, "EXPENSE");
        salaryLabel = createKPICard(cardsPanel, "薪资成本", "0.00 元", UIUtils.COLOR_WARNING_LIGHT, UIUtils.COLOR_WARNING, "SALARY");
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
                    // 从财务收支记录中获取当月员工工资
                    double monthlySalaryFromExpense = 0;
                    for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                        if (e.getDate().startsWith(m) && e.getCategory().equals("员工工资")) {
                            monthlySalaryFromExpense += e.getAmount();
                        }
                    }
                    // 从月度工资记录中获取当月员工工资
                    for (MonthlySalaryRecord record : DataManager.getInstance().getMonthlySalaryRecords()) {
                        if (record.getMonth().equals(m)) {
                            monthlySalaryFromExpense += record.getTotalSalary();
                        }
                    }
                    if (monthlySalaryFromExpense > 0) {
                        salaries[i] = monthlySalaryFromExpense;
                    } else {
                        // 如果没有财务收支中的员工工资记录和月度工资记录，使用员工的固定薪资
                        for (Employee emp : DataManager.getInstance().getEmployees()) {
                            salaries[i] += emp.getTotalSalary();
                        }
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

                    // 绘制日期标签，增大字体大小
                    g2d.setColor(UIUtils.COLOR_TEXT_SECONDARY);
                    g2d.setFont(new Font("Dialog", Font.PLAIN, 16)); // 增大字体大小到16
                    g2d.drawString(last6Months.get(i), xBase + 5, height - padding + 25); // 调整位置以适应更大的字体
                    
                    // 添加日期标签的可点击区域
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(last6Months.get(i));
                    int textHeight = fm.getHeight();
                    clickableBars.add(new BarRegion(
                        new Rectangle(xBase + 5, height - padding + 15, textWidth, textHeight), 
                        last6Months.get(i), "DATE"
                    ));
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
                        if (bar.type.equals("DATE")) {
                            // 点击日期标签，更新卡片为当月数据
                            updateCardsForMonth(bar.month);
                        } else {
                            // 点击其他类型的柱子，显示明细
                            showDetailDialog(bar.month, bar.type);
                        }
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
            card.setToolTipText("点击查看" + currentDisplayMonth + "明细");
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showDetailDialog(currentDisplayMonth, clickType);
                }
            });
        }
        
        parent.add(card);
        return vLabel;
    }

    public void refreshData() {
        String currentMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        updateCardsForMonth(currentMonth);
    }

    // 根据指定月份更新卡片数据
    public void updateCardsForMonth(String month) {
        // 更新当前显示月份
        currentDisplayMonth = month;
        
        double currentIncome = 0;
        for (SalesRecord s : DataManager.getInstance().getSalesRecords()) {
            if (s.getDate().startsWith(month)) currentIncome += s.getTotalAmount();
        }

        double currentExpense = 0;
        double otherIncome = 0;
        for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
            if (e.getDate().startsWith(month)) {
                if (isIncomeCategory(e.getCategory())) {
                    otherIncome += e.getAmount(); 
                } else {
                    currentExpense += e.getAmount(); 
                }
            }
        }

        double currentSalary = 0;
        // 从财务收支记录中获取当月员工工资
        for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
            if (e.getDate().startsWith(month) && e.getCategory().equals("员工工资")) {
                currentSalary += e.getAmount();
            }
        }
        // 从月度工资记录中获取当月员工工资
        for (MonthlySalaryRecord record : DataManager.getInstance().getMonthlySalaryRecords()) {
            if (record.getMonth().equals(month)) {
                currentSalary += record.getTotalSalary();
            }
        }
        // 如果没有财务收支中的员工工资记录和月度工资记录，使用员工的固定薪资作为备选
        if (currentSalary == 0) {
            for (Employee emp : DataManager.getInstance().getEmployees()) {
                currentSalary += emp.getTotalSalary();
            }
        }

        double totalRevenue = currentIncome + otherIncome;
        double profit = totalRevenue - currentExpense - currentSalary;

        incomeLabel.setText(String.format("%.2f 元", totalRevenue));
        expenseLabel.setText(String.format("%.2f 元", currentExpense));
        salaryLabel.setText(String.format("%.2f 元", currentSalary));
        profitLabel.setText(String.format("%.2f 元", profit));

        // 不需要刷新图表，因为图表显示的是过去6个月的趋势
    }

    private void showDetailDialog(String month, String type) {
        String title;
        if (type.equals("INCOME")) title = month + " 收入明细(含注资)";
        else if (type.equals("EXPENSE")) title = month + " 杂项支出明细";
        else if (type.equals("SALARY")) title = month + " 薪资成本明细";
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
                @Override public boolean isCellEditable(int r, int c) { return true; } // 允许编辑
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
                @Override public boolean isCellEditable(int r, int c) { return true; } // 允许编辑
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
            cols = new String[]{"日期", "金额(元)", "用途"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return true; } // 允许编辑
            };
            
            // 从财务收支记录中获取员工工资记录
            for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month) && e.getCategory().equals("员工工资")) {
                    model.addRow(new Object[]{
                        e.getDate(),
                        String.format("%.2f", e.getAmount()),
                        e.getUsage()
                    });
                }
            }
            // 从月度工资记录中获取当月员工工资
            for (MonthlySalaryRecord record : DataManager.getInstance().getMonthlySalaryRecords()) {
                if (record.getMonth().equals(month)) {
                    model.addRow(new Object[]{
                        record.getMonth() + "-01",
                        String.format("%.2f", record.getTotalSalary()),
                        record.getEmployeeName() + " - " + record.getEmployeePosition()
                    });
                }
            }
        } else {
            cols = new String[]{"核算项目", "金额(元)", "说明"};
            model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return true; } // 允许编辑
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
            // 从财务收支记录中获取当月员工工资
            for (ExpenseRecord e : DataManager.getInstance().getExpenseRecords()) {
                if (e.getDate().startsWith(month) && e.getCategory().equals("员工工资")) {
                    totalSal += e.getAmount();
                }
            }
            // 从月度工资记录中获取当月员工工资
            for (MonthlySalaryRecord record : DataManager.getInstance().getMonthlySalaryRecords()) {
                if (record.getMonth().equals(month)) {
                    totalSal += record.getTotalSalary();
                }
            }
            
            model.addRow(new Object[]{"本月总收入(含注资)", String.format("%.2f", totalInc), "包含农产品出货营业额与各类注资补贴"});
            model.addRow(new Object[]{"本月杂项支出", String.format("%.2f", totalExp), "日常运营各类开销"});
            model.addRow(new Object[]{"薪资成本", String.format("%.2f", totalSal), "财务收支中的员工工资和月度工资总计"});
            model.addRow(new Object[]{"本月净利润", String.format("%.2f", totalInc - totalExp - totalSal), "当月最终核算利润结余"});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        // 添加表格双击事件监听器
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 双击事件处理已移至表格编辑监听器
            }
        });
        
        // 添加表格编辑监听器
        table.getModel().addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    Object newValue = model.getValueAt(row, col);
                    
                    // 根据表格类型处理不同的编辑逻辑
                    if (type.equals("INCOME")) {
                        // 收入明细编辑处理
                        String date = (String) model.getValueAt(row, 0);
                        String category = (String) model.getValueAt(row, 1);
                        String amountStr = (String) model.getValueAt(row, 4);
                        String handler = (String) model.getValueAt(row, 5);
                        
                        // 检查是否是销售记录（包含"出货"）
                        if (category.contains("出货")) {
                            // 查找对应的销售记录
                            List<SalesRecord> sales = DataManager.getInstance().getSalesRecords();
                            for (int i = 0; i < sales.size(); i++) {
                                SalesRecord sale = sales.get(i);
                                if (sale.getDate().equals(date) && 
                                    sale.getHandler().equals(handler)) {
                                    // 更新销售记录
                                    try {
                                        double amount = Double.parseDouble(amountStr);
                                        sale.setTotalAmount(amount);
                                        DataManager.getInstance().updateSalesRecord(i, sale);
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    break;
                                }
                            }
                        } else {
                            // 查找对应的支出记录（注资等）
                            List<ExpenseRecord> expenses = DataManager.getInstance().getExpenseRecords();
                            for (int i = 0; i < expenses.size(); i++) {
                                ExpenseRecord expense = expenses.get(i);
                                // 提取分类名称（去掉【】括号）
                                String categoryName = category.replace("【", "").replace("】", "");
                                if (expense.getDate().equals(date) && 
                                    expense.getCategory().equals(categoryName) &&
                                    expense.getHandler().equals(handler)) {
                                    // 更新支出记录
                                    try {
                                        double amount = Double.parseDouble(amountStr);
                                        expense.setAmount(amount);
                                        DataManager.getInstance().updateExpenseRecord(i, expense);
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    break;
                                }
                            }
                        }
                        
                        // 刷新经营总览
                        MainFrame mainFrame = MainFrame.getInstance();
                        if (mainFrame != null) {
                            SummaryPanel summaryPanel = mainFrame.getSummaryPanel();
                            if (summaryPanel != null) {
                                summaryPanel.refreshData();
                            }
                        }
                    } else if (type.equals("EXPENSE")) {
                        // 支出明细编辑处理
                        String date = (String) model.getValueAt(row, 0);
                        String amountStr = (String) model.getValueAt(row, 2);
                        String usage = (String) model.getValueAt(row, 3);
                        String handler = (String) model.getValueAt(row, 4);
                        
                        // 查找对应的支出记录
                        List<ExpenseRecord> expenses = DataManager.getInstance().getExpenseRecords();
                        for (int i = 0; i < expenses.size(); i++) {
                            ExpenseRecord expense = expenses.get(i);
                            if (expense.getDate().equals(date) && 
                                expense.getUsage().equals(usage) && 
                                expense.getHandler().equals(handler)) {
                                // 更新支出记录
                                try {
                                    double amount = Double.parseDouble(amountStr);
                                    expense.setAmount(amount);
                                    DataManager.getInstance().updateExpenseRecord(i, expense);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                break;
                            }
                        }
                        
                        // 刷新经营总览
                        MainFrame mainFrame = MainFrame.getInstance();
                        if (mainFrame != null) {
                            SummaryPanel summaryPanel = mainFrame.getSummaryPanel();
                            if (summaryPanel != null) {
                                summaryPanel.refreshData();
                            }
                        }
                    } else if (type.equals("SALARY")) {
                        // 薪资明细编辑处理（包含财务收支记录和月度工资记录）
                        String date = (String) model.getValueAt(row, 0);
                        String amountStr = (String) model.getValueAt(row, 1);
                        String usage = (String) model.getValueAt(row, 2);
                        
                        // 判断是否是月度工资记录（用途格式为：员工姓名 - 职位）
                        if (usage.contains(" - ")) {
                            // 月度工资记录
                            String[] parts = usage.split(" - ");
                            String employeeName = parts[0];
                            String employeePosition = parts[1];
                            
                            // 查找对应的月度工资记录
                            List<MonthlySalaryRecord> records = DataManager.getInstance().getMonthlySalaryRecords();
                            for (int i = 0; i < records.size(); i++) {
                                MonthlySalaryRecord record = records.get(i);
                                if (record.getMonth().equals(month) && 
                                    record.getEmployeeName().equals(employeeName) &&
                                    record.getEmployeePosition().equals(employeePosition)) {
                                    // 根据列索引更新不同字段
                                    switch (col) {
                                        case 0: // 日期（月份）
                                            String newMonth = newValue.toString().substring(0, 7);
                                            record.setMonth(newMonth);
                                            break;
                                        case 1: // 金额
                                            try {
                                                double amount = Double.parseDouble(newValue.toString());
                                                record.setTotalSalary(amount);
                                            } catch (Exception ex) {
                                                JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                                return;
                                            }
                                            break;
                                        case 2: // 用途（员工姓名 - 职位）
                                            String[] newParts = newValue.toString().split(" - ");
                                            if (newParts.length == 2) {
                                                record.setEmployeeName(newParts[0]);
                                                record.setEmployeePosition(newParts[1]);
                                            }
                                            break;
                                    }
                                    // 更新记录
                                    DataManager.getInstance().updateMonthlySalaryRecord(i, record);
                                    break;
                                }
                            }
                        } else {
                            // 财务收支记录
                            List<ExpenseRecord> expenses = DataManager.getInstance().getExpenseRecords();
                            for (int i = 0; i < expenses.size(); i++) {
                                ExpenseRecord expense = expenses.get(i);
                                if (expense.getDate().equals(date) && 
                                    expense.getCategory().equals("员工工资") &&
                                    expense.getUsage().equals(usage)) {
                                    // 根据列索引更新不同字段
                                    switch (col) {
                                        case 0: // 日期
                                            expense.setDate((String) newValue);
                                            break;
                                        case 1: // 金额
                                            try {
                                                double amount = Double.parseDouble(newValue.toString());
                                                expense.setAmount(amount);
                                            } catch (Exception ex) {
                                                JOptionPane.showMessageDialog(dialog, "请输入有效数字！", "错误", JOptionPane.ERROR_MESSAGE);
                                                return;
                                            }
                                            break;
                                        case 2: // 用途
                                            expense.setUsage((String) newValue);
                                            break;
                                    }
                                    // 更新记录
                                    DataManager.getInstance().updateExpenseRecord(i, expense);
                                    break;
                                }
                            }
                        }
                        
                        // 刷新经营总览
                        MainFrame mainFrame = MainFrame.getInstance();
                        if (mainFrame != null) {
                            SummaryPanel summaryPanel = mainFrame.getSummaryPanel();
                            if (summaryPanel != null) {
                                summaryPanel.refreshData();
                            }
                        }
                        // 净利润核算账单编辑处理
                        // 这里需要根据实际数据结构进行处理
                    }
                }
            }
        });
        
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
