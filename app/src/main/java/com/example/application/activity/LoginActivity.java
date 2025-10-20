package com.example.application.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.User;


public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            // 登录成功
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

            // *** 核心步骤: 保存用户ID和用户名到 SharedPreferences ***
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("LOGGED_IN_USER_ID", user.getId());
            editor.putString("LOGGED_IN_USERNAME", user.getUsername());
            editor.putString("LOGGED_IN_USER_ROLE", user.getRole()); // 保存角色
            editor.apply();

            // 根据角色跳转到不同的主页面
            // 根据角色跳转到不同的主页面
            Intent intent;
            if ("老师".equals(user.getRole())) {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            } else if ("医生".equals(user.getRole())) {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            } else if ("管理员".equals(user.getRole())) {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            } else { // 学生和其他角色都跳转到默认的MainActivity
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();

        } else {
            // 登录失败
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}