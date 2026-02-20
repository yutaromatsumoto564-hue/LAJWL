package com.jinwanli;

import com.jinwanli.model.*;
import com.jinwanli.util.AttendanceImporter.MonthlyAttendance;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<Employee> employees;
    private List<SalesRecord> salesRecords;
    private List<ExpenseRecord> expenseRecords;
    private List<AttendanceRecord> attendanceRecords;
    
    // 月度考勤汇总 (key: month, value: Map<employeeId, MonthlyAttendance>)
    private Map<String, Map<String, MonthlyAttendance>> monthlyAttendance;

    private static final String DATA_DIR = "data";

    private DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        employees = loadList("employees.dat");
        salesRecords = loadList("sales.dat");
        expenseRecords = loadList("expenses.dat");
        attendanceRecords = loadList("attendance.dat");
        monthlyAttendance = loadMonthlyAttendance();

        if (employees.isEmpty()) {
            employees.add(new Employee("001", "张三", "经理", "13800138000", "110101199001011234", 8000, 2000, 500));
            employees.add(new Employee("002", "李四", "员工", "13900139000", "110101199202025678", 5000, 1000, 200));
            saveEmployees();
        }
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // ========== 员工管理 ==========
    public List<Employee> getEmployees() { return employees; }
    
    public void addEmployee(Employee e) { 
        employees.add(e); 
        saveEmployees(); 
    }
    
    public void removeEmployee(int index) { 
        if(index >= 0 && index < employees.size()) { 
            employees.remove(index); 
            saveEmployees(); 
        } 
    }
    
    public void updateEmployee(int index, Employee e) {
        if(index >= 0 && index < employees.size()) {
            employees.set(index, e);
            saveEmployees();
        }
    }
    
    private void saveEmployees() { saveList(employees, "employees.dat"); }
    
    public Employee getEmployeeById(String id) {
        return employees.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
    
    public Employee getEmployeeByName(String name) {
        return employees.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    // ========== 销售管理 ==========
    public List<SalesRecord> getSalesRecords() { return salesRecords; }
    
    public void addSalesRecord(SalesRecord s) { salesRecords.add(s); saveSales(); }
    
    public void removeSalesRecord(int index) { 
        if(index >= 0 && index < salesRecords.size()) { 
            salesRecords.remove(index); 
            saveSales(); 
        } 
    }

    public void updateSalesRecord(int index, SalesRecord s) {
        if(index >= 0 && index < salesRecords.size()) {
            salesRecords.set(index, s);
            saveSales();
        }
    }
    
    private void saveSales() { saveList(salesRecords, "sales.dat"); }

    // ========== 开支管理 ==========
    public List<ExpenseRecord> getExpenseRecords() { return expenseRecords; }
    
    public void addExpenseRecord(ExpenseRecord e) { expenseRecords.add(e); saveExpenses(); }
    
    public void removeExpenseRecord(int index) { 
        if(index >= 0 && index < expenseRecords.size()) { 
            expenseRecords.remove(index); 
            saveExpenses(); 
        } 
    }
    
    public void updateExpenseRecord(int index, ExpenseRecord e) {
        if(index >= 0 && index < expenseRecords.size()) {
            expenseRecords.set(index, e);
            saveExpenses();
        }
    }
    
    private void saveExpenses() { saveList(expenseRecords, "expenses.dat"); }

    // ========== 考勤管理 ==========
    public List<AttendanceRecord> getAttendanceRecords() { return attendanceRecords; }
    
    public void addAttendanceRecord(AttendanceRecord r) { 
        attendanceRecords.removeIf(ar -> ar.getEmployeeId().equals(r.getEmployeeId()) && ar.getDate().equals(r.getDate()));
        attendanceRecords.add(r); 
        saveAttendance(); 
    }
    
    private void saveAttendance() { saveList(attendanceRecords, "attendance.dat"); }
    
    public List<AttendanceRecord> getAttendanceByMonth(String year, String month) {
        String prefix = year + "-" + (month.length() == 1 ? "0"+month : month);
        return attendanceRecords.stream()
                .filter(r -> r.getDate().startsWith(prefix))
                .collect(Collectors.toList());
    }

    // ========== 月度考勤汇总 ==========
    
    /**
     * 保存月度考勤汇总数据
     */
    public void saveMonthlyAttendance(String month, String employeeId, MonthlyAttendance record) {
        if (monthlyAttendance == null) {
            monthlyAttendance = new HashMap<>();
        }
        
        monthlyAttendance.computeIfAbsent(month, k -> new HashMap<>());
        monthlyAttendance.get(month).put(employeeId, record);
        
        saveMonthlyAttendance();
    }
    
    /**
     * 获取指定月份的考勤汇总
     */
    public Map<String, MonthlyAttendance> getMonthlyAttendance(String month) {
        if (monthlyAttendance == null) return new HashMap<>();
        return monthlyAttendance.getOrDefault(month, new HashMap<>());
    }
    
    /**
     * 获取所有月度考勤汇总（用于显示）
     */
    public List<Object[]> getMonthlyAttendanceSummary() {
        List<Object[]> result = new ArrayList<>();
        
        if (monthlyAttendance == null) return result;
        
        for (Map.Entry<String, Map<String, MonthlyAttendance>> monthEntry : monthlyAttendance.entrySet()) {
            String month = monthEntry.getKey();
            Map<String, MonthlyAttendance> records = monthEntry.getValue();
            
            for (Map.Entry<String, MonthlyAttendance> entry : records.entrySet()) {
                String empId = entry.getKey();
                MonthlyAttendance ma = entry.getValue();
                
                // 获取员工姓名
                String empName = ma.getEmployeeName();
                Employee emp = getEmployeeById(empId);
                if (emp != null) {
                    empName = emp.getName();
                }
                
                result.add(new Object[]{
                    empName,
                    empId,
                    month,
                    ma.getRequiredDays(),
                    ma.getActualDays(),
                    ma.getRequiredHours(),
                    ma.getActualHours(),
                    ma.getPaidHours(),
                    ma.getOvertimePay(),
                    ma.getOvertimeOff()
                });
            }
        }
        
        // 按月份排序
        result.sort((a, b) -> {
            String m1 = (String) a[2];
            String m2 = (String) b[2];
            return m2.compareTo(m1); // 最新的在前
        });
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, MonthlyAttendance>> loadMonthlyAttendance() {
        String filename = DATA_DIR + File.separator + "monthly_attendance.dat";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Map<String, Map<String, MonthlyAttendance>>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    private void saveMonthlyAttendance() {
        String filename = DATA_DIR + File.separator + "monthly_attendance.dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(monthlyAttendance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== 通用方法 ==========
    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_DIR + File.separator + filename))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private <T> void saveList(List<T> list, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + File.separator + filename))) {
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
