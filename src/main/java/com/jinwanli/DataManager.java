package com.jinwanli;

import com.jinwanli.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<Employee> employees;
    private List<SalesRecord> salesRecords;
    private List<ExpenseRecord> expenseRecords;
    private List<AttendanceRecord> attendanceRecords;

    private static final String DATA_DIR = "data";

    private DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        employees = loadList("employees.dat");
        salesRecords = loadList("sales.dat");
        expenseRecords = loadList("expenses.dat");
        attendanceRecords = loadList("attendance.dat");

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

    public List<Employee> getEmployees() { return employees; }
    public void addEmployee(Employee e) { employees.add(e); saveEmployees(); }
    public void removeEmployee(int index) { 
        if(index >= 0 && index < employees.size()) { 
            employees.remove(index); 
            saveEmployees(); 
        } 
    }
    private void saveEmployees() { saveList(employees, "employees.dat"); }
    public Employee getEmployeeById(String id) {
        return employees.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    public List<SalesRecord> getSalesRecords() { return salesRecords; }
    public void addSalesRecord(SalesRecord s) { salesRecords.add(s); saveSales(); }
    public void removeSalesRecord(int index) { 
        if(index >= 0 && index < salesRecords.size()) { 
            salesRecords.remove(index); 
            saveSales(); 
        } 
    }
    private void saveSales() { saveList(salesRecords, "sales.dat"); }

    public List<ExpenseRecord> getExpenseRecords() { return expenseRecords; }
    public void addExpenseRecord(ExpenseRecord e) { expenseRecords.add(e); saveExpenses(); }
    public void removeExpenseRecord(int index) { 
        if(index >= 0 && index < expenseRecords.size()) { 
            expenseRecords.remove(index); 
            saveExpenses(); 
        } 
    }
    private void saveExpenses() { saveList(expenseRecords, "expenses.dat"); }

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