package com.jinwanli.model;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String position;
    private String phone;
    private String idCard;
    private double baseSalary;
    private double performanceSalary;
    private double overtimeSalary;
    private String status;

    public Employee(String id, String name, String position, String phone, String idCard, double baseSalary, double performanceSalary, double overtimeSalary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.phone = phone;
        this.idCard = idCard;
        this.baseSalary = baseSalary;
        this.performanceSalary = performanceSalary;
        this.overtimeSalary = overtimeSalary;
        this.status = "未发放";
    }

    public double getTotalSalary() { 
        return baseSalary + performanceSalary + overtimeSalary; 
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getPhone() { return phone; }
    public String getIdCard() { return idCard; }
    public double getBaseSalary() { return baseSalary; }
    public double getPerformanceSalary() { return performanceSalary; }
    public double getOvertimeSalary() { return overtimeSalary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}