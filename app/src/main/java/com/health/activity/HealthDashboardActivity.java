package com.Health.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.Health.HealthApplication;
import com.Health.R;
import com.Health.health.HealthConstants;
import com.Health.health.HealthDataSyncManager;
import com.Health.health.HealthMonitorScheduler;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.local.model.HealthAlertItem;
import com.Health.local.model.HealthAlertRule;
import com.Health.local.model.HealthMetricRecord;
import com.Health.utils.HealthDebugLogger;
import com.Health.utils.SharedPrefManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthDashboardActivity extends AppCompatActivity {

    private static final int REQ_NOTIFICATION = 2001;
    private static final String TAG = "HealthDashboard";

    private TextView tvStepsValue;
    private TextView tvHeartRateValue;
    private TextView tvBloodPressureValue;
    private TextView tvAlertCountValue;
    private TextView tvRecentAlerts;
    private Button btnSaveBloodPressure;
    private Button btnSaveAlertRules;
    private EditText etSystolic;
    private EditText etDiastolic;
    private EditText etHeartRateLow;
    private EditText etHeartRateHigh;
    private EditText etSystolicLow;
    private EditText etSystolicHigh;
    private EditText etDiastolicLow;
    private EditText etDiastolicHigh;
    private EditText etStepGoal;
    private EditText etStepAlertThreshold;
    private BarChart chartStepsWeekly;
    private LineChart chartHeartRateWeekly;

    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;
    private HealthDataSyncManager syncManager;
    private ExecutorService ioExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);

        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();
        syncManager = new HealthDataSyncManager(this);
        ioExecutor = Executors.newSingleThreadExecutor();

        bindViews();
        setupCharts();
        setupListeners();
        ensureNotificationPermission();
        renderRuleInputs();
        refreshDashboard();
        HealthDebugLogger.i(TAG, "onCreate finished. userId=" + getCurrentUserId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderRuleInputs();
        refreshDashboard();
        HealthDebugLogger.d(TAG, "onResume refresh complete. userId=" + getCurrentUserId());
    }

    @Override
    protected void onDestroy() {
        if (ioExecutor != null) {
            ioExecutor.shutdownNow();
        }
        super.onDestroy();
    }

    private void bindViews() {
        tvStepsValue = findViewById(R.id.tv_steps_value);
        tvHeartRateValue = findViewById(R.id.tv_heart_rate_value);
        tvBloodPressureValue = findViewById(R.id.tv_blood_pressure_value);
        tvAlertCountValue = findViewById(R.id.tv_alert_count_value);
        tvRecentAlerts = findViewById(R.id.tv_recent_alerts);
        btnSaveBloodPressure = findViewById(R.id.btn_save_blood_pressure);
        btnSaveAlertRules = findViewById(R.id.btn_save_alert_rules);
        etSystolic = findViewById(R.id.et_systolic);
        etDiastolic = findViewById(R.id.et_diastolic);
        etHeartRateLow = findViewById(R.id.et_heart_rate_low);
        etHeartRateHigh = findViewById(R.id.et_heart_rate_high);
        etSystolicLow = findViewById(R.id.et_systolic_low);
        etSystolicHigh = findViewById(R.id.et_systolic_high);
        etDiastolicLow = findViewById(R.id.et_diastolic_low);
        etDiastolicHigh = findViewById(R.id.et_diastolic_high);
        etStepGoal = findViewById(R.id.et_step_goal);
        etStepAlertThreshold = findViewById(R.id.et_step_alert_threshold);
        chartStepsWeekly = findViewById(R.id.chart_steps_weekly);
        chartHeartRateWeekly = findViewById(R.id.chart_heart_rate_weekly);
    }

    private void setupCharts() {
        setupBarChart(chartStepsWeekly);
        setupLineChart(chartHeartRateWeekly);
    }

    private void setupBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);
        chart.setNoDataText(getString(R.string.health_no_step_data));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setDrawGridLines(true);
    }

    private void setupLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);
        chart.setNoDataText(getString(R.string.health_no_heart_rate_data));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setDrawGridLines(true);
    }

    private void setupListeners() {
        btnSaveBloodPressure.setOnClickListener(v -> saveBloodPressure());
        btnSaveAlertRules.setOnClickListener(v -> saveAlertRules());
    }

    private void saveBloodPressure() {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            HealthDebugLogger.w(TAG, "saveBloodPressure skipped because user not logged in");
            Toast.makeText(this, R.string.health_please_login, Toast.LENGTH_SHORT).show();
            return;
        }
        String systolicText = etSystolic.getText().toString().trim();
        String diastolicText = etDiastolic.getText().toString().trim();
        if (TextUtils.isEmpty(systolicText) || TextUtils.isEmpty(diastolicText)) {
            HealthDebugLogger.w(TAG, "saveBloodPressure blocked because input is incomplete");
            Toast.makeText(this, R.string.health_complete_blood_pressure, Toast.LENGTH_SHORT).show();
            return;
        }

        int systolic;
        int diastolic;
        try {
            systolic = Integer.parseInt(systolicText);
            diastolic = Integer.parseInt(diastolicText);
        } catch (NumberFormatException e) {
            HealthDebugLogger.w(TAG, "saveBloodPressure blocked because input is not numeric: systolic="
                    + systolicText + ", diastolic=" + diastolicText);
            Toast.makeText(this, R.string.health_blood_pressure_numeric, Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<HealthMetricRecord> result = repository.saveManualBloodPressure(userId, systolic, diastolic);
        if (!result.isSuccess()) {
            HealthDebugLogger.w(TAG, "saveBloodPressure failed. userId=" + userId
                    + ", systolic=" + systolic + ", diastolic=" + diastolic);
            Toast.makeText(this, R.string.health_blood_pressure_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        HealthDebugLogger.i(TAG, "saveBloodPressure success. userId=" + userId
                + ", systolic=" + systolic + ", diastolic=" + diastolic);

        etSystolic.setText("");
        etDiastolic.setText("");
        ioExecutor.execute(() -> {
            syncManager.evaluateLocalAlerts(userId);
            runOnUiThread(() -> {
                refreshDashboard();
                Toast.makeText(this, R.string.health_blood_pressure_saved, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void saveAlertRules() {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            HealthDebugLogger.w(TAG, "saveAlertRules skipped because user not logged in");
            Toast.makeText(this, R.string.health_please_login, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int heartRateLow = parseInt(etHeartRateLow);
            int heartRateHigh = parseInt(etHeartRateHigh);
            int systolicLow = parseInt(etSystolicLow);
            int systolicHigh = parseInt(etSystolicHigh);
            int diastolicLow = parseInt(etDiastolicLow);
            int diastolicHigh = parseInt(etDiastolicHigh);
            int stepGoal = parseInt(etStepGoal);
            int stepAlertThreshold = parseInt(etStepAlertThreshold);

            LocalResult<HealthAlertRule> result = repository.saveHealthAlertRule(
                    userId,
                    heartRateLow,
                    heartRateHigh,
                    systolicLow,
                    systolicHigh,
                    diastolicLow,
                    diastolicHigh,
                    stepGoal,
                    stepAlertThreshold,
                    true
            );
            Toast.makeText(
                    this,
                    result.isSuccess() ? R.string.health_thresholds_saved : R.string.health_thresholds_invalid,
                    Toast.LENGTH_SHORT
            ).show();
            HealthDebugLogger.i(TAG, "saveAlertRules result. userId=" + userId
                    + ", success=" + result.isSuccess()
                    + ", hrLow=" + heartRateLow
                    + ", hrHigh=" + heartRateHigh
                    + ", sysLow=" + systolicLow
                    + ", sysHigh=" + systolicHigh
                    + ", diaLow=" + diastolicLow
                    + ", diaHigh=" + diastolicHigh
                    + ", stepGoal=" + stepGoal
                    + ", stepAlertThreshold=" + stepAlertThreshold);
            if (result.isSuccess()) {
                HealthMonitorScheduler.scheduleImmediate(this);
            }
        } catch (NumberFormatException e) {
            HealthDebugLogger.w(TAG, "saveAlertRules blocked because one or more values are not numeric");
            Toast.makeText(this, R.string.health_thresholds_numeric, Toast.LENGTH_SHORT).show();
        }
    }

    private void renderRuleInputs() {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            HealthDebugLogger.w(TAG, "renderRuleInputs skipped because user not logged in");
            return;
        }
        repository.ensureHealthDashboardMockData(userId);
        HealthAlertRule rule = repository.getHealthAlertRule(userId);
        etHeartRateLow.setText(String.valueOf(rule.getHeartRateLow()));
        etHeartRateHigh.setText(String.valueOf(rule.getHeartRateHigh()));
        etSystolicLow.setText(String.valueOf(rule.getSystolicLow()));
        etSystolicHigh.setText(String.valueOf(rule.getSystolicHigh()));
        etDiastolicLow.setText(String.valueOf(rule.getDiastolicLow()));
        etDiastolicHigh.setText(String.valueOf(rule.getDiastolicHigh()));
        etStepGoal.setText(String.valueOf(rule.getStepGoal()));
        etStepAlertThreshold.setText(String.valueOf(rule.getStepAlertThreshold()));
        HealthDebugLogger.d(TAG, "renderRuleInputs loaded rule for userId=" + userId
                + ", hr=" + rule.getHeartRateLow() + "-" + rule.getHeartRateHigh()
                + ", bpSys=" + rule.getSystolicLow() + "-" + rule.getSystolicHigh()
                + ", bpDia=" + rule.getDiastolicLow() + "-" + rule.getDiastolicHigh()
                + ", stepGoal=" + rule.getStepGoal()
                + ", stepAlert=" + rule.getStepAlertThreshold());
    }

    private void refreshDashboard() {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            HealthDebugLogger.w(TAG, "refreshDashboard shows empty state because user not logged in");
            tvStepsValue.setText(R.string.health_value_placeholder);
            tvHeartRateValue.setText(R.string.health_value_placeholder);
            tvBloodPressureValue.setText(R.string.health_value_placeholder);
            tvAlertCountValue.setText("0");
            tvRecentAlerts.setText(R.string.health_please_login);
            chartStepsWeekly.clear();
            chartHeartRateWeekly.clear();
            return;
        }
        boolean seeded = repository.ensureHealthDashboardMockData(userId);

        HealthMetricRecord latestStep = repository.getLatestHealthMetric(
                userId,
                HealthConstants.METRIC_STEP,
                HealthConstants.SCOPE_DAILY
        );
        HealthMetricRecord latestHeartRate = repository.getLatestHealthMetric(
                userId,
                HealthConstants.METRIC_HEART_RATE,
                HealthConstants.SCOPE_LATEST
        );
        if (latestHeartRate == null) {
            latestHeartRate = repository.getLatestHealthMetric(
                    userId,
                    HealthConstants.METRIC_HEART_RATE,
                    HealthConstants.SCOPE_DAILY
            );
        }
        HealthMetricRecord latestBloodPressure = repository.getLatestHealthMetric(
                userId,
                HealthConstants.METRIC_BLOOD_PRESSURE,
                HealthConstants.SCOPE_INSTANT
        );

        tvStepsValue.setText(formatMetric(latestStep, getString(R.string.health_steps_unit), false));
        tvHeartRateValue.setText(formatMetric(latestHeartRate, getString(R.string.health_heart_rate_unit), false));
        tvBloodPressureValue.setText(formatBloodPressure(latestBloodPressure));
        tvAlertCountValue.setText(String.valueOf(repository.getUnacknowledgedHealthAlertCount(userId)));

        List<HealthAlertItem> recentAlerts = repository.listRecentHealthAlerts(userId, 5);
        HealthDebugLogger.i(TAG, "refreshDashboard loaded. userId=" + userId
                + ", seededMockData=" + seeded
                + ", latestStep=" + describeMetric(latestStep)
                + ", latestHeartRate=" + describeMetric(latestHeartRate)
                + ", latestBloodPressure=" + describeMetric(latestBloodPressure)
                + ", alertCount=" + recentAlerts.size());
        renderAlerts(recentAlerts);
        renderWeeklyCharts(userId);
    }

    private void renderAlerts(List<HealthAlertItem> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            tvRecentAlerts.setText(R.string.health_no_recent_alerts);
            HealthDebugLogger.d(TAG, "renderAlerts found no recent alerts");
            return;
        }
        StringBuilder builder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.getDefault());
        for (HealthAlertItem alert : alerts) {
            String time = formatter.format(
                    Instant.ofEpochMilli(alert.getCreatedAt()).atZone(ZoneId.systemDefault()).toLocalDateTime()
            );
            builder.append(time)
                    .append(" | ")
                    .append(alert.getTitle())
                    .append("\n")
                    .append(alert.getContent())
                    .append("\n\n");
        }
        tvRecentAlerts.setText(builder.toString().trim());
        HealthDebugLogger.d(TAG, "renderAlerts displayed alertCount=" + alerts.size());
    }

    private void renderWeeklyCharts(long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;

        List<HealthMetricRecord> stepRecords = repository.listHealthMetrics(
                userId,
                HealthConstants.METRIC_STEP,
                HealthConstants.SCOPE_DAILY,
                startTime,
                endTime
        );
        List<HealthMetricRecord> heartRateRecords = repository.listHealthMetrics(
                userId,
                HealthConstants.METRIC_HEART_RATE,
                HealthConstants.SCOPE_DAILY,
                startTime,
                endTime
        );

        renderStepChart(stepRecords, startDate);
        renderHeartRateChart(heartRateRecords, startDate);
        HealthDebugLogger.d(TAG, "renderWeeklyCharts. userId=" + userId
                + ", stepRecordCount=" + stepRecords.size()
                + ", heartRateRecordCount=" + heartRateRecords.size());
    }

    private void renderStepChart(List<HealthMetricRecord> records, LocalDate startDate) {
        Map<String, HealthMetricRecord> recordMap = new HashMap<>();
        for (HealthMetricRecord record : records) {
            recordMap.put(record.getSampleDay(), record);
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            HealthMetricRecord record = recordMap.get(date.toString());
            float value = record == null || record.getValuePrimary() == null
                    ? 0f : record.getValuePrimary().floatValue();
            entries.add(new BarEntry(i, value));
            labels.add(formatter.format(date));
        }

        chartStepsWeekly.getXAxis().setValueFormatter(new SimpleAxisFormatter(labels));
        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.health_chart_steps));
        dataSet.setColor(0xFF1E88E5);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) == 0 ? "" : String.valueOf((int) value);
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        chartStepsWeekly.setData(data);
        chartStepsWeekly.invalidate();
        HealthDebugLogger.d(TAG, "renderStepChart completed. recordCount=" + records.size()
                + ", firstLabel=" + (labels.isEmpty() ? "" : labels.get(0))
                + ", lastLabel=" + (labels.isEmpty() ? "" : labels.get(labels.size() - 1)));
    }

    private void renderHeartRateChart(List<HealthMetricRecord> records, LocalDate startDate) {
        Map<String, HealthMetricRecord> recordMap = new HashMap<>();
        for (HealthMetricRecord record : records) {
            recordMap.put(record.getSampleDay(), record);
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            HealthMetricRecord record = recordMap.get(date.toString());
            float value = record == null || record.getValuePrimary() == null
                    ? 0f : record.getValuePrimary().floatValue();
            entries.add(new Entry(i, value));
            labels.add(formatter.format(date));
        }

        chartHeartRateWeekly.getXAxis().setValueFormatter(new SimpleAxisFormatter(labels));
        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.health_chart_heart_rate));
        dataSet.setColor(0xFFD93025);
        dataSet.setCircleColor(0xFFD93025);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) == 0
                        ? ""
                        : getString(R.string.health_chart_heart_rate_value, (int) value);
            }
        });

        chartHeartRateWeekly.setData(new LineData(dataSet));
        chartHeartRateWeekly.invalidate();
        HealthDebugLogger.d(TAG, "renderHeartRateChart completed. recordCount=" + records.size()
                + ", firstLabel=" + (labels.isEmpty() ? "" : labels.get(0))
                + ", lastLabel=" + (labels.isEmpty() ? "" : labels.get(labels.size() - 1)));
    }

    private String formatMetric(HealthMetricRecord record, String unit, boolean withDay) {
        if (record == null || record.getValuePrimary() == null) {
            return getString(R.string.health_value_placeholder);
        }
        String value = String.valueOf(Math.round(record.getValuePrimary()));
        if (!withDay) {
            return getString(R.string.health_metric_format, value, unit);
        }
        return getString(R.string.health_metric_with_day_format, record.getSampleDay(), value, unit);
    }

    private String formatBloodPressure(HealthMetricRecord record) {
        if (record == null || record.getValuePrimary() == null || record.getValueSecondary() == null) {
            return getString(R.string.health_value_placeholder);
        }
        return getString(
                R.string.health_blood_pressure_format,
                Math.round(record.getValuePrimary()),
                Math.round(record.getValueSecondary()),
                getString(R.string.health_blood_pressure_unit)
        );
    }

    private int parseInt(EditText editText) {
        return Integer.parseInt(editText.getText().toString().trim());
    }

    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.POST_NOTIFICATIONS },
                REQ_NOTIFICATION
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIFICATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.health_notification_permission_granted, Toast.LENGTH_SHORT).show();
        }
    }

    private long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0L) {
            return userId;
        }
        if (HealthApplication.curUser != null && HealthApplication.curUser.getId() != null) {
            return HealthApplication.curUser.getId();
        }
        return 0L;
    }

    private String describeMetric(HealthMetricRecord record) {
        if (record == null) {
            return "null";
        }
        return "type=" + record.getMetricType()
                + ",scope=" + record.getRecordScope()
                + ",primary=" + record.getValuePrimary()
                + ",secondary=" + record.getValueSecondary()
                + ",unit=" + record.getUnit()
                + ",source=" + record.getSourceType()
                + ",sampleDay=" + record.getSampleDay()
                + ",sampleTime=" + record.getSampleTime();
    }

    private static class SimpleAxisFormatter extends ValueFormatter {

        private final List<String> labels;

        SimpleAxisFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            return index >= 0 && index < labels.size() ? labels.get(index) : "";
        }
    }
}
