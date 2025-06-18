package com.example.productclasswork;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.productclasswork.products.Product;
import com.example.productclasswork.users.User;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "app_db";
    private static final int DB_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT, password TEXT, role TEXT, active INTEGER)");
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price REAL, stock INTEGER, description TEXT, image TEXT)");

        db.execSQL("INSERT INTO users (username, password, role, active) VALUES " +
                "('admin', '123', 'admin', 1), " +
                "('user1', '123', 'user', 1), " +
                "('user2', '123', 'user', 1)");

        db.execSQL("INSERT INTO products (title, price, stock, description, image) VALUES " +
                "('Laptop A', 500.0, 10, 'Lightweight laptop', 'https://via.placeholder.com/100')," +
                "('Phone B', 300.0, 25, 'High performance phone', 'https://via.placeholder.com/100')," +
                "('Tablet C', 250.0, 8, 'Compact tablet device', 'https://via.placeholder.com/100')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean isValid = c.moveToFirst();
        c.close();
        return isValid;
    }

    @SuppressLint("Range")
    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users", null);
        while (c.moveToNext()) {
            list.add(new User(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("username")),
                    c.getString(c.getColumnIndexOrThrow("role")),
                    c.getInt(c.getColumnIndexOrThrow("active")) == 1
            ));
        }
        c.close();
        return list;
    }

    public void setUserActive(int id, boolean active) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", active ? 1 : 0);
        db.update("users", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }


    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM products", null);
        while (c.moveToNext()) {
            list.add(new Product(
                    c.getInt(0),
                    c.getString(1),
                    c.getDouble(2),
                    c.getInt(3),
                    c.getString(4),
                    c.getString(5)
            ));
        }
        c.close();
        return list;
    }

    // CREATE
    public void insertProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", p.title);
        values.put("price", p.price);
        values.put("stock", p.stock);
        values.put("description", p.description);
        values.put("image", p.image);
        db.insert("products", null, values);
        db.close();
    }

    // UPDATE
    public void updateProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", p.title);
        values.put("price", p.price);
        values.put("stock", p.stock);
        values.put("description", p.description);
        values.put("image", p.image);
        db.update("products", values, "id = ?", new String[]{String.valueOf(p.id)});
        db.close();
    }

    // DELETE
    public void deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("products", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
