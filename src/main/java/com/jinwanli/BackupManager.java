package com.jinwanli;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jinwanli.model.*;

public class BackupManager {
    public static void performBackup() {
        try {
            File backupDir = new File("backup");
            if (!backupDir.exists()) backupDir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM").format(new Date());
            String fileName = "backup/金万里月度数据备份_" + timestamp + ".xlsx";

            Workbook workbook = new XSSFWorkbook();

            Sheet salesSheet = workbook.createSheet("销售记录");
            Row salesHeader = salesSheet.createRow(0);
            salesHeader.createCell(0).setCellValue("日期");
            salesHeader.createCell(1).setCellValue("客户");
            salesHeader.createCell(2).setCellValue("总金额");
            List<SalesRecord> sales = DataManager.getInstance().getSalesRecords();
            int r = 1;
            for (SalesRecord record : sales) {
                Row row = salesSheet.createRow(r++);
                row.createCell(0).setCellValue(record.getDate());
                row.createCell(1).setCellValue(record.getShipperName());
                row.createCell(2).setCellValue(record.getTotalAmount());
            }

            Sheet expenseSheet = workbook.createSheet("开支与投资记录");
            Row expenseHeader = expenseSheet.createRow(0);
            expenseHeader.createCell(0).setCellValue("日期");
            expenseHeader.createCell(1).setCellValue("分类");
            expenseHeader.createCell(2).setCellValue("关联项目");
            expenseHeader.createCell(3).setCellValue("金额");
            expenseHeader.createCell(4).setCellValue("用途");
            expenseHeader.createCell(5).setCellValue("经手人");
            List<ExpenseRecord> expenses = DataManager.getInstance().getExpenseRecords();
            r = 1;
            for (ExpenseRecord record : expenses) {
                Row row = expenseSheet.createRow(r++);
                row.createCell(0).setCellValue(record.getDate());
                row.createCell(1).setCellValue(record.getCategory());
                row.createCell(2).setCellValue(record.getTargetProject() != null ? record.getTargetProject() : "-");
                row.createCell(3).setCellValue(record.getAmount());
                row.createCell(4).setCellValue(record.getUsage());
                row.createCell(5).setCellValue(record.getHandler());
            }

            Sheet attendanceSheet = workbook.createSheet("考勤数据(小时)");
            Row attendanceHeader = attendanceSheet.createRow(0);
            attendanceHeader.createCell(0).setCellValue("员工ID");
            attendanceHeader.createCell(1).setCellValue("员工姓名");
            attendanceHeader.createCell(2).setCellValue("日期");
            attendanceHeader.createCell(3).setCellValue("工作时长(小时)");
            List<AttendanceRecord> attendance = DataManager.getInstance().getAttendanceRecords();
            r = 1;
            for (AttendanceRecord record : attendance) {
                Row row = attendanceSheet.createRow(r++);
                row.createCell(0).setCellValue(record.getEmployeeId());
                row.createCell(1).setCellValue(record.getEmployeeName());
                row.createCell(2).setCellValue(record.getDate());
                row.createCell(3).setCellValue(record.getWorkHours());
            }

            Sheet employeeSheet = workbook.createSheet("员工信息");
            Row employeeHeader = employeeSheet.createRow(0);
            employeeHeader.createCell(0).setCellValue("工号");
            employeeHeader.createCell(1).setCellValue("姓名");
            employeeHeader.createCell(2).setCellValue("职位");
            employeeHeader.createCell(3).setCellValue("联系电话");
            employeeHeader.createCell(4).setCellValue("身份证号");
            employeeHeader.createCell(5).setCellValue("基本工资");
            employeeHeader.createCell(6).setCellValue("绩效奖金");
            employeeHeader.createCell(7).setCellValue("加班补贴");
            employeeHeader.createCell(8).setCellValue("总工资");
            List<Employee> employees = DataManager.getInstance().getEmployees();
            r = 1;
            for (Employee emp : employees) {
                Row row = employeeSheet.createRow(r++);
                row.createCell(0).setCellValue(emp.getId());
                row.createCell(1).setCellValue(emp.getName());
                row.createCell(2).setCellValue(emp.getPosition());
                row.createCell(3).setCellValue(emp.getPhone());
                row.createCell(4).setCellValue(emp.getIdCard());
                row.createCell(5).setCellValue(emp.getBaseSalary());
                row.createCell(6).setCellValue(emp.getPerformanceSalary());
                row.createCell(7).setCellValue(emp.getOvertimeSalary());
                row.createCell(8).setCellValue(emp.getTotalSalary());
            }

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                workbook.write(out);
            }
            workbook.close();

            javax.swing.JOptionPane.showMessageDialog(null, "成功生成报表：" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "备份导出失败：" + e.getMessage());
        }
    }
}
