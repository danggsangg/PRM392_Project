package com.example.productclasswork;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.Order;
import com.example.productclasswork.models.OrderItem;
import com.example.productclasswork.models.Product;
import com.example.productclasswork.models.User;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "app_db";
    private static final int DB_VERSION = 4;

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

        db.execSQL("CREATE TABLE cart (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER, quantity INTEGER)");

        db.execSQL("CREATE TABLE orders (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, date TEXT, status TEXT)");
        db.execSQL("CREATE TABLE order_items (orderId INTEGER, productId INTEGER, quantity INTEGER, unitPrice REAL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_items");

        onCreate(db);
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean isValid = c.moveToFirst();
        c.close();
        return isValid;
    }
    @Nullable
    public User getUserByCredentials(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        if (c.moveToFirst()) {
            User u = new User(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("username")),
                    c.getString(c.getColumnIndexOrThrow("role")),
                    c.getInt(c.getColumnIndexOrThrow("active")) == 1
            );
            c.close();
            return u;
        }
        c.close();
        return null;
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

    @Nullable
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            Product p = new Product(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("title")),
                    c.getDouble(c.getColumnIndexOrThrow("price")),
                    c.getInt(c.getColumnIndexOrThrow("stock")),
                    c.getString(c.getColumnIndexOrThrow("description")),
                    c.getString(c.getColumnIndexOrThrow("image"))
            );
            c.close();
            return p;
        }
        c.close();
        return null;
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

    // Add to cart
    public void addToCart(int productId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cart WHERE product_id=?", new String[]{String.valueOf(productId)});
        if (c.moveToFirst()) {
            // Nếu sản phẩm đã có thì tăng số lượng
            int quantity = c.getInt(c.getColumnIndexOrThrow("quantity"));
            ContentValues values = new ContentValues();
            values.put("quantity", quantity + 1);
            db.update("cart", values, "product_id=?", new String[]{String.valueOf(productId)});
        } else {
            // Nếu chưa có thì thêm mới
            ContentValues values = new ContentValues();
            values.put("product_id", productId);
            values.put("quantity", 1);
            db.insert("cart", null, values);
        }
        c.close();
        db.close();
    }

    public void saveOrder(Order order) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues orderValues = new ContentValues();
        orderValues.put("userId", order.userId);
        orderValues.put("date", order.date);
        orderValues.put("status", order.status);
        long orderId = db.insert("orders", null, orderValues);

        for (OrderItem item : order.items) {
            ContentValues itemValues = new ContentValues();
            itemValues.put("orderId", orderId);
            itemValues.put("productId", item.productId);
            itemValues.put("quantity", item.quantity);
            itemValues.put("unitPrice", item.unitPrice);
            db.insert("order_items", null, itemValues);

            db.execSQL("UPDATE products SET stock = stock - ? WHERE id = ?",
                    new Object[]{item.quantity, item.productId});
        }

        db.close();
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM orders WHERE userId = ?", new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            int orderId = c.getInt(c.getColumnIndexOrThrow("id"));
            String date = c.getString(c.getColumnIndexOrThrow("date"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));

            List<OrderItem> items = new ArrayList<>();
            Cursor itemCursor = db.rawQuery("SELECT * FROM order_items WHERE orderId = ?", new String[]{String.valueOf(orderId)});
            while (itemCursor.moveToNext()) {
                int productId = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("productId"));
                int quantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"));
                double unitPrice = itemCursor.getDouble(itemCursor.getColumnIndexOrThrow("unitPrice"));
                items.add(new OrderItem(orderId, productId, quantity, unitPrice));
            }
            itemCursor.close();

            orders.add(new Order(orderId, userId, date, status, items));
        }
        c.close();
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM orders", null);
        while (c.moveToNext()) {
            int orderId = c.getInt(c.getColumnIndexOrThrow("id"));
            int userId = c.getInt(c.getColumnIndexOrThrow("userId"));
            String date = c.getString(c.getColumnIndexOrThrow("date"));

            List<OrderItem> items = new ArrayList<>();
            Cursor itemCursor = db.rawQuery("SELECT * FROM order_items WHERE orderId = ?", new String[]{String.valueOf(orderId)});
            while (itemCursor.moveToNext()) {
                int productId = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("productId"));
                int quantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"));
                double unitPrice = itemCursor.getDouble(itemCursor.getColumnIndexOrThrow("unitPrice"));
                items.add(new OrderItem(orderId, productId, quantity, unitPrice));
            }
            itemCursor.close();

            orders.add(new Order(orderId, userId, date, items));
        }
        c.close();
        return orders;
    }

    public List<Order> getAllOrdersWithUsernames() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT o.id AS orderId, o.date, o.status, u.id AS userId, u.username " +
                "FROM orders o INNER JOIN users u ON o.userId = u.id";
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            int orderId = c.getInt(c.getColumnIndexOrThrow("orderId"));
            int userId = c.getInt(c.getColumnIndexOrThrow("userId"));
            String username = c.getString(c.getColumnIndexOrThrow("username"));
            String date = c.getString(c.getColumnIndexOrThrow("date"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));

            List<OrderItem> items = new ArrayList<>();
            Cursor itemCursor = db.rawQuery("SELECT * FROM order_items WHERE orderId = ?",
                    new String[]{String.valueOf(orderId)});
            while (itemCursor.moveToNext()) {
                int productId = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("productId"));
                int quantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"));
                double unitPrice = itemCursor.getDouble(itemCursor.getColumnIndexOrThrow("unitPrice"));
                items.add(new OrderItem(orderId, productId, quantity, unitPrice));
            }
            itemCursor.close();

            Order o = new Order(orderId, userId, username, date, status, items);
            orders.add(o);
        }

        c.close();
        return orders;
    }


    public void updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update("orders", values, "id = ?", new String[]{String.valueOf(orderId)});
        db.close();
    }

}
