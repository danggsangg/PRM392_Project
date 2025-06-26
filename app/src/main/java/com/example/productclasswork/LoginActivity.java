package com.example.productclasswork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.productclasswork.models.User;

public class LoginActivity extends AppCompatActivity {
    EditText txtUsername, txtPassword;
    Button btnLogin;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DbHelper(this);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String user = txtUsername.getText().toString();
            String pass = txtPassword.getText().toString();
            User u = db.getUserByCredentials(user, pass);
            if (u != null && u.active) {
                Intent i;
                if (u.role.equals("admin")) {
                    i = new Intent(LoginActivity.this, AdminHomeActivity.class);
                } else {
                    i = new Intent(LoginActivity.this, UserHomeActivity.class);
                }
                i.putExtra("userId", u.id);
                i.putExtra("username", u.username);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Login Failed or Account Locked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
