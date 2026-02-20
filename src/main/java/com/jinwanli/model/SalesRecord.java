package com.jinwanli.model;

import java.io.Serializable;

public class SalesRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String shipperName;
    private String productName;
    private String handler;
    private int basketCount;
    private double weightPerBasket;
    private double pricePerJin;
    private String date;

    public SalesRecord(String shipperName, int basketCount, double weightPerBasket, double pricePerJin, String date) {
        this.shipperName = shipperName;
        this.basketCount = basketCount;
        this.weightPerBasket = weightPerBasket;
        this.pricePerJin = pricePerJin;
        this.date = date;
    }

    public SalesRecord(String date, String customer, String product, double price, int qty, double total, String handler) {
        this.shipperName = customer;
        this.productName = product;
        this.handler = handler;
        this.basketCount = qty;
        this.weightPerBasket = 1.0;
        this.pricePerJin = price;
        this.date = date;
    }

    public double getNetWeight() { 
        return basketCount * weightPerBasket; 
    }
    
    public double getTotalAmount() { 
        return getNetWeight() * pricePerJin; 
    }

    public String getShipperName() { return shipperName; }
    public String getProductName() { return productName; }
    public String getHandler() { return handler; }
    public int getBasketCount() { return basketCount; }
    public double getWeightPerBasket() { return weightPerBasket; }
    public double getPricePerJin() { return pricePerJin; }
    public String getDate() { return date; }

    public void setShipperName(String shipperName) { this.shipperName = shipperName; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setHandler(String handler) { this.handler = handler; }
    public void setBasketCount(int basketCount) { this.basketCount = basketCount; }
    public void setWeightPerBasket(double weightPerBasket) { this.weightPerBasket = weightPerBasket; }
    public void setPricePerJin(double pricePerJin) { this.pricePerJin = pricePerJin; }
    public void setDate(String date) { this.date = date; }
}