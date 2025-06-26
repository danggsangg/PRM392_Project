package com.example.productclasswork;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.productclasswork.users.CartActivity;
import com.example.productclasswork.users.OrderHistoryActivity;
import com.example.productclasswork.users.UserProductFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserHomeActivity extends AppCompatActivity {
    int userId;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userId = getIntent().getIntExtra("userId", -1);
        username = getIntent().getStringExtra("username");

        BottomNavigationView nav = findViewById(R.id.userBottomNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer, new ProfileFragment(username)).commit();

        nav.setOnItemSelectedListener(item -> {
            Fragment selected;
            if (item.getItemId() == R.id.nav_product) {
                selected = new UserProductFragment();
            }else if(item.getItemId() == R.id.nav_cart){
                Intent cartIntent = new Intent(UserHomeActivity.this, CartActivity.class);
                cartIntent.putExtra("userId", userId);
                cartIntent.putExtra("username", username);
                startActivity(cartIntent);
                return true;
            }
            else if(item.getItemId() == R.id.nav_orders){
                Intent intent = new Intent(UserHomeActivity.this, OrderHistoryActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return true;
            }
            else {
                selected = new ProfileFragment(username);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer, selected).commit();
            return true;
        });
    }
}
