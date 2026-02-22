package com.example.healthdietapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.HealthRecordDAO;
import com.example.healthdietapp.models.HealthRecord;
import com.example.healthdietapp.utils.DateUtils;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;
import com.example.healthdietapp.utils.ValidationUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

/**
 * HealthRecordActivity - Displays and manages daily health data records
 */
public class HealthRecordActivity extends AppCompatActivity {

    private Button backButton;
    private Button prevDateButton;
    private Button nextDateButton;
    private Button datePickerButton;
    private Button saveButton;
    private Button viewChartButton; // 新增：查看曲线按钮

    private EditText weightInput;
    private EditText waterInput;
    private EditText measurementsInput;

    private MaterialCardView weightSection;
    private MaterialCardView waterSection;
    private MaterialCardView measurementsSection;

    private DatabaseHelper dbHelper;
    private HealthRecordDAO healthRecordDAO;
    private SessionManager sessionManager;

    private String currentDate;
    private String userId;
    private HealthRecord currentRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record);

        initializeViews();
        initializeDatabase();
        initializeCurrentDate();
        setupDateNavigation();
        setupListeners();
        loadRecord();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        prevDateButton = findViewById(R.id.prevDateButton);
        nextDateButton = findViewById(R.id.nextDateButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        saveButton = findViewById(R.id.saveButton);
        viewChartButton = findViewById(R.id.viewChartButton); // 绑定曲线按钮

        weightInput = findViewById(R.id.weightInput);
        waterInput = findViewById(R.id.waterInput);
        measurementsInput = findViewById(R.id.measurementsInput);

        weightSection = findViewById(R.id.weightSection);
        waterSection = findViewById(R.id.waterSection);
        measurementsSection = findViewById(R.id.measurementsSection);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        healthRecordDAO = new HealthRecordDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void initializeCurrentDate() {
        currentDate = DateUtils.getCurrentDate();
        updateDateDisplay();
    }

    private void setupDateNavigation() {
        backButton.setOnClickListener(v -> finish());

        prevDateButton.setOnClickListener(v -> {
            currentDate = DateUtils.addDays(currentDate, -1);
            updateDateDisplay();
            loadRecord();
        });

        nextDateButton.setOnClickListener(v -> {
            currentDate = DateUtils.addDays(currentDate, 1);
            updateDateDisplay();
            loadRecord();
        });

        datePickerButton.setOnClickListener(v -> showDatePicker());
    }

    private void updateDateDisplay() {
        String displayDate = DateUtils.getDisplayDate(currentDate);
        datePickerButton.setText(displayDate);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse(currentDate);
            calendar.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    currentDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    updateDateDisplay();
                    loadRecord();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveRecord());

        // 绑定跳转曲线图事件
        viewChartButton.setOnClickListener(v -> {
            try {
                // 等你创建好 HealthChartActivity 后，解除下面的注释即可跳转
                 Intent intent = new Intent(HealthRecordActivity.this, HealthChartActivity.class);
                 startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadRecord() {
        new Thread(() -> {
            try {
                currentRecord = healthRecordDAO.getHealthRecordByDate(userId, currentDate);
                runOnUiThread(this::displayRecord);
            } catch (Exception e) {
                ErrorHandler.logException("HealthRecordActivity", e);
                runOnUiThread(() -> {
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }

    private void displayRecord() {
        // 去掉了原来根据 currentRecord 隐藏界面的逻辑，让三个卡片始终可见
        weightSection.setVisibility(android.view.View.VISIBLE);
        waterSection.setVisibility(android.view.View.VISIBLE);
        measurementsSection.setVisibility(android.view.View.VISIBLE);

        if (currentRecord == null) {
            // 没有数据时，清空输入框，方便用户直接输入新数据
            clearInputs();
        } else {
            // 有数据时，直接将数据回填到 EditText 中
            if (currentRecord.getWeight() > 0) {
                weightInput.setText(String.valueOf(currentRecord.getWeight()));
            } else {
                weightInput.setText("");
            }

            if (currentRecord.getWaterIntake() > 0) {
                waterInput.setText(String.valueOf((int) currentRecord.getWaterIntake()));
            } else {
                waterInput.setText("");
            }

            if (currentRecord.getMeasurements() != null && !currentRecord.getMeasurements().isEmpty()) {
                measurementsInput.setText(currentRecord.getMeasurements());
            } else {
                measurementsInput.setText("");
            }
        }
    }

    private void clearInputs() {
        weightInput.setText("");
        waterInput.setText("");
        measurementsInput.setText("");
    }

    private void saveRecord() {
        String weightStr = weightInput.getText().toString().trim();
        String waterStr = waterInput.getText().toString().trim();
        String measurements = measurementsInput.getText().toString().trim();

        // 至少输入一项
        if (weightStr.isEmpty() && waterStr.isEmpty() && measurements.isEmpty()) {
            ErrorHandler.handleValidationException(this, "请至少输入一项健康数据");
            return;
        }

        // 校验输入范围
        if (!weightStr.isEmpty()) {
            ValidationUtils.ValidationResult weightValidation = ValidationUtils.validateNumericInput(weightStr, "体重", 20, 300);
            if (!weightValidation.isValid()) {
                ErrorHandler.handleValidationException(this, weightValidation.getMessage());
                return;
            }
        }

        if (!waterStr.isEmpty()) {
            ValidationUtils.ValidationResult waterValidation = ValidationUtils.validateNumericInput(waterStr, "饮水量", 0, 10000);
            if (!waterValidation.isValid()) {
                ErrorHandler.handleValidationException(this, waterValidation.getMessage());
                return;
            }
        }

        saveButton.setEnabled(false);

        new Thread(() -> {
            try {
                HealthRecord record;
                boolean isUpdating = (currentRecord != null); // 判断是更新还是新建

                if (isUpdating) {
                    record = currentRecord;
                } else {
                    record = new HealthRecord();
                    record.setUserId(userId);
                    record.setDate(currentDate);
                }

                if (!weightStr.isEmpty()) {
                    record.setWeight(Float.parseFloat(weightStr));
                }
                if (!waterStr.isEmpty()) {
                    record.setWaterIntake(Float.parseFloat(waterStr));
                }
                if (!measurements.isEmpty()) {
                    record.setMeasurements(measurements);
                }

                // 执行数据库保存/更新操作
                boolean success;
                if (isUpdating) {
                    success = healthRecordDAO.updateHealthRecord(record);
                } else {
                    success = healthRecordDAO.createHealthRecord(record);
                }

                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    if (success) {
                        ErrorHandler.showShortToast(this, "记录已保存");
                        currentRecord = record; // 更新当前缓存
                        displayRecord();        // 重新展示最新数据
                    } else {
                        ErrorHandler.showShortToast(this, "保存记录失败");
                    }
                });
            } catch (Exception e) {
                ErrorHandler.logException("HealthRecordActivity", e);
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }
}