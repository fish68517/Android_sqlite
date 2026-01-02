package com.archive.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.MyApplication;
import com.archive.app.db.OpenHelperDataBase;
import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private RadioGroup rgRole;
    private Button btnLogin, btnToRegister;
    private CheckBox cbRememberPassword;
    private OpenHelperDataBase dbHelper;
    private static final String TAG = "LoginActivity";

    // SharedPreferences 常量
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER_ME = "rememberMe";
    private static final String PREF_ROLE_ID = "roleId"; // 用于记住角色选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        rgRole = findViewById(R.id.rg_login_role);
        cbRememberPassword = findViewById(R.id.cb_remember_password);
        btnLogin = findViewById(R.id.btn_login);
        btnToRegister = findViewById(R.id.btn_to_register);

        dbHelper = new OpenHelperDataBase(this);

        // 加载保存的偏好设置
        loadPreferences();

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            int selectedRoleId = rgRole.getCheckedRadioButtonId();
            String role = selectedRoleId == R.id.rb_login_user ? "用户" : "管理员";
            System.out.println(username + " 1111" + password + " 角色： " + role);
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
    /*        User user = dbHelper.loginUser(username, password, role);
            if (user != null) {
                Log.i(TAG, "登录成功: " + username + "，角色: " + role);
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                
                // 处理记住密码逻辑
                savePreferences(username, password, role, selectedRoleId, cbRememberPassword.isChecked());

                // 跳转到主页面
               *//* MyApplication.setUser(user);
                startActivity(new Intent(this, AdminMainActivity.class));*//*
                finish();
            } else {
                Log.w(TAG, "登录失败: " + username + "，角色: " + role);
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

                // 处理记住密码逻辑
                savePreferences(username, password, role, selectedRoleId, cbRememberPassword.isChecked());

              *//*  // 跳转到主页面
                MyApplication.setUser(user);
                startActivity(new Intent(this, AdminMainActivity.class));*//*
                finish();
            }*/
        });

        btnToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    /**
     * 加载 SharedPreferences 中保存的用户登录信息和"记住密码"状态
     */
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = prefs.getString(PREF_USERNAME, null);
        String savedPassword = prefs.getString(PREF_PASSWORD, null);
        boolean rememberMe = prefs.getBoolean(PREF_REMEMBER_ME, false);
        int savedRoleId = prefs.getInt(PREF_ROLE_ID, R.id.rb_login_user); // 默认用户角色

        if (savedUsername != null) {
            etUsername.setText(savedUsername);
            Log.d(TAG, "已加载保存的用户名: " + savedUsername);
        }

       // rgRole.check(savedRoleId); // 恢复角色选择

        if (rememberMe && savedPassword != null) {
            etPassword.setText(savedPassword);
            cbRememberPassword.setChecked(true);
            Log.d(TAG, "已加载保存的密码并勾选记住密码");
        } else {
            cbRememberPassword.setChecked(false);
            Log.d(TAG, "未加载密码或未勾选记住密码");
        }
    }

    /**
     * 根据"记住密码"选项保存用户偏好
     * @param username 用户名
     * @param password 密码
     * @param role 角色字符串 (可选，主要用于日志)
     * @param selectedRoleId 选中的RadioButton的ID
     * @param rememberMe 是否记住密码
     */
    private void savePreferences(String username, String password, String role, int selectedRoleId, boolean rememberMe) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        if (rememberMe) {
            editor.putString(PREF_USERNAME, username);
            editor.putString(PREF_PASSWORD, password); // 只有当记住密码时才保存密码
            editor.putBoolean(PREF_REMEMBER_ME, true);
            editor.putInt(PREF_ROLE_ID, selectedRoleId); // 保存角色选择
            Log.i(TAG, "已保存偏好: 用户名=" + username + ", 角色=" + role + ", 记住密码=true");
        } else {
            // 如果不记住密码，可以选择清除已保存的密码，或者只保存用户名和"不记住"状态
            editor.remove(PREF_PASSWORD); // 清除密码
            editor.putBoolean(PREF_REMEMBER_ME, false);
            // 可以选择是否保留用户名和角色，如果希望不记住密码时也清除用户名和角色选择，则添加 remove
            // editor.remove(PREF_USERNAME);
            // editor.remove(PREF_ROLE_ID);
            // 或者，如果希望保留上次输入的用户名和角色即使不记住密码
            editor.putString(PREF_USERNAME, username);
            editor.putInt(PREF_ROLE_ID, selectedRoleId);
            Log.i(TAG, "已保存偏好: 用户名=" + username + ", 角色=" + role + ", 记住密码=false (密码已清除)");
        }
        editor.apply();
    }
}