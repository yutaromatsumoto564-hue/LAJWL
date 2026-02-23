package com.jinwanli;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.jinwanli.model.*;

public class BackupManager {

    public static void performBackup(String year, String month) {
        try {
            File backupDir = new File("backup");
            if (!backupDir.exists()) backupDir.mkdirs();

            String baseName = "backup/金万里_" + year + "年" + month + "月_";
            
            exportAttendance(baseName + "员工考勤表.xlsx", year, month);
            exportAccount(baseName + "账目.xlsx", year, month);

            javax.swing.JOptionPane.showMessageDialog(null, "成功生成 " + year + "年" + month + "月 报表！\n请前往 backup 文件夹查看。");

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "备份导出失败：" + e.getMessage());
        }
    }

    private static void exportAttendance(String fileName, String year, String month) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("考勤表");

        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        
        CellStyle centerStyle = wb.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);

        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(30);
        Cell titleCell = row0.createCell(0);
        titleCell.setCellValue("金 万 里 农 业 员 工 考 勤 表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 33));

        Row row1 = sheet.createRow(1);
        Cell subtitleCell = row1.createCell(0);
        subtitleCell.setCellValue("（ " + month + " ） 月");
        
        sheet.createRow(2);

        Row row3 = sheet.createRow(3);
        row3.setHeightInPoints(25);
        Cell c0 = row3.createCell(0); c0.setCellValue("序号"); c0.setCellStyle(headerStyle);
        Cell c1 = row3.createCell(1); c1.setCellValue("姓 名"); c1.setCellStyle(headerStyle);
        Cell c2 = row3.createCell(2); c2.setCellValue("出   勤   情   况"); c2.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 32));
        Cell c33 = row3.createCell(33); c33.setCellValue("本月\n出勤"); c33.setCellStyle(headerStyle);

        Row row4 = sheet.createRow(4);
        for (int i = 1; i <= 31; i++) {
            Cell dayCell = row4.createCell(i + 1);
            dayCell.setCellValue(i);
            dayCell.setCellStyle(centerStyle);
        }

        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 10 * 256);
        for (int i = 2; i <= 32; i++) {
            sheet.setColumnWidth(i, 4 * 256);
        }
        sheet.setColumnWidth(33, 8 * 256);

        List<Employee> employees = DataManager.getInstance().getEmployees();
        List<AttendanceRecord> monthRecords = DataManager.getInstance().getAttendanceByMonth(year, month);
        
        int rowIndex = 5;
        int seq = 1;
        
        for (Employee emp : employees) {
            List<AttendanceRecord> myRecords = monthRecords.stream()
                    .filter(record -> record.getEmployeeId().equals(emp.getId()))
                    .collect(Collectors.toList());
            
            boolean hasValidRecord = myRecords.stream().anyMatch(record -> 
                record.getWorkHours() > 0 || (record.getPunchDetails() != null && !record.getPunchDetails().contains("(无打卡记录)"))
            );
            if (!hasValidRecord) continue;

            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(seq++);
            dataRow.createCell(1).setCellValue(emp.getName());

            double totalHours = 0;
            for (AttendanceRecord record : myRecords) {
                int day = record.getDayOfMonth();
                if (day >= 1 && day <= 31) {
                    double hours = record.getWorkHours();
                    if (hours > 0) {
                        dataRow.createCell(day + 1).setCellValue(hours);
                        totalHours += hours;
                    }
                }
            }
            dataRow.createCell(33).setCellValue(totalHours);
        }

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            wb.write(out);
        }
        wb.close();
    }

    private static void exportAccount(String fileName, String year, String month) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("账目");

        CellStyle centerStyle = wb.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("（ " + month + " ）月");
        headerRow.createCell(1).setCellValue("出货重量/数量");
        headerRow.createCell(2).setCellValue("单价");
        headerRow.createCell(3).setCellValue("总额");

        sheet.setColumnWidth(0, 12 * 256);
        sheet.setColumnWidth(1, 16 * 256);
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 15 * 256);

        List<SalesRecord> monthSales = DataManager.getInstance().getSalesRecords().stream()
                .filter(s -> s.getDate().startsWith(year + "-" + month))
                .collect(Collectors.toList());

        double grandTotalAmount = 0;
        int grandTotalQty = 0;

        for (int i = 1; i <= 31; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(i);
            
            String targetDate = year + "-" + month + "-" + String.format("%02d", i);
            List<SalesRecord> dailySales = monthSales.stream()
                    .filter(s -> s.getDate().equals(targetDate))
                    .collect(Collectors.toList());

            if (!dailySales.isEmpty()) {
                int dailyQty = dailySales.stream().mapToInt(SalesRecord::getBasketCount).sum();
                double dailyTotal = dailySales.stream().mapToDouble(SalesRecord::getTotalAmount).sum();
                
                row.createCell(1).setCellValue(dailyQty);
                
                if (dailyQty > 0) {
                    double avgPrice = dailyTotal / dailyQty;
                    row.createCell(2).setCellValue(String.format("%.2f", avgPrice));
                }
                
                row.createCell(3).setCellValue(dailyTotal);

                grandTotalQty += dailyQty;
                grandTotalAmount += dailyTotal;
            } else {
                row.createCell(3).setCellValue(0);
            }
        }

        Row totalRow = sheet.createRow(32);
        totalRow.createCell(0).setCellValue("总计");
        totalRow.createCell(1).setCellValue(grandTotalQty);
        totalRow.createCell(3).setCellValue(grandTotalAmount);

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            wb.write(out);
        }
        wb.close();
    }
}
