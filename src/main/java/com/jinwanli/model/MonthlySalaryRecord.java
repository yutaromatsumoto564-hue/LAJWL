package com.jinwanli.model;

import java.io.Serializable;
import java.util.Objects;

public class MonthlySalaryRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String employeeId;
    private String employeeName;
    private String employeePosition;
    private String month;
    private double baseSalary;
    private double performanceSalary;
    private double overtimeSalary;
    private double totalSalary;
    private String status;

    public MonthlySalaryRecord(String id, String employeeId, String employeeName, String employeePosition, 
                           String month, double baseSalary, double performanceSalary, double overtimeSalary) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeePosition = employeePosition;
        this.month = month;
        this.baseSalary = baseSalary;
        this.performanceSalary = performanceSalary;
        this.overtimeSalary = overtimeSalary;
        this.totalSalary = baseSalary + performanceSalary + overtimeSalary;
        this.status = "未发放";
    }

    public String getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeePosition() { return employeePosition; }
    public String getMonth() { return month; }
    public double getBaseSalary() { return baseSalary; }
    public double getPerformanceSalary() { return performanceSalary; }
    public double getOvertimeSalary() { return overtimeSalary; }
    public double getTotalSalary() { return totalSalary; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setEmployeePosition(String employeePosition) { this.employeePosition = employeePosition; }
    public void setMonth(String month) { this.month = month; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public void setPerformanceSalary(double performanceSalary) { this.performanceSalary = performanceSalary; }
    public void setOvertimeSalary(double overtimeSalary) { this.overtimeSalary = overtimeSalary; }
    public void setTotalSalary(double totalSalary) { this.totalSalary = totalSalary; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return employeeName + " - " + month; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlySalaryRecord record = (MonthlySalaryRecord) o;
        return Objects.equals(id, record.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
