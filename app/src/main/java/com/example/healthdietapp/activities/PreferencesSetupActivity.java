package com.example.healthdietapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.models.UserPreferences;
import com.example.healthdietapp.utils.AnimationUtils;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

/**
 * Preferences Setup Activity - User dietary preferences setup screen
 * Allows users to set their taste tendency, diet type, and health goals
 * Saves preferences to database and navigates to home screen
 */
public class PreferencesSetupActivity extends AppCompatActivity {

    private MaterialAutoCompleteTextView tasteTendencySpinner;
    private MaterialAutoCompleteTextView  dietTypeSpinner;
    private MaterialAutoCompleteTextView  healthGoalSpinner;
    private Button saveButton;
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_setup);

        // Get user ID from intent
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            // If no user ID provided, get from session
            sessionManager = new SessionManager(this);
            userId = sessionManager.getUserId();
        }

        if (userId == null) {
            ErrorHandler.showShortToast(this, "用户信息获取失败");
            finish();
            return;
        }

        initializeViews();
        initializeDatabase();
        setupSpinners();
        setupListeners();
    }

    private void initializeViews() {
        tasteTendencySpinner = findViewById(R.id.taste_tendency_spinner);
        dietTypeSpinner = findViewById(R.id.diet_type_spinner);
        healthGoalSpinner = findViewById(R.id.health_goal_spinner);
        saveButton = findViewById(R.id.save_button);
    }

    private void initializeDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper);
        sessionManager = new SessionManager(this);
    }

    private void setupSpinners() {
        // Setup taste tendency spinner
        String[] tasteTendencies = {
                "清淡",
                "微辣",
                "中辣",
                "重口味"
        };
        // 注意：将 simple_spinner_item 修改为 simple_dropdown_item_1line
        ArrayAdapter<String> tasteTendencyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                tasteTendencies
        );
        tasteTendencySpinner.setAdapter(tasteTendencyAdapter);
        // 强制点击时弹出下拉菜单
        tasteTendencySpinner.setOnClickListener(v -> tasteTendencySpinner.showDropDown());

        // Setup diet type spinner
        String[] dietTypes = {
                "普通饮食",
                "素食",
                "低碳水",
                "高蛋白",
                "无麸质"
        };
        ArrayAdapter<String> dietTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                dietTypes
        );
        dietTypeSpinner.setAdapter(dietTypeAdapter);
        dietTypeSpinner.setOnClickListener(v -> dietTypeSpinner.showDropDown());

        // Setup health goal spinner
        String[] healthGoals = {
                "减脂",
                "增肌",
                "维持体重",
                "改善体质",
                "控制血糖"
        };
        ArrayAdapter<String> healthGoalAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                healthGoals
        );
        healthGoalSpinner.setAdapter(healthGoalAdapter);
        healthGoalSpinner.setOnClickListener(v -> healthGoalSpinner.showDropDown());
    }
    private void setupListeners() {
        saveButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(saveButton, this::handleSavePreferences);
        });
    }

    private void handleSavePreferences() {
        // 获取输入框中的文本值 (MaterialAutoCompleteTextView 使用 getText() 而不是 getSelectedItem())
        String tasteTendency = tasteTendencySpinner.getText().toString().trim();
        String dietType = dietTypeSpinner.getText().toString().trim();
        String healthGoal = healthGoalSpinner.getText().toString().trim();

        // 验证用户是否已选择（同时检查是否为空以及是否是默认的提示语）
        if (tasteTendency.isEmpty() || tasteTendency.startsWith("请选择") ||
                dietType.isEmpty() || dietType.startsWith("请选择") ||
                healthGoal.isEmpty() || healthGoal.startsWith("请选择")) {
            ErrorHandler.handleValidationException(this, "请完整填写所有偏好设置");
            return;
        }

        saveButton.setEnabled(false);

        // Create preferences object
        UserPreferences preferences = new UserPreferences();
        preferences.setUserId(userId);
        preferences.setTasteTendency(tasteTendency);
        preferences.setDietType(dietType);
        preferences.setHealthGoal(healthGoal);

        try {
            boolean success = userDAO.saveUserPreferences(preferences);
            runOnUiThread(() -> {
                saveButton.setEnabled(true);
                if (success) {
                    ErrorHandler.showShortToast(this, "偏好设置保存成功");
                    // navigateToHome();
                    navigateToLogin();
                } else {
                    ErrorHandler.showShortToast(this, "保存失败，请重试");
                }
            });
        } catch (Exception e) {
            ErrorHandler.logException("PreferencesSetupActivity", e);
            runOnUiThread(() -> {
                saveButton.setEnabled(true);
                ErrorHandler.handleDatabaseException(this, e);
            });
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(PreferencesSetupActivity.this, MainActivity.class);
        startActivity(intent);
        AnimationUtils.applyFadeActivityTransition(this);
        finish();
    }


    private void navigateToLogin() {
        Intent intent = new Intent(PreferencesSetupActivity.this, LoginActivity.class);
        startActivity(intent);
        AnimationUtils.applyFadeActivityTransition(this);
        finish();
    }
}
