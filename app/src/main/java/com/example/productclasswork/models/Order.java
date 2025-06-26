// models/Order.java
package com.example.productclasswork.models;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    public int id;
    public int userId;

    public String username;
    public String date;
    public List<OrderItem> items;
    public String status;

    public Order(int id, int userId, String date, List<OrderItem> items) {
        this(id, userId, null, date, "Pending", items);
    }

    public Order(int id, int userId, String date, String status, List<OrderItem> items) {
        this(id, userId, null, date, status, items);
    }

    public Order(int id, int userId, String username, String date, String status, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.date = date;
        this.status = status;
        this.items = items;
    }
}

