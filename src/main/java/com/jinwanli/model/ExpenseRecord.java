package com.jinwanli.model;

import java.io.Serializable;

public class ExpenseRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String date;
    private String category;
    private double amount;
    private String usage;
    private String handler;
    private String remark;

    public ExpenseRecord(String date, String category, double amount, String usage, String handler, String remark) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.usage = usage;
        this.handler = handler;
        this.remark = remark;
    }

    public String getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getUsage() { return usage; }
    public String getHandler() { return handler; }
    public String getRemark() { return remark; }
}