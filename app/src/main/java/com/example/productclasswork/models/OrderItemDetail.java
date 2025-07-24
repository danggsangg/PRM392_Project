package com.example.productclasswork.models;

public class OrderItemDetail {
    private String productName;
    private int quantity;
    private double unitPrice;
    private String orderDate;

    public OrderItemDetail(String productName, int quantity, double unitPrice, String orderDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.orderDate = orderDate;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}

