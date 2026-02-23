package com.jinwanli;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.jinwanli.model.*;

public class BackupManager {

    /**
     * 按指定年份和月份执行备份，生成高度还原模板的考勤表和账目表
     */
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

    // =========================================================================
    // 生成考勤表 (全包围边框 + 横向/纵向双维度公式总计)
    // =========================================================================
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

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setAlignment(HorizontalAlignment.CENTER);
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.cloneStyleFrom(borderStyle);
        Font boldFont = wb.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);
        headerStyle.setWrapText(true);
        
        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(30);
        Cell titleCell = row0.createCell(0);
        titleCell.setCellValue("金 万 里 农 业 员 工 考 勤 表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 33));

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("（ " + month + " ） 月");
        sheet.createRow(2); 

        Row row3 = sheet.createRow(3);
        row3.setHeightInPoints(25);
        Row row4 = sheet.createRow(4);

        for (int c = 0; c <= 33; c++) {
            row3.createCell(c).setCellStyle(headerStyle);
            row4.createCell(c).setCellStyle(headerStyle);
        }

        row3.getCell(0).setCellValue("序号");
        row3.getCell(1).setCellValue("姓 名");
        row3.getCell(2).setCellValue("出   勤   情   况");
        row3.getCell(33).setCellValue("本月\n出勤");

        sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 32)); 
        sheet.addMergedRegion(new CellRangeAddress(3, 4, 0, 0));  
        sheet.addMergedRegion(new CellRangeAddress(3, 4, 1, 1));  
        sheet.addMergedRegion(new CellRangeAddress(3, 4, 33, 33));

        for (int i = 1; i <= 31; i++) {
            row4.getCell(i + 1).setCellValue(i);
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
            for (int c = 0; c <= 33; c++) {
                dataRow.createCell(c).setCellStyle(borderStyle);
            }

            dataRow.getCell(0).setCellValue(seq++);
            dataRow.getCell(1).setCellValue(emp.getName());

            for (AttendanceRecord record : myRecords) {
                int day = record.getDayOfMonth();
                if (day >= 1 && day <= 31) {
                    double hours = record.getWorkHours();
                    if (hours > 0) {
                        dataRow.getCell(day + 1).setCellValue(hours);
                    }
                }
            }
            
            int excelRowIndex = rowIndex; 
            dataRow.getCell(33).setCellFormula("SUM(C" + excelRowIndex + ":AG" + excelRowIndex + ")");
        }

        Row totalRow = sheet.createRow(rowIndex);
        for (int c = 0; c <= 33; c++) {
            totalRow.createCell(c).setCellStyle(headerStyle); 
        }
        totalRow.getCell(1).setCellValue("总计");

        if (rowIndex > 5) {
            for (int c = 2; c <= 33; c++) {
                String colLetter = CellReference.convertNumToColString(c); 
                totalRow.getCell(c).setCellFormula("SUM(" + colLetter + "6:" + colLetter + rowIndex + ")");
            }
        }

        sheet.setForceFormulaRecalculation(true);

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            wb.write(out);
        }
        wb.close();
    }

    // =========================================================================
    // 生成账目表 (独立分行展示 + 日期自动合并 + 动态公式求和)
    // =========================================================================
    private static void exportAccount(String fileName, String year, String month) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("账目");

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setAlignment(HorizontalAlignment.CENTER);
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.cloneStyleFrom(borderStyle);
        Font boldFont = wb.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);

        // 【修改1】增加"货主"列，由之前的 4 列扩展为 5 列 (索引0~4)
        Row headerRow = sheet.createRow(0);
        for (int c = 0; c <= 4; c++) {
            headerRow.createCell(c).setCellStyle(headerStyle);
        }
        headerRow.getCell(0).setCellValue("（ " + month + " ）月");
        headerRow.getCell(1).setCellValue("货主");
        headerRow.getCell(2).setCellValue("出货重量");
        headerRow.getCell(3).setCellValue("单价");
        headerRow.getCell(4).setCellValue("总额");

        sheet.setColumnWidth(0, 10 * 256); // 日期
        sheet.setColumnWidth(1, 15 * 256); // 货主
        sheet.setColumnWidth(2, 15 * 256); // 重量
        sheet.setColumnWidth(3, 12 * 256); // 单价
        sheet.setColumnWidth(4, 15 * 256); // 总额

        List<SalesRecord> monthSales = DataManager.getInstance().getSalesRecords().stream()
                .filter(s -> s.getDate().startsWith(year + "-" + month))
                .collect(Collectors.toList());

        int rowIndex = 1; // 数据从第2行（索引1）开始

        // 1 到 31 号的每日数据
        for (int i = 1; i <= 31; i++) {
            String targetDate = year + "-" + month + "-" + String.format("%02d", i);
            List<SalesRecord> dailySales = monthSales.stream()
                    .filter(s -> s.getDate().equals(targetDate))
                    .collect(Collectors.toList());

            if (dailySales.isEmpty()) {
                // 当天无数据，生成一行空记录
                Row row = sheet.createRow(rowIndex++);
                for (int c = 0; c <= 4; c++) {
                    row.createCell(c).setCellStyle(borderStyle);
                }
                row.getCell(0).setCellValue(i);
                row.getCell(4).setCellValue(0); // 空白天数总额填 0
            } else {
                // 【修改2】当天有多笔数据，分行遍历写入
                int firstRowOfDay = rowIndex;
                for (int j = 0; j < dailySales.size(); j++) {
                    SalesRecord s = dailySales.get(j);
                    Row row = sheet.createRow(rowIndex++);
                    
                    for (int c = 0; c <= 4; c++) {
                        row.createCell(c).setCellStyle(borderStyle);
                    }

                    // 第一行才写入日期数字
                    if (j == 0) {
                        row.getCell(0).setCellValue(i);
                    }
                    
                    // 填入真实的单笔交易数据
                    row.getCell(1).setCellValue(s.getShipperName());
                    row.getCell(2).setCellValue(s.getTotalWeight());
                    row.getCell(3).setCellValue(s.getUnitPrice());
                    row.getCell(4).setCellValue(s.getTotalAmount());
                }
                
                // 【修改3】为了排版美观，当一天有多笔订单时，把最左侧的日期格子合并
                if (dailySales.size() > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(firstRowOfDay, rowIndex - 1, 0, 0));
                }
            }
        }

        // --- 底部总计行 ---
        Row totalRow = sheet.createRow(rowIndex);
        for (int c = 0; c <= 4; c++) {
            totalRow.createCell(c).setCellStyle(headerStyle); 
        }
        totalRow.getCell(0).setCellValue("总计");
        
        // 【修改4】动态公式求和：C列(索引2)汇总重量，E列(索引4)汇总金额
        if (rowIndex > 1) {
            totalRow.getCell(2).setCellFormula("SUM(C2:C" + rowIndex + ")");
            totalRow.getCell(4).setCellFormula("SUM(E2:E" + rowIndex + ")");
        }

        // 强制 Excel 重算公式
        sheet.setForceFormulaRecalculation(true);

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            wb.write(out);
        }
        wb.close();
    }
}
