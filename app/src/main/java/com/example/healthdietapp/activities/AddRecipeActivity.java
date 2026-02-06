package com.example.healthdietapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.database.UserRecipeDAO;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.models.UserRecipe;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * AddRecipeActivity - Handles adding a recipe to user's meal schedule
 * Includes date selection, meal type selection, and conflict handling
 */
public class AddRecipeActivity extends AppCompatActivity {

    private TextView selectedDateTextView;
    private RadioGroup mealTypeRadioGroup;
    private Button selectDateButton;
    private Button confirmButton;
    private Button cancelButton;
    private Button backButton;

    private DatabaseHelper dbHelper;
    private RecipeDAO recipeDAO;
    private UserRecipeDAO userRecipeDAO;
    private SessionManager sessionManager;

    private String recipeId;
    private String selectedDate;
    private String selectedMealType;
    private Recipe recipe;

    private Calendar selectedCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initializeViews();
        initializeDatabase();
        loadRecipeData();
        setupClickListeners();
        initializeDate();
    }

    private void initializeViews() {
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        mealTypeRadioGroup = findViewById(R.id.mealTypeRadioGroup);
        selectDateButton = findViewById(R.id.selectDateButton);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        recipeDAO = new RecipeDAO(dbHelper);
        userRecipeDAO = new UserRecipeDAO(dbHelper);
        sessionManager = new SessionManager(this);
    }

    private void loadRecipeData() {
        recipeId = getIntent().getStringExtra("recipe_id");
        if (recipeId == null) {
            ErrorHandler.showShortToast(this, "食谱未找到");
            finish();
            return;
        }

        new Thread(() -> {
            try {
                recipe = recipeDAO.getRecipeById(recipeId);
                runOnUiThread(() -> {
                    if (recipe == null) {
                        ErrorHandler.showShortToast(this, "食谱未找到");
                        finish();
                    }
                });
            } catch (Exception e) {
                ErrorHandler.logException("AddRecipeActivity", e);
                runOnUiThread(() -> {
                    ErrorHandler.handleDatabaseException(this, e);
                    finish();
                });
            }
        }).start();
    }

    private void initializeDate() {
        selectedCalendar = Calendar.getInstance();
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        selectedDate = dateFormat.format(selectedCalendar.getTime());
        selectedDateTextView.setText(selectedDate);
    }

    private void setupClickListeners() {
        selectDateButton.setOnClickListener(v -> showDatePicker());

        confirmButton.setOnClickListener(v -> {
            int selectedRadioButtonId = mealTypeRadioGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                ErrorHandler.showShortToast(this, "请选择餐次");
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            selectedMealType = selectedRadioButton.getText().toString().toLowerCase();

            addRecipeToSchedule();
        });

        cancelButton.setOnClickListener(v -> finish());
        
        backButton.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(year, month, dayOfMonth);
                    updateDateDisplay();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addRecipeToSchedule() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            ErrorHandler.showShortToast(this, "用户未登录");
            finish();
            return;
        }

        confirmButton.setEnabled(false);

        new Thread(() -> {
            try {
                // Check for conflict
                boolean hasConflict = userRecipeDAO.hasConflict(userId, selectedDate, selectedMealType);

                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    if (hasConflict) {
                        showConflictDialog(userId);
                    } else {
                        performAddRecipe(userId);
                    }
                });
            } catch (Exception e) {
                ErrorHandler.logException("AddRecipeActivity", e);
                runOnUiThread(() -> {
                    confirmButton.setEnabled(true);
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }

    private void showConflictDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("日程冲突")
                .setMessage("您已为此餐次安排了食谱。请选择处理方式。")
                .setPositiveButton("替换", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            boolean success = userRecipeDAO.replaceUserRecipe(userId, selectedDate, selectedMealType, recipeId);
                            runOnUiThread(() -> {
                                if (success) {
                                    ErrorHandler.showShortToast(this, "食谱已替换");
                                    finish();
                                } else {
                                    ErrorHandler.showShortToast(this, "替换食谱失败");
                                }
                            });
                        } catch (Exception e) {
                            ErrorHandler.logException("AddRecipeActivity", e);
                            runOnUiThread(() -> {
                                ErrorHandler.handleDatabaseException(this, e);
                            });
                        }
                    }).start();
                })
                .setNeutralButton("加餐", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            boolean success = userRecipeDAO.addExtraMeal(userId, selectedDate, selectedMealType, recipeId);
                            runOnUiThread(() -> {
                                if (success) {
                                    ErrorHandler.showShortToast(this, "加餐已添加");
                                    finish();
                                } else {
                                    ErrorHandler.showShortToast(this, "添加加餐失败");
                                }
                            });
                        } catch (Exception e) {
                            ErrorHandler.logException("AddRecipeActivity", e);
                            runOnUiThread(() -> {
                                ErrorHandler.handleDatabaseException(this, e);
                            });
                        }
                    }).start();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performAddRecipe(String userId) {
        new Thread(() -> {
            try {
                UserRecipe userRecipe = new UserRecipe();
                userRecipe.setUserId(userId);
                userRecipe.setRecipeId(recipeId);
                userRecipe.setDate(selectedDate);
                userRecipe.setMealType(selectedMealType);
                userRecipe.setAddedAt(System.currentTimeMillis());

                boolean success = userRecipeDAO.addUserRecipe(userRecipe);
                runOnUiThread(() -> {
                    if (success) {
                        ErrorHandler.showShortToast(this, "食谱已添加");
                        finish();
                    } else {
                        ErrorHandler.showShortToast(this, "添加食谱失败");
                    }
                });
            } catch (Exception e) {
                ErrorHandler.logException("AddRecipeActivity", e);
                runOnUiThread(() -> {
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }
}
