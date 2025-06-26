package com.example.productclasswork.models;

public class User {
    public int id;
    public String username;
    public String role;
    public boolean active;

    public User(int id, String username, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
    }
}
