package com.example.orderfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfood.DataBaseOpenHelper;

import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private DataBaseOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DataBaseOpenHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);

        // 检查是否保存了登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
        }

        loginButton.setOnClickListener(v -> {
            handleLogin();
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {  // 检查输入是否为空
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        login(username, password);
    }

    private void login(String username, String password) {
        User user = dbHelper.loginUser(username, password);
        if (user != null) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

            // 总是保存登录信息
            saveLoginInfo(username, password);
            MyApplication.saveUser(user);

            // 保存当前用户ID以供整个应用使用
            SharedPreferences sessionPrefs = getSharedPreferences("AppSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sessionPrefs.edit();
            editor.putInt("CURRENT_USER_ID", user.getId());
            editor.apply();

            // 启动主活动
            Intent intent = new Intent(LoginActivity.this, com.example.orderfood.activity.MainActivity.class);
            startActivity(intent);

            finish(); // 关闭登录活动
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLoginInfo(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("rememberMe", true); // 默认记住
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    // 从 sharedPreferences 中读取登录信息
    private void loadLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
        }
    }
}
