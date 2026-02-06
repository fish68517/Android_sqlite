package com.example.healthdietapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.HealthRecordDAO;
import com.example.healthdietapp.models.HealthRecord;
import com.example.healthdietapp.utils.DateUtils;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;
import com.example.healthdietapp.utils.ValidationUtils;

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
    private EditText weightInput;
    private EditText waterInput;
    private EditText measurementsInput;
    private TextView noRecordMessage;
    private LinearLayout weightSection;
    private LinearLayout waterSection;
    private LinearLayout measurementsSection;

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
        setupSaveButton();
        loadRecord();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        prevDateButton = findViewById(R.id.prevDateButton);
        nextDateButton = findViewById(R.id.nextDateButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        saveButton = findViewById(R.id.saveButton);
        weightInput = findViewById(R.id.weightInput);
        waterInput = findViewById(R.id.waterInput);
        measurementsInput = findViewById(R.id.measurementsInput);
        noRecordMessage = findViewById(R.id.noRecordMessage);
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

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveRecord());
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
        if (currentRecord == null) {
            // No record for this date
            noRecordMessage.setVisibility(android.view.View.VISIBLE);
            weightSection.setVisibility(android.view.View.GONE);
            waterSection.setVisibility(android.view.View.GONE);
            measurementsSection.setVisibility(android.view.View.GONE);
            clearInputs();
        } else {
            // Record exists, display it
            noRecordMessage.setVisibility(android.view.View.GONE);
            weightSection.setVisibility(android.view.View.VISIBLE);
            waterSection.setVisibility(android.view.View.VISIBLE);
            measurementsSection.setVisibility(android.view.View.VISIBLE);

            if (currentRecord.getWeight() > 0) {
                weightInput.setText(String.valueOf(currentRecord.getWeight()));
            }
            if (currentRecord.getWaterIntake() > 0) {
                waterInput.setText(String.valueOf((int) currentRecord.getWaterIntake()));
            }
            if (currentRecord.getMeasurements() != null && !currentRecord.getMeasurements().isEmpty()) {
                measurementsInput.setText(currentRecord.getMeasurements());
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

        // Validate at least one field is filled
        if (weightStr.isEmpty() && waterStr.isEmpty() && measurements.isEmpty()) {
            ErrorHandler.handleValidationException(this, "请至少输入一项健康数据");
            return;
        }

        // Validate numeric inputs if provided
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
                if (currentRecord != null) {
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

                boolean success;
                if (currentRecord != null) {
                    success = healthRecordDAO.updateHealthRecord(record);
                } else {
                    success = healthRecordDAO.createHealthRecord(record);
                }

                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    if (success) {
                        ErrorHandler.showShortToast(this, "记录已保存");
                        currentRecord = record;
                        displayRecord();
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
