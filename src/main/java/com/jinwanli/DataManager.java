package com.jinwanli;

import com.jinwanli.model.*;
import java.io.*;
import java.util.*;

public class DataManager {
    private static DataManager instance;

    private static final String DATA_DIR = "data";

    private List<Employee> employees;
    private List<SalesRecord> salesRecords;
    private List<ExpenseRecord> expenseRecords;
    private List<AttendanceRecord> attendanceRecords;
    private List<MonthlySalaryRecord> monthlySalaryRecords;
    
    private Map<String, List<AttendanceRecord>> monthlyAttendance;

    private DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        employees = loadList("employees.dat");
        salesRecords = loadList("sales.dat");
        expenseRecords = loadList("expenses.dat");
        attendanceRecords = loadList("attendance.dat");
        monthlySalaryRecords = loadList("monthly_salaries.dat");
        monthlyAttendance = loadMonthlyAttendance();

        Set<String> seenEmpNames = new HashSet<>();
        boolean cleanEmp = employees.removeIf(e -> !seenEmpNames.add(e.getName()));
        if (cleanEmp) saveEmployees(); 

        Set<String> seenAtt = new HashSet<>();
        boolean cleanAtt = attendanceRecords.removeIf(r -> !seenAtt.add(r.getEmployeeId() + "_" + r.getDate()));
        if (cleanAtt) saveAttendance();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // ========== 数据保存通用方法 ==========
    private <T> void saveList(List<T> list, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "/" + filename))) {
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(String filename) {
        File file = new File(DATA_DIR + "/" + filename);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (List<T>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    // ========== 员工管理 ==========
    public List<Employee> getEmployees() { return employees; }
    
    public Employee getEmployeeByName(String name) {
        for (Employee emp : employees) {
            if (emp.getName().equals(name)) return emp;
        }
        return null;
    }
    
    public void addEmployee(Employee emp) {
        employees.add(emp);
        saveEmployees();
    }
    
    public void updateEmployee(int index, Employee emp) {
        if(index >= 0 && index < employees.size()) {
            employees.set(index, emp);
            saveEmployees();
        }
    }
    
    public void saveEmployees() { saveList(employees, "employees.dat"); }

    public Employee getEmployeeById(String id) {
        for (Employee emp : employees) {
            if (emp.getId().equals(id)) return emp;
        }
        return null;
    }

    public void removeEmployee(int index) {
        if(index >= 0 && index < employees.size()) {
            employees.remove(index);
            saveEmployees();
        }
    }

    // ========== 考勤管理 ==========
    public List<AttendanceRecord> getAttendanceRecords() { return attendanceRecords; }
    
    public void addAttendanceRecord(AttendanceRecord record) {
        attendanceRecords.add(record);
        
        String yyyyMM = record.getDate().substring(0, 7); 
        monthlyAttendance.computeIfAbsent(yyyyMM, k -> new ArrayList<>()).add(record);
        
        saveAttendance();
        saveMonthlyAttendance();
    }
    
    public List<AttendanceRecord> getAttendanceByMonth(String year, String month) {
        String key = year + "-" + month;
        return monthlyAttendance.getOrDefault(key, new ArrayList<>());
    }
    
    public void saveAttendance() { saveList(attendanceRecords, "attendance.dat"); }

    private void saveMonthlyAttendance() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "/monthly_attendance.dat"))) {
            oos.writeObject(monthlyAttendance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, List<AttendanceRecord>> loadMonthlyAttendance() {
        File file = new File(DATA_DIR + "/monthly_attendance.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (Map<String, List<AttendanceRecord>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    // ========== 销售管理 ==========
    public List<SalesRecord> getSalesRecords() { return salesRecords; }
    
    public void addSalesRecord(SalesRecord record) {
        salesRecords.add(record);
        saveSales();
    }
    
    public void updateSalesRecord(int index, SalesRecord s) {
        if(index >= 0 && index < salesRecords.size()) {
            salesRecords.set(index, s);
            saveSales();
        }
    }
    
    public void saveSales() { saveList(salesRecords, "sales.dat"); }

    // ========== 开支/财务管理 ==========
    public List<ExpenseRecord> getExpenseRecords() { return expenseRecords; }
    
    public void addExpenseRecord(ExpenseRecord record) {
        expenseRecords.add(record);
        saveExpenses();
    }
    
    public void updateExpenseRecord(int index, ExpenseRecord record) {
        if (index >= 0 && index < expenseRecords.size()) {
            expenseRecords.set(index, record);
            saveExpenses();
        }
    }
    
    public void saveExpenses() { saveList(expenseRecords, "expenses.dat"); }

    // ========== 月度工资管理 ==========
    public List<MonthlySalaryRecord> getMonthlySalaryRecords() { return monthlySalaryRecords; }
    
    public void addMonthlySalaryRecord(MonthlySalaryRecord record) {
        monthlySalaryRecords.add(record);
        saveMonthlySalaries();
    }
    
    public void updateMonthlySalaryRecord(int index, MonthlySalaryRecord record) {
        if (index >= 0 && index < monthlySalaryRecords.size()) {
            monthlySalaryRecords.set(index, record);
            saveMonthlySalaries();
        }
    }
    
    public void deleteMonthlySalaryRecord(int index) {
        if (index >= 0 && index < monthlySalaryRecords.size()) {
            monthlySalaryRecords.remove(index);
            saveMonthlySalaries();
        }
    }
    
    public void saveMonthlySalaries() { saveList(monthlySalaryRecords, "monthly_salaries.dat"); }
}
