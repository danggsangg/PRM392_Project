package com.example.productclasswork.users;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.CartItem;
import com.example.productclasswork.models.Order;

import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtEmpty;
    OrderHistoryAdapter adapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.recyclerOrderHistory);
        txtEmpty = findViewById(R.id.txtEmptyOrder);

        userId = getIntent().getIntExtra("userId", -1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DbHelper db = new DbHelper(this);
        List<Order> orderList = db.getOrdersByUserId(userId);

        if (orderList.isEmpty()) {
            txtEmpty.setText("No orders placed yet.");
        } else {
            adapter = new OrderHistoryAdapter(orderList);
            recyclerView.setAdapter(adapter);
        }


    }
}
