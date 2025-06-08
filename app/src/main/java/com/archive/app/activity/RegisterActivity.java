package com.archive.app.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.archive.app.db.OpenHelperDataBase;
import com.example.myapplication.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private OpenHelperDataBase dbHelper;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);

        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        rgRole = findViewById(R.id.rg_register_role);
        btnRegister = findViewById(R.id.btn_register);

        dbHelper = new OpenHelperDataBase(this);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = rgRole.getCheckedRadioButtonId() == R.id.rb_register_user ? "用户" : "管理员";
            System.out.println(username + " " + password + " 角色： " + role);
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = dbHelper.registerUser(username, password);
            if (success) {
                Log.i(TAG, "注册成功: " + username + "，角色: " + role);
                Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.w(TAG, "注册失败: " + username + "，角色: " + role);
                Toast.makeText(this, "注册失败，账号可能已存在", Toast.LENGTH_SHORT).show();
            }
        });
    }
}