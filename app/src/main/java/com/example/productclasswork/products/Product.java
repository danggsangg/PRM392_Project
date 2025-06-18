package com.example.productclasswork.products;

import java.io.Serializable;

public class Product implements Serializable {
    public int id, stock;
    public String title, description, image;
    public double price;

    public Product(int id, String title, double price, int stock, String description, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.image = image;
    }
}
