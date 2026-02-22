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
import com.example.healthdietapp.utils.ValidationUtils;

/**
 * Register Activity - User registration screen
 * Allows new users to create an account with username and password
 * Validates username uniqueness and password strength
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        initializeDatabase();
        setupListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);
    }

    private void initializeDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(registerButton, this::handleRegister);
        });
        loginLink.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(loginLink);
            navigateToLogin();
        });
    }

    private void handleRegister() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate username
        ValidationUtils.ValidationResult usernameValidation = ValidationUtils.validateUsername(username);
        if (!usernameValidation.isValid()) {
            ErrorHandler.handleValidationException(this, usernameValidation.getMessage());
            return;
        }

        // Validate password
        ValidationUtils.ValidationResult passwordValidation = ValidationUtils.validatePassword(password);
        if (!passwordValidation.isValid()) {
            ErrorHandler.handleValidationException(this, passwordValidation.getMessage());
            return;
        }

        // Validate password confirmation
        ValidationUtils.ValidationResult confirmValidation = ValidationUtils.validatePasswordConfirmation(password, confirmPassword);
        if (!confirmValidation.isValid()) {
            ErrorHandler.handleValidationException(this, confirmValidation.getMessage());
            return;
        }

        // Disable register button to prevent multiple clicks
        registerButton.setEnabled(false);

        // Perform registration in background thread
        try {
            // Validate username uniqueness
            if (userDAO.usernameExists(username)) {
                runOnUiThread(() -> {
                    registerButton.setEnabled(true);
                    ErrorHandler.handleValidationException(this, "用户名已存在，请选择其他用户名");
                });
                return;
            }

            // Encrypt password
            // String encryptedPassword = PasswordUtils.encryptPassword(password);
            if (password == null) {
                runOnUiThread(() -> {
                    registerButton.setEnabled(true);
                    ErrorHandler.showShortToast(this, "请输入密码，请重试");
                });
                return;
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setNickname(username); // Default nickname is username
            newUser.setCreatedAt(System.currentTimeMillis());
            newUser.setUpdatedAt(System.currentTimeMillis());

            // Save user to database
            boolean success = userDAO.createUser(newUser);

            runOnUiThread(() -> {
                registerButton.setEnabled(true);
                if (success) {
                    ErrorHandler.showShortToast(this, "注册成功，请设置您的饮食偏好");
                    navigateToPreferencesSetup(newUser.getUserId());
                } else {
                    ErrorHandler.showShortToast(this, "注册失败，请重试");
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                registerButton.setEnabled(true);
                ErrorHandler.handleDatabaseException(this, e);
            });
        }
    }

    private void navigateToPreferencesSetup(String userId) {
        Intent intent = new Intent(RegisterActivity.this, PreferencesSetupActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
        AnimationUtils.applySlideInActivityTransition(this);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        AnimationUtils.applySlideOutActivityTransition(this);
        finish();
    }
}
