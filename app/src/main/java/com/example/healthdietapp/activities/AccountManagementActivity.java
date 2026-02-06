package com.example.healthdietapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.database.UserRecipeDAO;
import com.example.healthdietapp.database.HealthRecordDAO;
import com.example.healthdietapp.database.SearchHistoryDAO;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.PasswordUtils;
import com.example.healthdietapp.utils.SessionManager;

/**
 * AccountManagementActivity - Handles account management operations
 * Allows users to change password and delete their account
 */
public class AccountManagementActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private UserRecipeDAO userRecipeDAO;
    private HealthRecordDAO healthRecordDAO;
    private SearchHistoryDAO searchHistoryDAO;

    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button changePasswordButton;
    private Button deleteAccountButton;
    private Button backButton;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        initializeManagers();
        initializeViews();
        setupButtonListeners();
    }

    private void initializeManagers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper);
        postDAO = new PostDAO(dbHelper);
        userRecipeDAO = new UserRecipeDAO(dbHelper);
        healthRecordDAO = new HealthRecordDAO(dbHelper);
        searchHistoryDAO = new SearchHistoryDAO(dbHelper);
        userId = sessionManager.getUserId();
    }

    private void initializeViews() {
        oldPasswordInput = findViewById(R.id.oldPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> finish());

        changePasswordButton.setOnClickListener(v -> handleChangePassword());

        deleteAccountButton.setOnClickListener(v -> handleDeleteAccount());
    }

    /**
     * Handle password change operation
     */
    private void handleChangePassword() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写所有密码字段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "新密码至少需要6个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "新密码和确认密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform password change in background thread
        new Thread(() -> {
            try {
                // Get current user
                User user = userDAO.getUserById(userId);
                if (user == null) {
                    runOnUiThread(() -> Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Verify old password
                String encryptedOldPassword = PasswordUtils.encryptPassword(oldPassword);
                if (!user.getPassword().equals(encryptedOldPassword)) {
                    runOnUiThread(() -> Toast.makeText(this, "旧密码错误", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Update password
                String encryptedNewPassword = PasswordUtils.encryptPassword(newPassword);
                boolean success = userDAO.updatePassword(userId, encryptedNewPassword);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        clearPasswordFields();
                    } else {
                        Toast.makeText(this, "密码修改失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "发生错误：" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Handle account deletion operation
     */
    private void handleDeleteAccount() {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("删除账户")
                .setMessage("确定要删除账户吗？此操作无法撤销，所有数据将被永久删除。")
                .setPositiveButton("确定", (dialog, which) -> {
                    // Show password confirmation dialog
                    showPasswordConfirmationDialog();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * Show password confirmation dialog before account deletion
     */
    private void showPasswordConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认密码");
        builder.setMessage("请输入您的密码以确认删除账户");

        EditText passwordInput = new EditText(this);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint("输入密码");
        builder.setView(passwordInput);

        builder.setPositiveButton("确认", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            performAccountDeletion(password);
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * Perform account deletion after password verification
     */
    private void performAccountDeletion(String password) {
        new Thread(() -> {
            try {
                // Get current user
                User user = userDAO.getUserById(userId);
                if (user == null) {
                    runOnUiThread(() -> Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Verify password
                String encryptedPassword = PasswordUtils.encryptPassword(password);
                if (!user.getPassword().equals(encryptedPassword)) {
                    runOnUiThread(() -> Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Delete all user-related data
                deleteUserData(userId);

                // Delete user account
                boolean success = userDAO.deleteUser(userId);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "账户已删除", Toast.LENGTH_SHORT).show();
                        // Clear session and navigate to login
                        sessionManager.logout();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "账户删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "发生错误：" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Delete all user-related data from database
     */
    private void deleteUserData(String userId) {
        try {
            // Delete user posts and related data (likes, collections)
            postDAO.deleteUserPosts(userId);

            // Delete user recipes
            userRecipeDAO.deleteUserRecipes(userId);

            // Delete health records
            healthRecordDAO.deleteUserRecords(userId);

            // Delete search history
            searchHistoryDAO.deleteAllUserSearchHistory(userId);

            // Delete user preferences
            userDAO.deleteUserPreferences(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear password input fields
     */
    private void clearPasswordFields() {
        oldPasswordInput.setText("");
        newPasswordInput.setText("");
        confirmPasswordInput.setText("");
    }
}
