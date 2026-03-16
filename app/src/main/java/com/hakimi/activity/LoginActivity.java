package com.hakimi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.User;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
import com.hakimi.utils.SharedPrefManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount;
    private EditText etPassword;
    private CheckBox cbRememberLogin;
    private Button btnLogin;
    private Button btnRegister;

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = SharedPrefManager.getInstance();
        apiService = RetrofitClient.getInstance().getApiService();

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
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u624b\u673a\u53f7\u6216\u7528\u6237\u540d",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u5bc6\u7801", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);

        btnLogin.setEnabled(false);
        btnLogin.setText("\u767b\u5f55\u4e2d...");

        apiService.login(params).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                    Response<ApiResponse<User>> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("\u767b\u5f55");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        Gson gson = new Gson();

                        saveLoginPreference(account, password);

                        HakimiApplication.getInstance().curUser = user;
                        sharedPrefManager.saveToken(gson.toJson(user));
                        if (user.getId() != null) {
                            sharedPrefManager.saveUserId(user.getId());
                        }
                        sharedPrefManager.saveUsername(user.getUsername());
                        sharedPrefManager.savePhone(user.getPhone());

                        Toast.makeText(LoginActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String message = apiResponse.getMessage();
                        if (TextUtils.isEmpty(message)) {
                            message = "\u767b\u5f55\u5931\u8d25";
                        }
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "\u767b\u5f55\u5931\u8d25",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("\u767b\u5f55");
                Toast.makeText(LoginActivity.this,
                        "\u7f51\u7edc\u9519\u8bef: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
