package com.jinwanli.model;

import java.io.Serializable;

public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String employeeId;
    private String employeeName;
    private String date;
    private String status;
    private double overtimeHours;

    public AttendanceRecord(String employeeId, String employeeName, String date, String status, double overtimeHours) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.status = status;
        this.overtimeHours = overtimeHours;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public double getOvertimeHours() { return overtimeHours; }
    
    public int getDay() {
        try {
            return Integer.parseInt(date.substring(date.lastIndexOf("-") + 1));
        } catch (Exception e) { return 0; }
    }
}