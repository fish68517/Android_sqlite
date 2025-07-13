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

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private Button btnRegister;
    private TextView tvLogin;
    
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        userManager = UserManager.getInstance(this);
        
        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String gender = rbMale.isChecked() ? "male" : "female";
            
            // 验证输入
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "请输入至少6位密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 检查用户名是否已存在
            if (userManager.checkUserExists(username)) {
                Toast.makeText(this, R.string.username_exists, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 注册用户
            boolean success = userManager.registerUser(username, password, gender);
            
            if (success) {
                Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                
                // 直接登录并跳转到主页面
                userManager.loadUser(username);
                
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
        
        tvLogin.setOnClickListener(v -> {
            // 返回登录页面
            finish();
        });
    }
} 