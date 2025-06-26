package com.example.productclasswork.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    public int productId;
    public String title;
    public double price;
    public int quantity;
    public int stock;
    public CartItem(int productId, String title, double price, int quantity, int stock) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
    }
}
