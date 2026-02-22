package com.example.healthdietapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.AnimationUtils;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.PasswordUtils;
import com.example.healthdietapp.utils.SessionManager;
import com.example.healthdietapp.utils.ValidationUtils;

/**
 * Login Activity - User login screen
 * Allows users to enter credentials and login
 * Implements password encryption, session management, and remember password feature
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox rememberPasswordCheckbox;
    private Button loginButton;
    private TextView registerLink;
    private UserDAO userDAO;
    private SessionManager sessionManager;

    // SharedPreferences 常量
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_IS_REMEMBERED = "is_remembered";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initializeViews();
        initializeDatabase();
        loadSavedCredentials(); // 加载保存的密码
        setupListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        rememberPasswordCheckbox = findViewById(R.id.remember_password_checkbox);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
    }

    private void initializeDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = new SessionManager(this);
    }

    // 读取 SharedPreferences，如果勾选了记住密码则自动填充
    private void loadSavedCredentials() {
        boolean isRemembered = sharedPreferences.getBoolean(PREF_IS_REMEMBERED, false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString(PREF_USERNAME, "");
            String savedPassword = sharedPreferences.getString(PREF_PASSWORD, "");
            usernameInput.setText(savedUsername);
            passwordInput.setText(savedPassword);
            rememberPasswordCheckbox.setChecked(true);
        } else {
            // 如果没有勾选记住密码，可以考虑保留上次登录的用户名，但清空密码
            String savedUsername = sharedPreferences.getString(PREF_USERNAME, "");
            usernameInput.setText(savedUsername);
            rememberPasswordCheckbox.setChecked(false);
        }
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(loginButton, this::handleLogin);
        });
        registerLink.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(registerLink);
            navigateToRegister();
        });
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        ValidationUtils.ValidationResult usernameValidation = ValidationUtils.validateUsername(username);
        if (!usernameValidation.isValid()) {
            ErrorHandler.handleValidationException(this, usernameValidation.getMessage());
            return;
        }

        ValidationUtils.ValidationResult passwordValidation = ValidationUtils.validatePassword(password);
        if (!passwordValidation.isValid()) {
            ErrorHandler.handleValidationException(this, passwordValidation.getMessage());
            return;
        }

        // Encrypt password for verification
        String encryptedPassword = PasswordUtils.encryptPassword(password);
        if (encryptedPassword == null) {
            ErrorHandler.showShortToast(this, "密码加密失败，请重试");
            return;
        }

        // Disable login button to prevent multiple clicks
        loginButton.setEnabled(false);

        // Verify login credentials in background thread
        new Thread(() -> {
            try {
                User user = userDAO.verifyLogin(username, encryptedPassword);
                runOnUiThread(() -> {
                    loginButton.setEnabled(true);
                    if (user != null) {
                        // 登录成功 - 保存账号密码状态
                        saveCredentials(username, password);

                        // Login successful - save session and navigate to home
                        String token = generateLoginToken(user.getUserId());
                        sessionManager.saveLoginSession(user.getUserId(), user.getUsername(), token);

                        ErrorHandler.showShortToast(this, "登录成功");
                        navigateToHome();
                    } else {
                        // Login failed - show error message
                        ErrorHandler.handleAuthenticationException(this, "用户名或密码错误");
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    loginButton.setEnabled(true);
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }

    // 保存或清除 SharedPreferences 中的密码信息
    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberPasswordCheckbox.isChecked()) {
            editor.putString(PREF_USERNAME, username);
            editor.putString(PREF_PASSWORD, password);
            editor.putBoolean(PREF_IS_REMEMBERED, true);
        } else {
            editor.putString(PREF_USERNAME, username); // 保留用户名以便下次输入
            editor.remove(PREF_PASSWORD);              // 清除密码
            editor.putBoolean(PREF_IS_REMEMBERED, false);
        }
        editor.apply();
    }

    private String generateLoginToken(String userId) {
        // Generate a simple token based on userId and timestamp
        return PasswordUtils.encryptPassword(userId + System.currentTimeMillis());
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        AnimationUtils.applyFadeActivityTransition(this);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        AnimationUtils.applySlideInActivityTransition(this);
    }
}