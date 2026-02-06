package com.example.healthdietapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
 * Implements password encryption and session management
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeDatabase();
        setupListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
    }

    private void initializeDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = new SessionManager(this);
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
