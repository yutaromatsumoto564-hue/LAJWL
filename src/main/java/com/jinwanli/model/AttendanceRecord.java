package com.jinwanli.model;

import java.io.Serializable;

public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String employeeId;
    private String employeeName;
    private String date;
    private double workHours;

    public AttendanceRecord(String employeeId, String employeeName, String date, double workHours) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.workHours = workHours;
    }

    public AttendanceRecord(String employeeId, String date, double workHours) {
        this.employeeId = employeeId;
        this.employeeName = "";
        this.date = date;
        this.workHours = workHours;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDate() { return date; }
    public double getWorkHours() { return workHours; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setDate(String date) { this.date = date; }
    public void setWorkHours(double workHours) { this.workHours = workHours; }

    public int getDay() {
        try {
            return Integer.parseInt(date.substring(date.lastIndexOf("-") + 1));
        } catch (Exception e) { return 0; }
    }

    public int getDayOfMonth() {
        try {
            return Integer.parseInt(date.split("-")[2]);
        } catch (Exception e) { return 0; }
    }
}
