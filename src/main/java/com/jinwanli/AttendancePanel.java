package com.jinwanli;

import com.jinwanli.model.AttendanceRecord;
import com.jinwanli.model.Employee;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttendancePanel extends JPanel {
    private JTabbedPane tabbedPane;

    private JTable monthlyTable;
    private DefaultTableModel monthlyModel;
    private JComboBox<String> yearBox;
    private JComboBox<String> monthBox;
    private JLabel grandTotalLabel;

    private JTable employeeTable;
    private Runnable refreshEmp;

    public AttendancePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.COLOR_BG_MAIN);
        add(UIUtils.createTitlePanel("员工考勤管理"), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_TAB);

        tabbedPane.addTab("月考勤表", createMonthlyView());
        tabbedPane.addTab("员工档案管理", createEmployeeView());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMonthlyView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);

        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.setBackground(UIUtils.COLOR_BG_CONTROL);

        queryPanel.add(new JLabel("年份:"));
        yearBox = new JComboBox<>(UIUtils.getRecentYears());
        yearBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        yearBox.addActionListener(e -> refreshMonthlyTable());
        queryPanel.add(yearBox);

        queryPanel.add(new JLabel("月份:"));
        monthBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        monthBox.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
        monthBox.addActionListener(e -> refreshMonthlyTable());
        queryPanel.add(monthBox);

        JButton importBtn = UIUtils.createButton("导入考勤(Excel)");
        importBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    com.jinwanli.util.AttendanceImporter.importDailyAttendance(
                        chooser.getSelectedFile().getAbsolutePath(),
                        (String) yearBox.getSelectedItem(),
                        (String) monthBox.getSelectedItem()
                    );
                    refreshMonthlyTable();
                    refreshEmp.run();
                    JOptionPane.showMessageDialog(this, "考勤导入成功！");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        queryPanel.add(importBtn);

        JButton addBtn = UIUtils.createButton("录入考勤");
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

        JButton exportBtn = UIUtils.createButton("导出当月报表");
        exportBtn.addActionListener(e -> {
            String y = (String) yearBox.getSelectedItem();
            String m = String.format("%02d", Integer.parseInt((String) monthBox.getSelectedItem()));
            BackupManager.performBackup(y, m);
        });
        queryPanel.add(exportBtn);

        panel.add(queryPanel, BorderLayout.NORTH);

        String[] columnNames = new String[33];
        columnNames[0] = "姓名";
        for (int i = 1; i <= 31; i++) {
            columnNames[i] = i + "日";
        }
        columnNames[32] = "总时长(h)";

        monthlyModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return column >= 1 && column <= 31;
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
                
                String empName = (String) getValueAt(row, 0);
                Employee emp = DataManager.getInstance().getEmployeeByName(empName);
                if (emp == null) return;

                String year = (String) yearBox.getSelectedItem();
                String month = String.format("%02d", Integer.parseInt((String) monthBox.getSelectedItem()));
                String date = year + "-" + month + "-" + String.format("%02d", column);
                
                String valStr = aValue.toString().trim();
                double hours = valStr.isEmpty() ? 0 : Double.parseDouble(valStr);

                AttendanceRecord record = new AttendanceRecord(emp.getId(), date, hours);
                DataManager.getInstance().addAttendanceRecord(record);
                
                SwingUtilities.invokeLater(() -> refreshMonthlyTable());
            }
        };

        monthlyTable = new JTable(monthlyModel);
        monthlyTable.setRowHeight(30);
        monthlyTable.setFont(UIUtils.FONT_NORMAL);
        monthlyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        monthlyTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        for (int i = 1; i <= 31; i++) {
            monthlyTable.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        monthlyTable.getColumnModel().getColumn(32).setPreferredWidth(60);

        monthlyTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                c.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
                ((javax.swing.JComponent) c).setToolTipText(null);

                if (column >= 1 && column <= 31) {
                    String empName = (String) table.getValueAt(row, 0);
                    Employee emp = DataManager.getInstance().getEmployeeByName(empName);
                    
                    if (emp != null) {
                        String year = (String) yearBox.getSelectedItem();
                        String month = String.format("%02d", Integer.parseInt((String) monthBox.getSelectedItem()));
                        String date = year + "-" + month + "-" + String.format("%02d", column);
                        
                        AttendanceRecord record = DataManager.getInstance().getAttendanceRecords().stream()
                                .filter(r -> r.getEmployeeId().equals(emp.getId()) && r.getDate().equals(date))
                                .findFirst().orElse(null);
                                
                        if (record != null && record.isAbnormal()) {
                            c.setForeground(UIUtils.COLOR_DANGER);
                            ((javax.swing.JComponent) c).setToolTipText(record.getPunchDetails());
                            
                            if (value == null || value.toString().isEmpty()) {
                                setText("");
                            }
                        }
                    }
                }
                
                if (isSelected) {
                    c.setForeground(table.getSelectionForeground());
                }
                
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(monthlyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setBackground(UIUtils.COLOR_BG_CARD);
        grandTotalLabel = new JLabel("当月所有员工出勤总时长: 0.0 h");
        grandTotalLabel.setFont(new java.awt.Font("Microsoft YaHei", java.awt.Font.BOLD, 16));
        grandTotalLabel.setForeground(UIUtils.COLOR_PRIMARY);
        bottomPanel.add(grandTotalLabel);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        refreshMonthlyTable();

        return panel;
    }

    public void refreshMonthlyTable() {
        monthlyModel.setRowCount(0);

        String year = (String) yearBox.getSelectedItem();
        String month = String.format("%02d", Integer.parseInt((String) monthBox.getSelectedItem()));

        List<Employee> employees = DataManager.getInstance().getEmployees();
        List<AttendanceRecord> monthRecords = DataManager.getInstance().getAttendanceByMonth(year, month);

        double grandTotalHours = 0.0;

        for (Employee emp : employees) {
            List<AttendanceRecord> myRecords = monthRecords.stream()
                    .filter(r -> r.getEmployeeId().equals(emp.getId()))
                    .collect(Collectors.toList());

            boolean hasValidRecord = myRecords.stream().anyMatch(r -> 
                r.getWorkHours() > 0 || (r.getPunchDetails() != null && !r.getPunchDetails().contains("(无打卡记录)"))
            );

            if (!hasValidRecord) {
                continue;
            }

            Object[] rowData = new Object[33];
            rowData[0] = emp.getName();

            double empTotalHours = 0.0;

            for (AttendanceRecord r : myRecords) {
                int day = r.getDayOfMonth();
                if (day >= 1 && day <= 31) {
                    double hours = r.getWorkHours();
                    rowData[day] = hours > 0 ? String.valueOf(hours) : "";
                    empTotalHours += hours;
                }
            }

            rowData[32] = String.format("%.1f", empTotalHours);
            grandTotalHours += empTotalHours;

            monthlyModel.addRow(rowData);
        }

        if (grandTotalLabel != null) {
            grandTotalLabel.setText(String.format("当月所有员工出勤总时长: %.1f h", grandTotalHours));
        }

        System.out.println("已刷新 " + year + "年" + month + "月 的考勤数据");
    }

    private JPanel createEmployeeView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG_MAIN);

        String[] cols = {"姓名", "职位", "联系电话", "身份证号"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override 
            public boolean isCellEditable(int row, int col) { 
                return col > 0;
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int col) {
                String newValue = aValue.toString().trim();
                Employee emp = DataManager.getInstance().getEmployees().get(row);
                
                try {
                    switch (col) {
                        case 1: emp.setPosition(newValue); break;
                        case 2: emp.setPhone(newValue); break;
                        case 3: emp.setIdCard(newValue); break;
                    }
                    DataManager.getInstance().updateEmployee(row, emp);
                    super.setValueAt(aValue, row, col);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "输入格式错误！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        JTable table = new JTable(model);
        employeeTable = table;
        employeeTable.setRowHeight(30);
        employeeTable.setFont(UIUtils.FONT_NORMAL);
        employeeTable.getTableHeader().setBackground(UIUtils.COLOR_BG_CONTROL);

        refreshEmp = () -> {
            model.setRowCount(0);
            for(Employee e : DataManager.getInstance().getEmployees()) {
                model.addRow(new Object[]{
                    e.getName(),
                    e.getPosition(),
                    "-".equals(e.getPhone()) ? "" : e.getPhone(),
                    "-".equals(e.getIdCard()) ? "" : e.getIdCard()
                });
            }
        };
        refreshEmp.run();

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(UIUtils.COLOR_BG_CONTROL);

        JButton addBtn = new JButton("添加员工");
        addBtn.addActionListener(e -> {
            EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            Employee newEmp = dialog.getData();

            if(newEmp != null) {
                DataManager.getInstance().addEmployee(newEmp);
                refreshEmp.run();
                refreshMonthlyTable();
            }
        });

        JButton delBtn = new JButton("删除员工");
        delBtn.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row >= 0) {
                if(JOptionPane.showConfirmDialog(panel, "确定删除该员工吗？") == JOptionPane.YES_OPTION) {
                    DataManager.getInstance().removeEmployee(row);
                    refreshEmp.run();
                    refreshMonthlyTable();
                }
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一名员工");
            }
        });

        JButton printBtn = new JButton("打印全部员工信息");
        printBtn.setForeground(new java.awt.Color(0, 102, 204));
        printBtn.addActionListener(e -> printAllEmployeesInfo());

        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        btnPanel.add(printBtn);

        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void printAllEmployeesInfo() {
        List<Employee> employees = DataManager.getInstance().getEmployees();
        
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有员工信息可打印！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        generateEmployeeTablePdf(employees);
    }

    private void generateEmployeeTablePdf(List<Employee> employees) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("全部员工信息");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"序号", "姓名", "电话", "身份证"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                Row dataRow = sheet.createRow(i + 1);
                
                Cell indexCell = dataRow.createCell(0);
                indexCell.setCellValue(i + 1);
                
                Cell nameCell = dataRow.createCell(1);
                nameCell.setCellValue(emp.getName());
                
                Cell phoneCell = dataRow.createCell(2);
                phoneCell.setCellValue(emp.getPhone());
                
                Cell idCardCell = dataRow.createCell(3);
                idCardCell.setCellValue(emp.getIdCard());
                
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setAlignment(HorizontalAlignment.LEFT);
                dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                
                indexCell.setCellStyle(dataStyle);
                nameCell.setCellStyle(dataStyle);
                phoneCell.setCellStyle(dataStyle);
                idCardCell.setCellStyle(dataStyle);
            }

            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 6000);
            sheet.setColumnWidth(2, 6000);
            sheet.setColumnWidth(3, 9000);

            String fileName = "全部员工信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            if (Desktop.isDesktopSupported()) {
                File excelFile = new File(fileName);
                Desktop.getDesktop().open(excelFile);
            } else {
                JOptionPane.showMessageDialog(null, "Excel已生成，但系统不支持自动打开。\n文件路径: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Excel生成失败: " + e.getMessage());
        }
    }
}
