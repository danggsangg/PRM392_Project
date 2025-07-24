package com.example.productclasswork;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.productclasswork.admins.AdminOrderActivity;
import com.example.productclasswork.admins.RevenueActivity;
import com.example.productclasswork.admins.products.AdminProductFragment;
import com.example.productclasswork.admins.users.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        username = getIntent().getStringExtra("username");

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment(username)).commit();

        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment(username)).commit();
                return true;
            } else if (item.getItemId() == R.id.nav_product) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminProductFragment()).commit();
                return true;
            } else if (item.getItemId() == R.id.nav_users){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new UserFragment()).commit();
                return true;
            } else if (item.getItemId() == R.id.nav_orders){
                startActivity(new Intent(AdminHomeActivity.this, AdminOrderActivity.class));
                return true;
            }
            else if (item.getItemId() == R.id.nav_revenue) {
                startActivity(new Intent(AdminHomeActivity.this, RevenueActivity.class));
                return true;
            }
            return false;
        });
    }
}
