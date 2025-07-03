package com.example.orderfood.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private TextView loginLink;
    private DataBaseOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DataBaseOpenHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> handleRegister());

        loginLink.setOnClickListener(v -> {
            finish(); // 结束当前活动，返回登录页
        });
    }

    private void handleRegister() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        register(username, password);
    }

    private void register(String username, String password) {
        long result = dbHelper.registerUser(username, password);
        if (result == -1L) {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
        } else if (result >= 1L) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            finish(); // 返回登录页
        } else {
            Toast.makeText(this, "注册失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
} 