package com.example.orderfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.model.Student;
import com.example.orderfood.model.MerchantBean;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, registerButton;
    private ImageView logoImageView;
    private CheckBox rememberMeCheckBox;  // 添加 CheckBox 的引用


    private EditText regionEditText;

    private RadioGroup loginRadioGroup;
    private RadioButton studentRadioButton;
    private RadioButton merchantRadioButton;
    private RadioButton adminLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logoImageView = findViewById(R.id.logo);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        rememberMeCheckBox = findViewById(R.id.remember_me);  // 初始化 CheckBox

        regionEditText = findViewById(R.id.region);
        loginRadioGroup = findViewById(R.id.login_radio_group);
        studentRadioButton = findViewById(R.id.radio_student);
        merchantRadioButton = findViewById(R.id.radio_merchant);
        adminLoginButton = findViewById(R.id.admin_login);


        // 检查是否保存了登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true); // 设置 CheckBox 状态
        }

        loginButton.setOnClickListener(v -> {
            handleLogin();
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loadLoginInfo();
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String region = regionEditText.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {  // 检查输入是否为空
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (studentRadioButton.isChecked()) {
            loginStudent(username, password);
        } else if (merchantRadioButton.isChecked()) {
            loginMerchant(username, password);
        } else if (adminLoginButton.isChecked()) {
            loginAdmin(username, password);
        } else {
            Toast.makeText(this, "请选择登录类型", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginAdmin(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) {
            MyApplication.saveAdmin(username, password);
            Toast.makeText(LoginActivity.this, "校园管理员登录成功", Toast.LENGTH_SHORT).show();
            saveLoginInfo(username, password);  // 保存登录信息
            Intent intent = new Intent(LoginActivity.this, AdminMerchantActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "校园管理员登录失败", Toast.LENGTH_SHORT).show();
        }

    }

    private void loginStudent(String username, String password) {

        DBMysqlHelper.getInstance(this).loginStudent(username,password, new DBMysqlHelper.DatabaseCallback<Map<String, Object>>() {

            @Override
            public void onSuccess(Map<String, Object> result) {
                // Convert studentJson to Student object
                System.out.println("打印学生信息：" + result);
                Gson gson = new Gson();
                Map<String, Object> resultMap = gson.fromJson(result.toString(), new TypeToken<Map<String, Object>>() {}.getType());
                Student student = gson.fromJson(String.valueOf((LinkedTreeMap) resultMap.get("student")), Student.class);
                String token = (String) resultMap.get("token");

                System.out.println("学生信息: " + student);
                System.out.println("Token: " + token);
                Toast.makeText(LoginActivity.this, "学生登录成功", Toast.LENGTH_SHORT).show();
                MyApplication.saveUser(username,password, Integer.valueOf(student.getStudentId()));
                saveLoginInfo(username, password);  // 保存登录信息
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    private void loginMerchant(String username, String password) {

        DBMysqlHelper.getInstance(this).loginMerchant(username, password,new DBMysqlHelper.DatabaseCallback<Map<String, String>>() {

            @Override
            public void onSuccess(Map<String, String> result) {
                // Convert studentJson to Student object
                Gson gson = new Gson();
                MerchantBean merchant = gson.fromJson(result.get("merchant"), MerchantBean.class);
                if (merchant == null) {
                    Toast.makeText(LoginActivity.this, "商家不存在或密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!merchant.getName() .equals(username) || !merchant.getPassword().equals(password)) {
                    Toast.makeText(LoginActivity.this, "商家不存在或密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, "商家登录成功", Toast.LENGTH_SHORT).show();
                MyApplication.saveMerchant(merchant);
                saveLoginInfo(username, password);  // 保存登录信息
                Intent intent = new Intent(LoginActivity.this, MerchantMainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    private void saveLoginInfo(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMeCheckBox.isChecked()) {
            editor.putBoolean("rememberMe", true);
            editor.putString("username", username);
            editor.putString("password", password);
        } else {
            editor.clear();  // 如果用户未勾选"记住我"，则清空保存的登录信息
        }
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
            rememberMeCheckBox.setChecked(true); // 设置 CheckBox 状态
        }
    }
}
