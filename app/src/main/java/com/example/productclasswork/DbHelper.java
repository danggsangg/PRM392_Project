package com.example.productclasswork;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.Comment;
import com.example.productclasswork.models.Order;
import com.example.productclasswork.models.OrderItem;
import com.example.productclasswork.models.Product;
import com.example.productclasswork.models.User;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "app_db";
    private static final int DB_VERSION = 6; // Tăng version lên để trigger onUpgrade

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Các bảng hiện có
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT, password TEXT, role TEXT, active INTEGER)");
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, price REAL, stock INTEGER, description TEXT, image TEXT)");
        db.execSQL("CREATE TABLE cart (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER, quantity INTEGER)");
        db.execSQL("CREATE TABLE orders (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, date TEXT, status TEXT)");
        db.execSQL("CREATE TABLE order_items (orderId INTEGER, productId INTEGER, quantity INTEGER, unitPrice REAL)");
        
        // Tạo bảng comments với orderId
        createCommentsTable(db);

        // Data mẫu cho users và products
        db.execSQL("INSERT INTO users (username, password, role, active) VALUES " +
                "('admin', '123', 'admin', 1), " +
                "('user1', '123', 'user', 1), " +
                "('user2', '123', 'user', 1)");

        db.execSQL("INSERT INTO products (title, price, stock, description, image) VALUES " +
                "('Laptop A', 500.0, 10, 'Lightweight laptop', 'https://via.placeholder.com/100')," +
                "('Phone B', 300.0, 25, 'High performance phone', 'https://via.placeholder.com/100')," +
                "('Tablet C', 250.0, 8, 'Compact tablet device', 'https://via.placeholder.com/100')");
    }

    private void createCommentsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE comments (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productId INTEGER, userId INTEGER, orderId INTEGER, content TEXT, rating REAL, date TEXT, username TEXT, " +
                "FOREIGN KEY(productId) REFERENCES products(id), " +
                "FOREIGN KEY(userId) REFERENCES users(id), " +
                "FOREIGN KEY(orderId) REFERENCES orders(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Xóa và tạo lại tất cả các bảng để đảm bảo schema mới nhất
        db.execSQL("DROP TABLE IF EXISTS comments");
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS users");
        
        // Tạo lại tất cả các bảng với schema mới nhất
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

    // GetAll
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

    public int getUnreviewedPurchaseCount(int userId, int productId) {
        SQLiteDatabase db = getReadableDatabase();
        // Đếm số đơn hàng hợp lệ chưa review (giống logic lấy orderId)
        String sql = "SELECT COUNT(DISTINCT o.id) as unreviewed_count FROM orders o " +
                "INNER JOIN order_items oi ON o.id = oi.orderId " +
                "WHERE o.userId = ? AND oi.productId = ? " +
                "AND o.status IN ('Completed', 'Delivering', 'Pending') " +
                "AND o.id NOT IN (SELECT orderId FROM comments WHERE userId = ? AND productId = ?)";
        Cursor c = db.rawQuery(sql, new String[]{
            String.valueOf(userId),
            String.valueOf(productId),
            String.valueOf(userId),
            String.valueOf(productId)
        });
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndexOrThrow("unreviewed_count"));
        }
        c.close();
        return count;
    }

    public boolean addComment(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // Log thông tin comment trước khi lưu
            android.util.Log.d("CommentDebug", "Saving comment: " +
                    "productId=" + comment.productId +
                    ", userId=" + comment.userId +
                    ", orderId=" + comment.orderId +
                    ", content=" + comment.content +
                    ", rating=" + comment.rating +
                    ", date=" + comment.date);

            // Kiểm tra xem orderId có tồn tại và có trạng thái hợp lệ không
            Cursor orderCheck = db.rawQuery(
                "SELECT id FROM orders WHERE id = ? AND status IN ('Completed', 'Delivering', 'Pending')",
                new String[]{String.valueOf(comment.orderId)}
            );
            boolean orderValid = orderCheck.moveToFirst();
            orderCheck.close();

            if (!orderValid) {
                android.util.Log.e("CommentDebug", "Order not found or invalid status: " + comment.orderId);
                return false;
            }

            // Kiểm tra xem đã có comment cho order này chưa
            Cursor commentCheck = db.rawQuery(
                "SELECT id FROM comments WHERE orderId = ?",
                new String[]{String.valueOf(comment.orderId)}
            );
            boolean hasComment = commentCheck.moveToFirst();
            commentCheck.close();

            if (hasComment) {
                android.util.Log.e("CommentDebug", "Comment already exists for order: " + comment.orderId);
                return false;
            }

            // Kiểm tra xem order có thuộc về user này không và có chứa sản phẩm này không
            Cursor orderItemCheck = db.rawQuery(
                "SELECT o.id FROM orders o " +
                "INNER JOIN order_items oi ON o.id = oi.orderId " +
                "WHERE o.id = ? AND o.userId = ? AND oi.productId = ?",
                new String[]{
                    String.valueOf(comment.orderId),
                    String.valueOf(comment.userId),
                    String.valueOf(comment.productId)
                }
            );
            boolean orderBelongsToUser = orderItemCheck.moveToFirst();
            orderItemCheck.close();

            if (!orderBelongsToUser) {
                android.util.Log.e("CommentDebug", "Order does not belong to user or does not contain product");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put("productId", comment.productId);
            values.put("userId", comment.userId);
            values.put("orderId", comment.orderId);
            values.put("content", comment.content);
            values.put("rating", comment.rating);
            values.put("date", comment.date);

            long result = db.insert("comments", null, values);
            
            if (result == -1) {
                android.util.Log.e("CommentDebug", "Failed to insert comment into database");
                return false;
            }
            
            android.util.Log.d("CommentDebug", "Comment saved successfully with id: " + result);
            return true;
        } catch (Exception e) {
            android.util.Log.e("CommentDebug", "Error saving comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public List<Comment> getCommentsByProductId(int productId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        
        try {
            String sql = "SELECT c.*, u.username FROM comments c " +
                    "INNER JOIN users u ON c.userId = u.id " +
                    "WHERE c.productId = ? ORDER BY c.date DESC";
                    
            Cursor c = db.rawQuery(sql, new String[]{String.valueOf(productId)});
            
            while (c != null && c.moveToNext()) {
                try {
                    comments.add(new Comment(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getInt(c.getColumnIndexOrThrow("productId")),
                        c.getInt(c.getColumnIndexOrThrow("userId")),
                        c.getInt(c.getColumnIndexOrThrow("orderId")),
                        c.getString(c.getColumnIndexOrThrow("username")),
                        c.getString(c.getColumnIndexOrThrow("content")),
                        c.getFloat(c.getColumnIndexOrThrow("rating")),
                        c.getString(c.getColumnIndexOrThrow("date"))
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    public float getAverageRating(int productId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
            "SELECT AVG(rating) as avg_rating FROM comments WHERE productId = ?",
            new String[]{String.valueOf(productId)}
        );
        float rating = 0;
        if (c.moveToFirst()) {
            rating = c.getFloat(c.getColumnIndexOrThrow("avg_rating"));
        }
        c.close();
        return rating;
    }

    public boolean hasUserPurchasedProduct(int userId, int productId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(*) as count FROM orders o " +
                "INNER JOIN order_items oi ON o.id = oi.orderId " +
                "WHERE o.userId = ? AND oi.productId = ? AND o.status IN ('Completed', 'Delivering', 'Pending')";
        
        Cursor c = db.rawQuery(sql, new String[]{
            String.valueOf(userId), 
            String.valueOf(productId)
        });
        
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndexOrThrow("count"));
        }
        c.close();
        return count > 0;
    }

    public boolean hasUserReviewedProduct(int userId, int productId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(*) as count FROM comments WHERE userId = ? AND productId = ?";
        
        Cursor c = db.rawQuery(sql, new String[]{
            String.valueOf(userId),
            String.valueOf(productId)
        });
        
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndexOrThrow("count"));
        }
        c.close();
        return count > 0;
    }

    public List<Integer> getOrderIdsForUnreviewedPurchases(int userId, int productId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Integer> orderIds = new ArrayList<>();
        
        try {
            // Lấy danh sách order_id của các đơn hàng chưa review
            String sql = "SELECT DISTINCT o.id FROM orders o " +
                    "INNER JOIN order_items oi ON o.id = oi.orderId " +
                    "WHERE o.userId = ? AND oi.productId = ? " +
                    "AND o.status IN ('Completed', 'Delivering', 'Pending') " +
                    "AND o.id NOT IN (SELECT orderId FROM comments WHERE userId = ? AND productId = ?)";
            
            android.util.Log.d("OrderDebug", "Executing query: " + sql);
            android.util.Log.d("OrderDebug", "Parameters: userId=" + userId + ", productId=" + productId);
            
            Cursor c = db.rawQuery(sql, new String[]{
                String.valueOf(userId), 
                String.valueOf(productId),
                String.valueOf(userId),
                String.valueOf(productId)
            });
            
            while (c.moveToNext()) {
                int orderId = c.getInt(0);
                orderIds.add(orderId);
                android.util.Log.d("OrderDebug", "Found unreviewed order: " + orderId);
            }
            c.close();
        } catch (Exception e) {
            android.util.Log.e("OrderDebug", "Error getting unreviewed orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        android.util.Log.d("OrderDebug", "Total unreviewed orders found: " + orderIds.size());
        return orderIds;
    }

    public String getOrderStatus(int orderId) {
        SQLiteDatabase db = getReadableDatabase();
        String status = null;
        try {
            Cursor c = db.rawQuery(
                "SELECT status FROM orders WHERE id = ?",
                new String[]{String.valueOf(orderId)}
            );
            if (c.moveToFirst()) {
                status = c.getString(c.getColumnIndexOrThrow("status"));
            }
            c.close();
        } catch (Exception e) {
            android.util.Log.e("OrderDebug", "Error getting order status: " + e.getMessage());
            e.printStackTrace();
        }
        return status;
    }

    @Nullable
    public User getUserById(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
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
}
