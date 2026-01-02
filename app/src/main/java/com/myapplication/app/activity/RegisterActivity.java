package com.myapplication.app.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.myapplication.app.db.StudentDbHelper;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etConfirmPass;
    private StudentDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new StudentDbHelper(this);

        etUsername = findViewById(R.id.et_reg_username);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirmPass = findViewById(R.id.et_reg_password_confirm);
        MaterialButton btnRegister = findViewById(R.id.btn_register);
        TextView tvBackLogin = findViewById(R.id.tv_back_login);

        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirm = etConfirmPass.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.checkUsernameExists(user)) {
                    Toast.makeText(this, "该用户已存在", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isAdded = dbHelper.registerUser(user, pass);
                    if (isAdded) {
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish(); // 返回登录页
                    } else {
                        Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvBackLogin.setOnClickListener(v -> finish());
    }
}