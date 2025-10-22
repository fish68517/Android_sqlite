package com.example.application.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.application.databinding.ActivityLoginBinding;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        binding.btnPerformLogin.setOnClickListener(v -> attemptLogin());
        binding.tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });


        binding.btnBiometricLogin.setOnClickListener(v -> showBiometricPrompt());
    }

    private void attemptLogin() {
        // Reset errors
        binding.tilLoginEmail.setError(null);
        binding.tilLoginPassword.setError(null);

        String email = binding.etLoginEmail.getText().toString().trim();
        String password = binding.etLoginPassword.getText().toString().trim();


        boolean cancel = false;
        android.view.View focusView = null;

        // Task: Form Validation - Level 2
        // Description: Validate that password is not empty.
        if (TextUtils.isEmpty(password)) {
            binding.tilLoginPassword.setError("Password cannot be empty");
            focusView = binding.etLoginPassword;
            cancel = true;
        }

        // Task: Form Validation - Level 2
        // Description: Validate that email format is correct.
        if (TextUtils.isEmpty(email)) {
            binding.tilLoginEmail.setError("Email cannot be empty");
            focusView = binding.etLoginEmail;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilLoginEmail.setError("Please enter a valid email address");
            focusView = binding.etLoginEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // 验证注册信息
            if (validateUserCredentials(email, password)) {
                performLogin(email);
            } else {
                // 显示登录失败信息
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 验证用户凭据
     * @param email 用户邮箱
     * @param password 用户密码
     * @return 验证结果
     */
    private boolean validateUserCredentials(String email, String password) {
        // TODO: 实际应用中应连接数据库或后端API验证用户凭据
        // 这里简单检查SharedPreference中是否有匹配的用户信息
        String registeredEmail = sharedPreferences.getString(Constants.KEY_EMAIL, "");
        // 注意：实际应用中不应明文存储密码，应使用加密方式存储和比较
        String registeredPassword = sharedPreferences.getString(Constants.PASSWORD, "");

        return email.equals(registeredEmail) && password.equals(registeredPassword);
    }

    private void performLogin(String email) {
        // Save login status and user information
        sharedPreferences.edit()
                .putBoolean(Constants.KEY_LOGGED_IN, true)
                .putString(Constants.KEY_EMAIL, email)
                .apply();

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        // Close login page and return to previous page (usually MainActivity -> ProfileFragment)
        Intent intent = new Intent();
        // 跳转到MainActivity
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void showBiometricPrompt() {
        // 任务: 生物识别 (Biometrics) - 级别 3
        // 描述: 这里是生物识别功能的完整实现流程。
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Authentication failed: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LoginActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                // login(sharedPreferences.getString(KEY_EMAIL, "")); // 模拟登录
                // Simulate login success - SharedPreferences should already have email if biometric was set up
                sharedPreferences.edit().putBoolean(Constants.KEY_LOGGED_IN, true).apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Authentication failed, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use your fingerprint to login")
                .setNegativeButtonText("Login with account password")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
