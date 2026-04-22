package com.Health.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Health.R;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPhone;
    private EditText etPassword;
    private Button btnRegister;

    private LocalHealthRepository localRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        localRepository = LocalHealthRepository.getInstance(this);
        initViews();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");

        LocalResult<User> result = localRepository.register(username, phone, password);

        btnRegister.setEnabled(true);
        btnRegister.setText("注册");

        if (result.isSuccess()) {
            Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this,
                TextUtils.isEmpty(result.getMessage()) ? "注册失败" : result.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
}
