package com.example.application.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.User;


public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        spinnerRole = findViewById(R.id.spinner_role);
        btnRegister = findViewById(R.id.btn_register);

        // 设置角色下拉框
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        // 1. 检查输入是否为空
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 检查两次输入的密码是否一致
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 检查用户名是否已存在
        User existingUser = dbHelper.getUserByUsername(username);
        if (existingUser != null) {
            Toast.makeText(this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. 创建新用户并添加到数据库
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // 注意：实际项目中密码需要加密存储
        newUser.setRole(role);

        long result = dbHelper.addUser(newUser);

        if (result != -1) {
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
            finish(); // 注册成功后关闭当前页面，返回登录页
        } else {
            Toast.makeText(this, "注册失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
}