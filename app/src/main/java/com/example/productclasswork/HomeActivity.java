package com.example.productclasswork;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.productclasswork.products.ProductFragment;
import com.example.productclasswork.users.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = getIntent().getStringExtra("username");

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment(username)).commit();

        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment(username)).commit();
                return true;
            } else if (item.getItemId() == R.id.nav_product) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProductFragment()).commit();
                return true;
            } else if (item.getItemId() == R.id.nav_users){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new UserFragment()).commit();
                return true;
            }
            return false;
        });
    }
}
