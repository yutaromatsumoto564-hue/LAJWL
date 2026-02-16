package com.jinwanli.model;

import java.io.Serializable;

public class SalesRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String shipperName;
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

    public double getNetWeight() { 
        return basketCount * weightPerBasket; 
    }
    
    public double getTotalAmount() { 
        return getNetWeight() * pricePerJin; 
    }

    public String getShipperName() { return shipperName; }
    public int getBasketCount() { return basketCount; }
    public double getWeightPerBasket() { return weightPerBasket; }
    public double getPricePerJin() { return pricePerJin; }
    public String getDate() { return date; }
}