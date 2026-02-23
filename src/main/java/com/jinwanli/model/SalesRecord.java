package com.jinwanli.model;
import java.io.Serializable;

public class SalesRecord implements Serializable {
    private static final long serialVersionUID = 3L;
    
    private String date;
    private String shipperName;
    private double unitPrice;
    private double weightPerBasket;
    private int basketCount;
    private double totalWeight;
    private double totalAmount;
    private String handler;

    public SalesRecord(String date, String shipperName, double unitPrice, double weightPerBasket, int basketCount, double totalWeight, double totalAmount, String handler) {
        this.date = date;
        this.shipperName = shipperName;
        this.unitPrice = unitPrice;
        this.weightPerBasket = weightPerBasket;
        this.basketCount = basketCount;
        this.totalWeight = totalWeight;
        this.totalAmount = totalAmount;
        this.handler = handler;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getShipperName() { return shipperName; }
    public void setShipperName(String shipperName) { this.shipperName = shipperName; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getWeightPerBasket() { return weightPerBasket; }
    public void setWeightPerBasket(double weightPerBasket) { this.weightPerBasket = weightPerBasket; }
    
    public int getBasketCount() { return basketCount; }
    public void setBasketCount(int basketCount) { this.basketCount = basketCount; }
    
    public double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(double totalWeight) { this.totalWeight = totalWeight; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getHandler() { return handler; }
    public void setHandler(String handler) { this.handler = handler; }
}
