package com.example.productclasswork.users;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.Order;
import com.example.productclasswork.models.OrderItem;
import com.example.productclasswork.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtTotal;
    Button btnOrder;
    CartAdapter adapter;
    int userId;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Nhận userId từ intent
        userId = getIntent().getIntExtra("userId", -1);
        username = getIntent().getStringExtra("username");

        recyclerView = findViewById(R.id.cartRecyclerView);
        txtTotal = findViewById(R.id.txtTotal);
        btnOrder = findViewById(R.id.btnOrder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Ensure full product info from DbHelper
        DbHelper db = new DbHelper(this);
        List<CartItem> cartItems = CartManager.getCart();
        List<CartItem> enrichedItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product p = db.getProductById(item.productId);
            if (p != null) {
                enrichedItems.add(new CartItem(p.id, p.title, p.price, item.quantity, p.stock));
            }
        }

        adapter = new CartAdapter(enrichedItems, updatedTotal -> txtTotal.setText("Total: $" + updatedTotal));
        recyclerView.setAdapter(adapter);

        btnOrder.setOnClickListener(v -> {
            List<CartItem> selected = adapter.getSelectedItems();
            if (selected.isEmpty()) {
                txtTotal.setText("No items selected");
                return;
            }

            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem item : selected) {
                orderItems.add(new OrderItem(0, item.productId, item.quantity, item.price));
            }

            String today = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            Order order = new Order(0, userId, username, today, "Pending", orderItems);

            db.saveOrder(order);

            CartManager.removeItems(selected);
            adapter.setData(CartManager.getCart());
            txtTotal.setText("Order placed successfully");
        });
    }
}