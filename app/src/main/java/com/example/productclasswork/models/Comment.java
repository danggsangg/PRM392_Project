package com.example.productclasswork.models;

public class Comment {
    public int id;
    public int productId;
    public int userId;
    public int orderId;  // Thêm orderId để biết review cho đơn hàng nào
    public String username;
    public String content;
    public float rating;
    public String date;

    public Comment(int id, int productId, int userId, int orderId, String username, String content, float rating, String date) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.orderId = orderId;
        this.username = username;
        this.content = content;
        this.rating = rating;
        this.date = date;
    }
} 