package com.jinwanli.util;

import com.jinwanli.DataManager;
import com.jinwanli.model.AttendanceRecord;
import com.jinwanli.model.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * 考勤数据导入工具
 * 支持从 Excel 文件导入月度考勤数据
 */
public class AttendanceImporter {
    
    /**
     * 月度考勤汇总记录
     */
    public static class MonthlyAttendance implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String employeeId;
        private String employeeName;
        private String month; // 格式: yyyy-MM
        private int requiredDays;      // 应出勤天数
        private double actualDays;    // 实际出勤天数
        private double requiredHours;  // 应出勤(小时)
        private double actualHours;   // 实际出勤(小时)
        private double paidHours;     // 计薪时长
        private double overtimePay;   // 加班费时长
        private double overtimeOff;   // 调休时长
        
        public MonthlyAttendance() {}
        
        public MonthlyAttendance(String employeeId, String employeeName, String month,
                                 int requiredDays, double actualDays, double requiredHours,
                                 double actualHours, double paidHours, double overtimePay, double overtimeOff) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.month = month;
            this.requiredDays = requiredDays;
            this.actualDays = actualDays;
            this.requiredHours = requiredHours;
            this.actualHours = actualHours;
            this.paidHours = paidHours;
            this.overtimePay = overtimePay;
            this.overtimeOff = overtimeOff;
        }
        
        // Getters
        public String getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getMonth() { return month; }
        public int getRequiredDays() { return requiredDays; }
        public double getActualDays() { return actualDays; }
        public double getRequiredHours() { return requiredHours; }
        public double getActualHours() { return actualHours; }
        public double getPaidHours() { return paidHours; }
        public double getOvertimePay() { return overtimePay; }
        public double getOvertimeOff() { return overtimeOff; }
        
        // Setters
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public void setMonth(String month) { this.month = month; }
        public void setRequiredDays(int requiredDays) { this.requiredDays = requiredDays; }
        public void setActualDays(double actualDays) { this.actualDays = actualDays; }
        public void setRequiredHours(double requiredHours) { this.requiredHours = requiredHours; }
        public void setActualHours(double actualHours) { this.actualHours = actualHours; }
        public void setPaidHours(double paidHours) { this.paidHours = paidHours; }
        public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }
        public void setOvertimeOff(double overtimeOff) { this.overtimeOff = overtimeOff; }
    }
    
    /**
     * 从 Excel 文件导入月度考勤数据
     * @param filePath Excel 文件路径
     * @return 月度考勤记录列表
     */
    public static List<MonthlyAttendance> importFromExcel(String filePath) throws IOException {
        List<MonthlyAttendance> records = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            String month = extractMonthFromFileName(filePath);
            
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                Cell nameCell = row.getCell(0);
                if (nameCell == null) continue;
                
                String name = getCellValue(nameCell).trim();
                if (name.isEmpty() || name.equals("姓名") || name.equals("基本信息")) {
                    continue;
                }
                
                String empId = getCellValue(row.getCell(1));
                
                double actualHours = getCellDoubleValue(row, 6);
                double paidHours = getCellDoubleValue(row, 7); 
                
                MonthlyAttendance record = new MonthlyAttendance(
                    empId.isEmpty() || empId.equals("-") ? null : empId,
                    name, 
                    month, 
                    0, 0, 0, 
                    actualHours, 
                    paidHours, 
                    0, 0
                );
                
                boolean exists = records.stream().anyMatch(r -> r.getEmployeeName().equals(name));
                if (!exists) {
                    records.add(record);
                }
            }
        } catch (Exception e) {
            throw new IOException("读取文件失败，请确保导入的是 .xlsx 格式的 Excel 文件！", e);
        }
        
        return records;
    }
    
    /**
     * 从文件名提取月份
     */
    private static String extractMonthFromFileName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        // 格式: 月度汇总表_20260117_20260216.xlsx
        try {
            if (fileName.contains("_")) {
                String[] parts = fileName.split("_");
                if (parts.length >= 2) {
                    String startDate = parts[1];
                    String year = startDate.substring(0, 4);
                    String month = startDate.substring(4, 6);
                    return year + "-" + month;
                }
            }
        } catch (Exception e) {
            // 使用当前月份
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return cal.get(java.util.Calendar.YEAR) + "-" + 
               String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
    }
    
    /**
     * 获取单元格字符串值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: 
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                long numVal = (long) cell.getNumericCellValue();
                return String.valueOf(numVal);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default: return "";
        }
    }
    
    /**
     * 获取单元格整数值
     */
    private static int getCellIntValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC: return (int) cell.getNumericCellValue();
            case STRING: 
                try { return Integer.parseInt(cell.getStringCellValue().trim()); }
                catch (Exception e) { return 0; }
            default: return 0;
        }
    }
    
    /**
     * 获取单元格双精度值
     */
    private static double getCellDoubleValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return 0.0;
        
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING: 
                try { return Double.parseDouble(cell.getStringCellValue().trim()); }
                catch (Exception e) { return 0.0; }
            default: return 0.0;
        }
    }
    
    /**
     * 获取单元格双精度值（从Cell对象）
     */
    private static double getCellDoubleValue(Cell cell) {
        if (cell == null) return 0.0;
        
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING: 
                try { return Double.parseDouble(cell.getStringCellValue().trim()); }
                catch (Exception e) { return 0.0; }
            default: return 0.0;
        }
    }
    
    /**
     * 根据姓名匹配员工ID
     */
    public static String matchEmployeeId(String name, List<Employee> employees) {
        // 先精确匹配
        for (Employee emp : employees) {
            if (emp.getName().equals(name)) {
                return emp.getId();
            }
        }
        
        // 模糊匹配（包含关系）
        for (Employee emp : employees) {
            if (emp.getName().contains(name) || name.contains(emp.getName())) {
                return emp.getId();
            }
        }
        
        return null;
    }
    
    /**
     * 根据工号匹配员工ID
     */
    public static String matchEmployeeIdByCode(String code, List<Employee> employees) {
        if (code == null || code.isEmpty() || code.equals("-")) return null;
        
        for (Employee emp : employees) {
            if (emp.getId().equals(code)) {
                return emp.getId();
            }
        }
        return null;
    }
    
    /**
     * 导入结果
     */
    public static class ImportResult {
        public int total;        // 总记录数
        public int success;      // 成功导入数
        public int matched;     // 匹配到员工数
        public int failed;      // 失败数
        public List<String> errors; // 错误信息
        
        public ImportResult() {
            this.errors = new ArrayList<>();
        }
    }

    public static void importDailyAttendance(String filePath, String yearStr, String monthStr) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<Employee> employees = DataManager.getInstance().getEmployees();
            
            int headerRowIdx = -1;
            for (int r = 0; r <= 5; r++) { 
                Row row = sheet.getRow(r);
                if (row != null && "姓名".equals(getCellValue(row.getCell(0)).trim())) {
                    headerRowIdx = r;
                    break;
                }
            }
            if (headerRowIdx == -1) throw new Exception("未能识别表头，请确保表格第一列包含姓名！");

            for (int rowNum = headerRowIdx + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                String name = getCellValue(row.getCell(0)).trim();
                if (name.isEmpty() || name.equals("姓名") || name.equals("基本信息")) continue;
                
                String dateRaw = getCellValue(row.getCell(3)).trim();
                if (dateRaw.length() < 8) continue;
                
                String formattedDate = dateRaw;
                if (formattedDate.endsWith(".0")) formattedDate = formattedDate.replace(".0", "");
                if (formattedDate.length() == 8 && !formattedDate.contains("-")) {
                    formattedDate = formattedDate.substring(0, 4) + "-" + 
                                    formattedDate.substring(4, 6) + "-" + 
                                    formattedDate.substring(6, 8);
                }
                
                java.util.List<Integer> punches = new java.util.ArrayList<>();
                StringBuilder punchTextBuilder = new StringBuilder();
                
                for (int c = 4; c <= 9; c++) {
                    Cell timeCell = row.getCell(c);
                    if (timeCell != null) {
                        String timeVal = getCellValue(timeCell).trim();
                        if (timeVal.matches("^\\d{2}:\\d{2}$")) {
                            punchTextBuilder.append(timeVal).append("  ");
                            String[] parts = timeVal.split(":");
                            int minutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
                            punches.add(minutes);
                        }
                    }
                }
                
                String status = "";
                Cell statusCell = row.getCell(10);
                if (statusCell != null) status = getCellValue(statusCell).trim();
                
                boolean isAbnormal = !status.isEmpty() && !status.equals("正常") && !status.equals("休息");
                String punchDetails = status + (punches.isEmpty() ? " (无打卡记录)" : " | 实际打卡: " + punchTextBuilder.toString().trim());
                
                double hours = 0.0;
                if (punches.size() >= 2) {
                    int min = java.util.Collections.min(punches);
                    int max = java.util.Collections.max(punches);
                    hours = Math.round(((max - min) / 60.0) * 10.0) / 10.0;
                }
                
                if (hours > 0 || isAbnormal) {
                    String empId = matchEmployeeId(name, employees);
                    
                    if (empId == null) {
                        empId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                        Employee newEmp = new Employee(empId, name, "员工", "-", "-", 0.0, 0.0, 0.0);
                        
                        com.jinwanli.DataManager.getInstance().addEmployee(newEmp);
                    }
                    
                    AttendanceRecord record = new AttendanceRecord(empId, formattedDate, hours, isAbnormal, punchDetails);
                    DataManager.getInstance().addAttendanceRecord(record);
                }
            }
        }
    }
}
