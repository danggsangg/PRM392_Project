// AdminOrderActivity.java
package com.example.productclasswork.admins;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;
import com.example.productclasswork.models.Order;

import java.util.List;

public class AdminOrderActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtEmpty;
    AdminOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        recyclerView = findViewById(R.id.recyclerAdminOrders);
        txtEmpty = findViewById(R.id.txtEmptyAdminOrder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DbHelper db = new DbHelper(this);
        List<Order> orderList = db.getAllOrdersWithUsernames();

        if (orderList.isEmpty()) {
            txtEmpty.setText("No orders found.");
        } else {
            adapter = new AdminOrderAdapter(orderList);
            recyclerView.setAdapter(adapter);
        }
    }
}
