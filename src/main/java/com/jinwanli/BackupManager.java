package com.jinwanli;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.jinwanli.model.*;

public class BackupManager {

    /**
     * 执行备份并生成高度还原模板的考勤表和账目表
     */
    public static void performBackup(String year, String month) {
        try {
            // 准备一个默认的系统备份目录供参考
            File defaultDir = new File(System.getProperty("user.dir"), "backup");
            if (!defaultDir.exists()) defaultDir.mkdirs();

            // 【全新升级】：弹出窗口，让用户自己选择要把 Excel 保存在哪里！
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("请选择报表导出的保存位置");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 只能选文件夹
            chooser.setCurrentDirectory(defaultDir);

            // 如果用户点击了"保存/确定"
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File targetDir = chooser.getSelectedFile();

                // 【全新升级】：加入时间戳，每次生成都是独立的新文件，绝不覆盖，绝不锁死！
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                String attFileName = new File(targetDir, "金万里_" + year + "年" + month + "月_员工考勤表_" + timestamp + ".xlsx").getAbsolutePath();
                String accFileName = new File(targetDir, "金万里_" + year + "年" + month + "月_账目_" + timestamp + ".xlsx").getAbsolutePath();

                // 执行生成逻辑
                exportAttendance(attFileName, year, month);
                exportAccount(accFileName, year, month);

                JOptionPane.showMessageDialog(null, 
                    "报表导出成功！\n\n已保存至目录：\n" + targetDir.getAbsolutePath() + 
                    "\n\n生成的文件：\n1. " + new File(attFileName).getName() + 
                    "\n2. " + new File(accFileName).getName(),
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "备份导出失败：\n" + e.getMessage() + "\n\n(提示：请确保选择的保存路径具有写入权限)", "导出错误", JOptionPane.ERROR_MESSAGE);
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

        Row headerRow = sheet.createRow(0);
        for (int c = 0; c <= 4; c++) {
            headerRow.createCell(c).setCellStyle(headerStyle);
        }
        headerRow.getCell(0).setCellValue("（ " + month + " ）月");
        headerRow.getCell(1).setCellValue("货主");
        headerRow.getCell(2).setCellValue("出货重量(斤)");
        headerRow.getCell(3).setCellValue("单价");
        headerRow.getCell(4).setCellValue("总额");

        sheet.setColumnWidth(0, 10 * 256); 
        sheet.setColumnWidth(1, 15 * 256); 
        sheet.setColumnWidth(2, 15 * 256); 
        sheet.setColumnWidth(3, 12 * 256); 
        sheet.setColumnWidth(4, 15 * 256); 

        List<SalesRecord> monthSales = DataManager.getInstance().getSalesRecords().stream()
                .filter(s -> s.getDate().startsWith(year + "-" + month))
                .collect(Collectors.toList());

        int rowIndex = 1; 

        for (int i = 1; i <= 31; i++) {
            String targetDate = year + "-" + month + "-" + String.format("%02d", i);
            List<SalesRecord> dailySales = monthSales.stream()
                    .filter(s -> s.getDate().equals(targetDate))
                    .collect(Collectors.toList());

            if (dailySales.isEmpty()) {
                Row row = sheet.createRow(rowIndex++);
                for (int c = 0; c <= 4; c++) {
                    row.createCell(c).setCellStyle(borderStyle);
                }
                row.getCell(0).setCellValue(i);
                row.getCell(4).setCellValue(0); 
            } else {
                int firstRowOfDay = rowIndex;
                for (int j = 0; j < dailySales.size(); j++) {
                    SalesRecord s = dailySales.get(j);
                    Row row = sheet.createRow(rowIndex++);
                    
                    for (int c = 0; c <= 4; c++) {
                        row.createCell(c).setCellStyle(borderStyle);
                    }

                    if (j == 0) {
                        row.getCell(0).setCellValue(i);
                    }
                    
                    row.getCell(1).setCellValue(s.getShipperName());
                    row.getCell(2).setCellValue(s.getTotalWeight());
                    row.getCell(3).setCellValue(s.getUnitPrice());
                    row.getCell(4).setCellValue(s.getTotalAmount());
                }
                
                if (dailySales.size() > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(firstRowOfDay, rowIndex - 1, 0, 0));
                }
            }
        }

        Row totalRow = sheet.createRow(rowIndex);
        for (int c = 0; c <= 4; c++) {
            totalRow.createCell(c).setCellStyle(headerStyle); 
        }
        totalRow.getCell(0).setCellValue("总计");
        
        if (rowIndex > 1) {
            totalRow.getCell(2).setCellFormula("SUM(C2:C" + rowIndex + ")");
            totalRow.getCell(4).setCellFormula("SUM(E2:E" + rowIndex + ")");
        }

        sheet.setForceFormulaRecalculation(true);

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            wb.write(out);
        }
        wb.close();
    }
}
