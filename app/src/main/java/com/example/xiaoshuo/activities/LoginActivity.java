package com.example.xiaoshuo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xiaoshuo.MainActivity;
import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.UserManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private Button btnLogin;
    private TextView tvRegister;
    
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = UserManager.getInstance(this);
        
/*        // 如果用户已登录，直接跳转到主界面
        if (userManager.isLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }*/

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String gender = rbMale.isChecked() ? "male" : "female";
            
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "请输入至少6位密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 验证用户是否存在
            if (userManager.checkUserExists(username)) {
                // 验证密码是否正确
                if (userManager.verifyPassword(username, password)) {
                    // 登录成功，加载用户数据
                    userManager.loadUser(username);
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                    finish();
                } else {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "用户不存在，请注册", Toast.LENGTH_SHORT).show();
            }
        });
        
        tvRegister.setOnClickListener(v -> {
            // 跳转到注册页面
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
} 