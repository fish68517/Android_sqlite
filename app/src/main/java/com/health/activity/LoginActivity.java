package com.Health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.Health.HealthApplication;
import com.Health.R;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.model.User;
import com.Health.utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount;
    private EditText etPassword;
    private CheckBox cbRememberLogin;
    private Button btnLogin;
    private Button btnRegister;

    private LocalHealthRepository localRepository;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = SharedPrefManager.getInstance();
        localRepository = LocalHealthRepository.getInstance(this);

        initViews();
        restoreSavedLogin();
    }

    private void initViews() {
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        cbRememberLogin = findViewById(R.id.cb_remember_login);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void restoreSavedLogin() {
        boolean rememberLogin = sharedPrefManager.isRememberLogin();
        cbRememberLogin.setChecked(rememberLogin);
        if (rememberLogin) {
            etAccount.setText(sharedPrefManager.getLoginAccount());
            etPassword.setText(sharedPrefManager.getLoginPassword());
            etPassword.setSelection(etPassword.getText().length());
        }
    }

    private void doLogin() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入手机号或用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("登录中...");

        LocalResult<User> result = localRepository.login(account, password);

        btnLogin.setEnabled(true);
        btnLogin.setText("登录");

        if (!result.isSuccess() || result.getData() == null) {
            Toast.makeText(this,
                    TextUtils.isEmpty(result.getMessage()) ? "登录失败" : result.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        User user = result.getData();
        Gson gson = new Gson();

        saveLoginPreference(account, password);

        HealthApplication.setUser(user);
        sharedPrefManager.saveToken(gson.toJson(user));
        if (user.getId() != null) {
            sharedPrefManager.saveUserId(user.getId());
        }
        sharedPrefManager.saveUsername(user.getUsername());
        sharedPrefManager.savePhone(user.getPhone());

        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveLoginPreference(String account, String password) {
        if (cbRememberLogin.isChecked()) {
            sharedPrefManager.saveRememberLogin(true);
            sharedPrefManager.saveLoginAccount(account);
            sharedPrefManager.saveLoginPassword(password);
        } else {
            sharedPrefManager.clearSavedLogin();
        }
    }
}
