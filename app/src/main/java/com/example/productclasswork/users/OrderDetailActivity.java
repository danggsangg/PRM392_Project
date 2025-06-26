package com.example.productclasswork.users;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.OrderItem;
import com.example.productclasswork.models.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtOrderTotal;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recyclerView = findViewById(R.id.recyclerOrderDetail);
        txtOrderTotal = findViewById(R.id.txtOrderTotal);
        db = new DbHelper(this);

        List<OrderItem> orderItems = (List<OrderItem>) getIntent().getSerializableExtra("orderItems");
        List<CartItem> cartItems = new ArrayList<>();

        double total = 0;
        for (OrderItem item : orderItems) {
            Product p = db.getProductById(item.productId);
            if (p != null) {
                cartItems.add(new CartItem(p.id, p.title, item.unitPrice, item.quantity, p.stock));
                total += item.unitPrice * item.quantity;
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderDetailAdapter(cartItems));
        txtOrderTotal.setText("Total: $" + total);
    }
}

