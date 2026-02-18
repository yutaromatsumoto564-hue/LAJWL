package com.jinwanli;

import com.jinwanli.model.AttendanceRecord;
import com.jinwanli.model.Employee;
import com.jinwanli.util.AttendanceImporter;
import com.jinwanli.util.AttendanceImporter.MonthlyAttendance;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AttendancePanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 月考勤表相关
    private JTable monthlyTable;
    private DefaultTableModel monthlyModel;
    private JComboBox<String> yearBox;
    private JComboBox<String> monthBox;
    
    // 汇总数据相关
    private JTable summaryTable;
    private DefaultTableModel summaryModel;

    public AttendancePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("员工考勤管理"), BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_TAB);
        
        tabbedPane.addTab("月考勤表", createMonthlyView());
        tabbedPane.addTab("月度汇总", createSummaryViewWithDrop());
        tabbedPane.addTab("员工档案", createEmployeeView());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMonthlyView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        // 查询工具栏
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        queryPanel.setBackground(UIUtils.COLOR_BG_CARD);
        queryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER));
        
        queryPanel.add(new JLabel("年份:"));
        yearBox = UIUtils.createComboBox(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)));
        queryPanel.add(yearBox);
        
        queryPanel.add(new JLabel("月份:"));
        monthBox = UIUtils.createComboBox(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        monthBox.setSelectedItem(String.format("%02d", java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1));
        queryPanel.add(monthBox);
        
        queryPanel.add(new JLabel("月"));
        
        JButton queryBtn = UIUtils.createButton("刷新");
        queryBtn.addActionListener(e -> refreshMonthlyTable());
        queryPanel.add(queryBtn);
        
        JButton addBtn = UIUtils.createButton("录入");
        addBtn.addActionListener(e -> {
            AttendanceDialog dialog = new AttendanceDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            AttendanceRecord record = dialog.getData();
            if (record != null) {
                DataManager.getInstance().addAttendanceRecord(record);
                refreshMonthlyTable();
            }
        });
        queryPanel.add(addBtn);
        
        panel.add(queryPanel, BorderLayout.NORTH);
        
        // 考勤表格
        String[] columnNames = new String[33];
        columnNames[0] = "姓名";
        for (int i = 1; i <= 31; i++) {
            columnNames[i] = i + "日";
        }
        columnNames[32] = "工号";

        monthlyModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        monthlyTable = new JTable(monthlyModel);
        monthlyTable.setRowHeight(30);
        monthlyTable.setFont(UIUtils.FONT_NORMAL);
        monthlyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        monthlyTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        for (int i = 1; i <= 31; i++) {
            monthlyTable.getColumnModel().getColumn(i).setPreferredWidth(40);
        }
        
        monthlyTable.setSelectionBackground(UIUtils.COLOR_PRIMARY_LIGHT);
        monthlyTable.setSelectionForeground(UIUtils.COLOR_PRIMARY);
        
        JScrollPane scrollPane = new JScrollPane(monthlyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshMonthlyTable();
        
        return panel;
    }

    /**
     * 创建带拖拽功能的月度汇总视图
     */
    private JPanel createSummaryViewWithDrop() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        // 工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolPanel.setBackground(UIUtils.COLOR_BG_CARD);
        toolPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER));
        
        JLabel titleLabel = new JLabel("月度考勤汇总表");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        toolPanel.add(titleLabel);
        
        toolPanel.add(Box.createHorizontalStrut(20));
        
        JButton refreshBtn = UIUtils.createButton("刷新");
        refreshBtn.addActionListener(e -> refreshSummaryTable());
        toolPanel.add(refreshBtn);
        
        JButton importBtn = UIUtils.createButton("选择文件导入");
        importBtn.addActionListener(e -> importFromExcel());
        toolPanel.add(importBtn);
        
        panel.add(toolPanel, BorderLayout.NORTH);
        
        // 拖拽区域 + 表格
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        // 拖拽提示面板
        JPanel dropPanel = createDropPanel();
        contentPanel.add(dropPanel, BorderLayout.NORTH);
        
        // 汇总表格
        String[] cols = {"姓名", "工号", "月份", "应出勤(天)", "实际出勤(天)", "应出勤(小时)", 
                        "实际出勤(小时)", "计薪时长(h)", "加班费时长(h)", "调休时长(h)"};
        
        summaryModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(35);
        summaryTable.setFont(UIUtils.FONT_BODY);
        summaryTable.setSelectionBackground(UIUtils.COLOR_PRIMARY_LIGHT);
        
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        summaryTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        summaryTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        refreshSummaryTable();
        
        return panel;
    }
    
    /**
     * 创建拖拽面板
     */
    private JPanel createDropPanel() {
        JPanel dropPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 虚线边框
                g2.setColor(UIUtils.COLOR_PRIMARY);
                float[] dash = {5, 5};
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                // 背景
                g2.setColor(new Color(59, 130, 246, 20));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
            }
        };
        
        dropPanel.setLayout(new BorderLayout());
        dropPanel.setPreferredSize(new Dimension(0, 100));
        dropPanel.setOpaque(false);
        
        // 提示文字
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("📁");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        
        JLabel hintLabel = new JLabel("拖拽 Excel 文件到此处导入考勤数据");
        hintLabel.setFont(UIUtils.FONT_BODY);
        hintLabel.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        
        JLabel formatLabel = new JLabel("支持 .xlsx .xls .csv 格式");
        formatLabel.setFont(UIUtils.FONT_SMALL);
        formatLabel.setForeground(UIUtils.COLOR_TEXT_SECONDARY);
        
        textPanel.add(iconLabel);
        textPanel.add(Box.createHorizontalStrut(10));
        textPanel.add(hintLabel);
        
        JPanel formatPanel = new JPanel();
        formatPanel.setOpaque(false);
        formatPanel.add(formatLabel);
        
        dropPanel.add(textPanel, BorderLayout.CENTER);
        dropPanel.add(formatPanel, BorderLayout.SOUTH);
        
        // 设置拖拽监听
        setupDropTarget(dropPanel);
        
        return dropPanel;
    }
    
    /**
     * 设置拖拽功能
     */
    private void setupDropTarget(JPanel dropPanel) {
        dropPanel.setDropTarget(new DropTarget(dropPanel, new DropTargetListener() {
            private boolean highlight = false;
            
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                highlight = true;
                dropPanel.repaint();
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // Accept the drop action
            }
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                highlight = false;
                dropPanel.repaint();
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                highlight = false;
                dropPanel.repaint();
                
                try {
                    dtde.acceptDrop(dtde.getDropAction());
                    java.util.List<File> files = (java.util.List<File>) dtde.getTransferable().getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                    
                    if (files != null && !files.isEmpty()) {
                        File file = files.get(0);
                        String fileName = file.getName().toLowerCase();
                        
                        // 检查文件类型
                        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv")) {
                            importFile(file.getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(AttendancePanel.this, 
                                "不支持的文件格式！\n请选择 .xlsx .xls 或 .csv 文件", 
                                "格式错误", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AttendancePanel.this, 
                        "读取文件失败: " + e.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}
        }));
    }
    
    /**
     * 导入文件（文件选择或拖拽）
     */
    private void importFile(String filePath) {
        try {
            // 读取Excel数据
            List<MonthlyAttendance> records = AttendanceImporter.importFromExcel(filePath);
            
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(this, "未读取到考勤数据，请检查文件格式", "导入失败", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 获取员工列表用于匹配
            List<Employee> employees = DataManager.getInstance().getEmployees();
            
            // 统计结果
            AttendanceImporter.ImportResult importResult = new AttendanceImporter.ImportResult();
            importResult.total = records.size();
            
            String month = records.isEmpty() ? "" : records.get(0).getMonth();
            
            // 显示导入预览
            StringBuilder preview = new StringBuilder();
            preview.append("导入预览 (共 ").append(records.size()).append(" 条记录)\n\n");
            preview.append(String.format("%-10s %-6s %-10s %-10s %s\n", "姓名", "工号", "出勤天数", "计薪时长", "状态"));
            preview.append("------------------------------------------------\n");
            
            for (MonthlyAttendance record : records) {
                String empId = record.getEmployeeId();
                String matchedId = null;
                
                // 优先用工号匹配
                if (empId != null) {
                    matchedId = AttendanceImporter.matchEmployeeIdByCode(empId, employees);
                }
                
                // 否则用姓名匹配
                if (matchedId == null) {
                    matchedId = AttendanceImporter.matchEmployeeId(record.getEmployeeName(), employees);
                }
                
                if (matchedId != null) {
                    importResult.matched++;
                    DataManager.getInstance().saveMonthlyAttendance(month, matchedId, record);
                } else {
                    importResult.failed++;
                    importResult.errors.add("未匹配员工: " + record.getEmployeeName());
                }
                
                String status = matchedId != null ? "成功" : "未匹配";
                preview.append(String.format("%-10s %-6s %-10.1f %-10.1f %s\n", 
                    record.getEmployeeName(), 
                    empId != null ? empId : "-",
                    record.getActualDays(),
                    record.getPaidHours(),
                    status));
            }
            
            importResult.success = importResult.matched;
            
            // 显示结果
            String resultMsg = String.format(
                "导入完成！\n\n" +
                "成功: %d 条\n" +
                "失败: %d 条\n" +
                "所属月份: %s",
                importResult.success, importResult.failed, month
            );
            
            JOptionPane.showMessageDialog(this, preview.toString() + "\n\n" + resultMsg, "导入完成", JOptionPane.INFORMATION_MESSAGE);
            
            // 刷新显示
            refreshMonthlyTable();
            refreshSummaryTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * 选择文件导入
     */
    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择考勤Excel文件");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // 过滤Excel文件
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() || name.endsWith(".xlsx") || name.endsWith(".xls") || name.endsWith(".csv");
            }
            
            @Override
            public String getDescription() {
                return "Excel 文件 (*.xlsx, *.xls, *.csv)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            importFile(selectedFile.getAbsolutePath());
        }
    }

    private void refreshMonthlyTable() {
        monthlyModel.setRowCount(0);
        
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        
        List<Employee> employees = DataManager.getInstance().getEmployees();
        List<AttendanceRecord> monthRecords = DataManager.getInstance().getAttendanceByMonth(year, month);
        
        for (Employee emp : employees) {
            Object[] rowData = new Object[33];
            rowData[0] = emp.getName();
            rowData[32] = emp.getId();
            
            List<AttendanceRecord> myRecords = monthRecords.stream()
                    .filter(r -> r.getEmployeeId().equals(emp.getId()))
                    .collect(Collectors.toList());
            
            for (AttendanceRecord r : myRecords) {
                int day = r.getDayOfMonth();
                if (day >= 1 && day <= 31) {
                    String symbol = "√";
                    if ("迟到".equals(r.getStatus())) symbol = "L";
                    else if ("早退".equals(r.getStatus())) symbol = "E";
                    else if ("缺勤".equals(r.getStatus())) symbol = "X";
                    else if ("正常".equals(r.getStatus())) symbol = "√";
                    
                    if (r.getOvertimeHours() > 0) {
                        symbol += "(+" + (int)r.getOvertimeHours() + ")";
                    }
                    
                    rowData[day] = symbol;
                }
            }
            
            monthlyModel.addRow(rowData);
        }
    }
    
    private void refreshSummaryTable() {
        summaryModel.setRowCount(0);
        
        List<Object[]> summaries = DataManager.getInstance().getMonthlyAttendanceSummary();
        
        for (Object[] row : summaries) {
            summaryModel.addRow(row);
        }
    }

    private JPanel createEmployeeView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);
        
        // 工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolPanel.setBackground(UIUtils.COLOR_BG_CARD);
        toolPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.COLOR_BORDER));
        
        JLabel titleLabel = new JLabel("员工档案管理");
        titleLabel.setFont(UIUtils.FONT_HEADING);
        toolPanel.add(titleLabel);
        
        toolPanel.add(Box.createHorizontalStrut(20));
        
        JButton addBtn = UIUtils.createButton("添加员工");
        addBtn.addActionListener(e -> addEmployee());
        toolPanel.add(addBtn);
        
        panel.add(toolPanel, BorderLayout.NORTH);
        
        // 员工表格
        String[] cols = {"工号", "姓名", "职位", "联系电话", "基本工资(元)", "绩效(元)", "加班补贴(元)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(UIUtils.FONT_BODY);
        table.setSelectionBackground(UIUtils.COLOR_PRIMARY_LIGHT);
        table.getTableHeader().setFont(UIUtils.FONT_BODY_BOLD);
        
        // 操作按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(UIUtils.COLOR_BG_CARD);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COLOR_BORDER));
        
        JButton delBtn = UIUtils.createDangerButton("删除员工");
        delBtn.addActionListener(e -> deleteEmployee(table, model));
        btnPanel.add(delBtn);
        
        JButton printBtn = UIUtils.createButton("打印工资单");
        printBtn.addActionListener(e -> printPayslip(table));
        btnPanel.add(printBtn);
        
        // 加载数据
        Runnable loadData = () -> {
            model.setRowCount(0);
            for (Employee e : DataManager.getInstance().getEmployees()) {
                model.addRow(new Object[]{
                    e.getId(), 
                    e.getName(), 
                    e.getPosition(), 
                    e.getPhone(),
                    String.format("%.2f", e.getBaseSalary()),
                    String.format("%.2f", e.getPerformanceSalary()),
                    String.format("%.2f", e.getOvertimeSalary())
                });
            }
        };
        loadData.run();
        
        this.empTableRef = new Object[]{ model, table, loadData };
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private Object[] empTableRef;
    
    private void addEmployee() {
        EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        Employee newEmp = dialog.getData();
        
        if (newEmp != null) {
            DataManager.getInstance().addEmployee(newEmp);
            
            if (empTableRef != null && empTableRef[2] instanceof Runnable) {
                ((Runnable) empTableRef[2]).run();
            }
            
            refreshMonthlyTable();
            JOptionPane.showMessageDialog(this, "添加成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteEmployee(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一名员工", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该员工吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.getInstance().removeEmployee(row);
            
            if (empTableRef != null && empTableRef[2] instanceof Runnable) {
                ((Runnable) empTableRef[2]).run();
            }
            
            refreshMonthlyTable();
        }
    }
    
    private void printPayslip(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一名员工", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = (String) table.getValueAt(row, 0);
        Employee emp = DataManager.getInstance().getEmployeeById(id);
        
        if (emp != null) {
            printPayslip(emp);
        }
    }
    
    private void printPayslip(Employee e) {
        java.util.Map<String, String> content = new java.util.LinkedHashMap<>();
        content.put("员工姓名:", e.getName());
        content.put("员工职位:", e.getPosition());
        content.put("工号:", e.getId());
        content.put("----------------", "--------------------");
        content.put("基本工资:", String.format("%.2f", e.getBaseSalary()));
        content.put("绩效奖金:", String.format("%.2f", e.getPerformanceSalary()));
        content.put("加班补贴:", String.format("%.2f", e.getOvertimeSalary()));
        content.put("扣除项:", "0.00");
        content.put("----------------", "--------------------");
        content.put("实发工资:", String.format("￥ %.2f", e.getTotalSalary()));

        String footer = "金万里企业管理系统 - 薪资凭证\n请核对无误后签字确认。\n签字：__________";
        PdfUtils.generateAndOpenPdf("工资单-" + e.getName(), "员工薪资单", content, footer);
    }
}
