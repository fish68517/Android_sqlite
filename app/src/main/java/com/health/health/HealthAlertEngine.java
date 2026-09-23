package com.Health.health;

import android.content.Context;

import com.Health.R;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.local.model.HealthAlertItem;
import com.Health.local.model.HealthAlertRule;
import com.Health.local.model.HealthMetricRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HealthAlertEngine {

    private static final long HEART_RATE_ALERT_COOLDOWN_MS = 4L * 60L * 60L * 1000L;
    private static final long BLOOD_PRESSURE_ALERT_COOLDOWN_MS = 6L * 60L * 60L * 1000L;

    private final Context appContext;
    private final LocalHealthRepository repository;

    public HealthAlertEngine(Context context) {
        this.appContext = context.getApplicationContext();
        this.repository = LocalHealthRepository.getInstance(appContext);
    }

    public List<HealthAlertItem> evaluate(long userId, HealthAlertRule rule,
            HealthMetricRecord latestStep, HealthMetricRecord latestHeartRate,
            HealthMetricRecord latestBloodPressure) {
        List<HealthAlertItem> alerts = new ArrayList<>();
        if (rule == null) {
            return alerts;
        }

        HealthAlertItem heartRateAlert = evaluateHeartRate(userId, rule, latestHeartRate);
        if (heartRateAlert != null) {
            alerts.add(heartRateAlert);
        }

        HealthAlertItem bloodPressureAlert = evaluateBloodPressure(userId, rule, latestBloodPressure);
        if (bloodPressureAlert != null) {
            alerts.add(bloodPressureAlert);
        }

        HealthAlertItem stepAlert = evaluateStep(userId, rule, latestStep);
        if (stepAlert != null) {
            alerts.add(stepAlert);
        }

        if (!alerts.isEmpty()) {
            for (HealthAlertItem alert : alerts) {
                HealthAlertNotifier.notifyAlert(appContext, alert);
            }
            if (rule.isVoiceEnabled()) {
                HealthVoiceAlertManager.getInstance(appContext).speak(alerts.get(0).getContent());
            }
        }
        return alerts;
    }

    private HealthAlertItem evaluateHeartRate(long userId, HealthAlertRule rule, HealthMetricRecord record) {
        if (record == null || record.getValuePrimary() == null) {
            return null;
        }
        double bpm = record.getValuePrimary();
        if (bpm >= rule.getHeartRateHigh()) {
            return createAlertIfAllowed(userId, HealthConstants.METRIC_HEART_RATE,
                    HealthConstants.ALERT_HEART_RATE_HIGH,
                    HealthConstants.ALERT_LEVEL_DANGER,
                    appContext.getString(R.string.health_alert_title_heart_rate),
                    appContext.getString(R.string.health_alert_content_heart_rate_high),
                    bpm, null, record.getSourceType(), HEART_RATE_ALERT_COOLDOWN_MS);
        }
        if (bpm <= rule.getHeartRateLow()) {
            return createAlertIfAllowed(userId, HealthConstants.METRIC_HEART_RATE,
                    HealthConstants.ALERT_HEART_RATE_LOW,
                    HealthConstants.ALERT_LEVEL_WARNING,
                    appContext.getString(R.string.health_alert_title_heart_rate),
                    appContext.getString(R.string.health_alert_content_heart_rate_low),
                    bpm, null, record.getSourceType(), HEART_RATE_ALERT_COOLDOWN_MS);
        }
        return null;
    }

    private HealthAlertItem evaluateBloodPressure(long userId, HealthAlertRule rule, HealthMetricRecord record) {
        if (record == null || record.getValuePrimary() == null || record.getValueSecondary() == null) {
            return null;
        }
        double systolic = record.getValuePrimary();
        double diastolic = record.getValueSecondary();
        if (systolic >= rule.getSystolicHigh() || diastolic >= rule.getDiastolicHigh()) {
            return createAlertIfAllowed(userId, HealthConstants.METRIC_BLOOD_PRESSURE,
                    HealthConstants.ALERT_BLOOD_PRESSURE_HIGH,
                    HealthConstants.ALERT_LEVEL_DANGER,
                    appContext.getString(R.string.health_alert_title_blood_pressure),
                    appContext.getString(R.string.health_alert_content_blood_pressure_high),
                    systolic, diastolic, record.getSourceType(), BLOOD_PRESSURE_ALERT_COOLDOWN_MS);
        }
        if (systolic <= rule.getSystolicLow() || diastolic <= rule.getDiastolicLow()) {
            return createAlertIfAllowed(userId, HealthConstants.METRIC_BLOOD_PRESSURE,
                    HealthConstants.ALERT_BLOOD_PRESSURE_LOW,
                    HealthConstants.ALERT_LEVEL_WARNING,
                    appContext.getString(R.string.health_alert_title_blood_pressure),
                    appContext.getString(R.string.health_alert_content_blood_pressure_low),
                    systolic, diastolic, record.getSourceType(), BLOOD_PRESSURE_ALERT_COOLDOWN_MS);
        }
        return null;
    }

    private HealthAlertItem evaluateStep(long userId, HealthAlertRule rule, HealthMetricRecord record) {
        if (record == null || record.getValuePrimary() == null || record.getSampleDay() == null) {
            return null;
        }
        if (!LocalDate.now().toString().equals(record.getSampleDay())) {
            return null;
        }
        int currentHour = java.time.LocalTime.now().getHour();
        if (currentHour < 20 || record.getValuePrimary() >= rule.getStepAlertThreshold()) {
            return null;
        }
        String alertKey = HealthConstants.ALERT_STEP_LOW + "_" + record.getSampleDay();
        return createAlertIfAllowed(userId, HealthConstants.METRIC_STEP, alertKey,
                HealthConstants.ALERT_LEVEL_WARNING,
                appContext.getString(R.string.health_alert_title_low_activity),
                appContext.getString(R.string.health_alert_content_low_activity),
                record.getValuePrimary(), null, record.getSourceType(), 24L * 60L * 60L * 1000L);
    }

    private HealthAlertItem createAlertIfAllowed(long userId, String metricType, String alertKey,
            String alertLevel, String title, String content, Double valuePrimary,
            Double valueSecondary, String sourceType, long cooldownMs) {
        HealthAlertItem latest = repository.getLatestHealthAlertByKey(userId, alertKey);
        if (latest != null && latest.getCreatedAt() > 0L) {
            long delta = System.currentTimeMillis() - latest.getCreatedAt();
            if (delta < cooldownMs) {
                return null;
            }
        }
        LocalResult<HealthAlertItem> result = repository.addHealthAlert(userId, metricType, alertKey,
                alertLevel, title, content, valuePrimary, valueSecondary, sourceType);
        return result.isSuccess() ? result.getData() : null;
    }
}
