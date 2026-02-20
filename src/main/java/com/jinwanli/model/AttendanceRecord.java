package com.jinwanli.model;
import java.io.Serializable;

public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 2L;
    private String employeeId;
    private String date;
    private double workHours;
    
    private boolean abnormal; 
    private String punchDetails; 

    public AttendanceRecord(String employeeId, String date, double workHours) {
        this(employeeId, date, workHours, false, "");
    }

    public AttendanceRecord(String employeeId, String date, double workHours, boolean abnormal, String punchDetails) {
        this.employeeId = employeeId;
        this.date = date;
        this.workHours = workHours;
        this.abnormal = abnormal;
        this.punchDetails = punchDetails;
    }

    public String getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public double getWorkHours() { return workHours; }
    public boolean isAbnormal() { return abnormal; }
    public String getPunchDetails() { return punchDetails; }
    
    public void setAbnormal(boolean abnormal) { this.abnormal = abnormal; }
    public void setPunchDetails(String punchDetails) { this.punchDetails = punchDetails; }

    public int getDayOfMonth() {
        if (date != null && date.length() >= 10) {
            return Integer.parseInt(date.substring(8, 10));
        }
        return -1;
    }
}
