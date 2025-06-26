// models/OrderItem.java
package com.example.productclasswork.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    public int orderId;
    public int productId;
    public int quantity;
    public double unitPrice;

    public OrderItem(int orderId, int productId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
