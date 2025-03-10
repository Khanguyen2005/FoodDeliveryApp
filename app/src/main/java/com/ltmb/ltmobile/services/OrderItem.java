package com.ltmb.ltmobile.services;

public class OrderItem {
    private String foodId;
    private int quantity;
    private double price;

    public OrderItem() { } // Constructor mặc định Firebase yêu cầu

    public OrderItem(String foodId, int quantity, double price) {
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
