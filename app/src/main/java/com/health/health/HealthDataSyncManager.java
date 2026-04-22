package com.Health.health;

import android.content.Context;
import android.content.SharedPreferences;

import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.local.model.HealthAlertItem;
import com.Health.local.model.HealthAlertRule;
import com.Health.local.model.HealthMetricRecord;
import com.Health.utils.HealthDebugLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HealthDataSyncManager {

    private static final String TAG = "HealthDataSync";

    private final Context appContext;
    private final LocalHealthRepository repository;
    private final HealthConnectService healthConnectService;
    private final HealthAlertEngine alertEngine;

    public HealthDataSyncManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.repository = LocalHealthRepository.getInstance(appContext);
        this.healthConnectService = new HealthConnectService(appContext);
        this.alertEngine = new HealthAlertEngine(appContext);
    }

    public HealthSyncResult syncHealthData(long userId, boolean allowBackgroundRead) {
        HealthSyncResult result = new HealthSyncResult();
        result.healthConnectAvailable = healthConnectService.isAvailable();
        result.permissionsGranted = healthConnectService.hasBasicPermissions();
        result.backgroundPermissionGranted = healthConnectService.hasBackgroundPermission();
        HealthDebugLogger.i(TAG, "syncHealthData start. userId=" + userId
                + ", allowBackgroundRead=" + allowBackgroundRead
                + ", healthConnectAvailable=" + result.healthConnectAvailable
                + ", permissionsGranted=" + result.permissionsGranted
                + ", backgroundPermissionGranted=" + result.backgroundPermissionGranted);

        if (userId <= 0L) {
            result.statusMessage = "Please login first";
            saveSyncStatus(result);
            HealthDebugLogger.w(TAG, "syncHealthData aborted because user not logged in");
            return result;
        }

        try {
            if (result.healthConnectAvailable && result.permissionsGranted) {
                HealthConnectService.HealthConnectReadResult healthResult =
                        healthConnectService.readLatestData(allowBackgroundRead);
                result.backgroundPermissionGranted = healthResult.backgroundPermissionGranted;
                persistHealthConnectMetrics(userId, healthResult, result);
                result.statusMessage = healthResult.message;
                HealthDebugLogger.i(TAG, "syncHealthData read success. stepSampleCount="
                        + healthResult.stepSamples.size()
                        + ", heartRateDailyCount=" + healthResult.heartRateDailySamples.size()
                        + ", latestHeartRate=" + describeMetric(healthResult.latestHeartRate));
            } else if (!result.healthConnectAvailable) {
                result.statusMessage = "Health Connect unavailable";
                HealthDebugLogger.w(TAG, "syncHealthData found Health Connect unavailable");
            } else {
                result.statusMessage = "Health permissions required";
                HealthDebugLogger.w(TAG, "syncHealthData missing Health Connect permissions");
            }
        } catch (Exception e) {
            result.statusMessage = "Sync failed: " + e.getMessage();
            HealthDebugLogger.e(TAG, "syncHealthData threw exception", e);
        }

        HealthAlertRule rule = repository.getHealthAlertRule(userId);
        result.latestStep = repository.getLatestHealthMetric(userId, HealthConstants.METRIC_STEP,
                HealthConstants.SCOPE_DAILY);
        result.latestHeartRate = repository.getLatestHealthMetric(userId, HealthConstants.METRIC_HEART_RATE,
                HealthConstants.SCOPE_LATEST);
        if (result.latestHeartRate == null) {
            result.latestHeartRate = repository.getLatestHealthMetric(userId, HealthConstants.METRIC_HEART_RATE,
                    HealthConstants.SCOPE_DAILY);
        }
        result.latestBloodPressure = repository.getLatestHealthMetric(userId, HealthConstants.METRIC_BLOOD_PRESSURE,
                HealthConstants.SCOPE_INSTANT);
        result.newAlerts.addAll(alertEngine.evaluate(userId, rule, result.latestStep,
                result.latestHeartRate, result.latestBloodPressure));
        HealthDebugLogger.i(TAG, "syncHealthData end. status=" + result.statusMessage
                + ", syncedStepDays=" + result.syncedStepDays
                + ", syncedHeartRateDays=" + result.syncedHeartRateDays
                + ", latestStep=" + describeMetric(result.latestStep)
                + ", latestHeartRate=" + describeMetric(result.latestHeartRate)
                + ", latestBloodPressure=" + describeMetric(result.latestBloodPressure)
                + ", alertCount=" + result.newAlerts.size());

        saveSyncStatus(result);
        return result;
    }

    public List<HealthAlertItem> evaluateLocalAlerts(long userId) {
        HealthAlertRule rule = repository.getHealthAlertRule(userId);
        HealthMetricRecord latestStep = repository.getLatestHealthMetric(userId, HealthConstants.METRIC_STEP,
                HealthConstants.SCOPE_DAILY);
        HealthMetricRecord latestHeartRate = repository.getLatestHealthMetric(userId, HealthConstants.METRIC_HEART_RATE,
                HealthConstants.SCOPE_LATEST);
        HealthMetricRecord latestBloodPressure = repository.getLatestHealthMetric(userId,
                HealthConstants.METRIC_BLOOD_PRESSURE, HealthConstants.SCOPE_INSTANT);
        List<HealthAlertItem> alerts = alertEngine.evaluate(userId, rule, latestStep, latestHeartRate, latestBloodPressure);
        HealthDebugLogger.i(TAG, "evaluateLocalAlerts. userId=" + userId
                + ", latestStep=" + describeMetric(latestStep)
                + ", latestHeartRate=" + describeMetric(latestHeartRate)
                + ", latestBloodPressure=" + describeMetric(latestBloodPressure)
                + ", alertCount=" + alerts.size());
        return alerts;
    }

    public long getLastSyncAt() {
        SharedPreferences preferences = appContext.getSharedPreferences(
                HealthConstants.PREF_HEALTH_MONITOR, Context.MODE_PRIVATE);
        return preferences.getLong(HealthConstants.KEY_LAST_SYNC_AT, 0L);
    }

    public String getLastSyncMessage() {
        SharedPreferences preferences = appContext.getSharedPreferences(
                HealthConstants.PREF_HEALTH_MONITOR, Context.MODE_PRIVATE);
        return preferences.getString(HealthConstants.KEY_LAST_SYNC_MESSAGE, "Never synced");
    }

    private void persistHealthConnectMetrics(long userId, HealthConnectService.HealthConnectReadResult healthResult,
            HealthSyncResult syncResult) {
        for (HealthConnectService.DailySample sample : healthResult.stepSamples) {
            LocalResult<HealthMetricRecord> saveResult = repository.upsertDailyHealthMetric(userId,
                    HealthConstants.METRIC_STEP, sample.valuePrimary, sample.valueSecondary,
                    sample.unit, sample.sourceType, sample.sampleTime, sample.sampleDay);
            if (saveResult.isSuccess()) {
                syncResult.syncedStepDays++;
            }
        }

        for (HealthConnectService.DailySample sample : healthResult.heartRateDailySamples) {
            LocalResult<HealthMetricRecord> saveResult = repository.upsertDailyHealthMetric(userId,
                    HealthConstants.METRIC_HEART_RATE, sample.valuePrimary, sample.valueSecondary,
                    sample.unit, sample.sourceType, sample.sampleTime, sample.sampleDay);
            if (saveResult.isSuccess()) {
                syncResult.syncedHeartRateDays++;
            }
        }

        if (healthResult.latestHeartRate != null) {
            repository.addInstantHealthMetric(userId, HealthConstants.METRIC_HEART_RATE,
                    HealthConstants.SCOPE_LATEST, healthResult.latestHeartRate.valuePrimary,
                    healthResult.latestHeartRate.valueSecondary, healthResult.latestHeartRate.unit,
                    healthResult.latestHeartRate.sourceType, healthResult.latestHeartRate.sampleTime,
                    healthResult.latestHeartRate.sampleDay);
        }
        HealthDebugLogger.d(TAG, "persistHealthConnectMetrics finished. userId=" + userId
                + ", syncedStepDays=" + syncResult.syncedStepDays
                + ", syncedHeartRateDays=" + syncResult.syncedHeartRateDays
                + ", latestHeartRate=" + describeMetric(healthResult.latestHeartRate));
    }

    private void saveSyncStatus(HealthSyncResult result) {
        SharedPreferences preferences = appContext.getSharedPreferences(
                HealthConstants.PREF_HEALTH_MONITOR, Context.MODE_PRIVATE);
        preferences.edit()
                .putLong(HealthConstants.KEY_LAST_SYNC_AT, System.currentTimeMillis())
                .putString(HealthConstants.KEY_LAST_SYNC_MESSAGE, result.statusMessage == null ? "" : result.statusMessage)
                .apply();
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
                + ",sampleDay=" + record.getSampleDay();
    }

    private String describeMetric(HealthConnectService.DailySample sample) {
        if (sample == null) {
            return "null";
        }
        return "primary=" + sample.valuePrimary
                + ",secondary=" + sample.valueSecondary
                + ",unit=" + sample.unit
                + ",source=" + sample.sourceType
                + ",sampleDay=" + sample.sampleDay
                + ",sampleTime=" + sample.sampleTime;
    }

    public static class HealthSyncResult {
        public boolean healthConnectAvailable;
        public boolean permissionsGranted;
        public boolean backgroundPermissionGranted;
        public String statusMessage;
        public int syncedStepDays;
        public int syncedHeartRateDays;
        public final List<HealthAlertItem> newAlerts = new ArrayList<>();
        public HealthMetricRecord latestStep;
        public HealthMetricRecord latestHeartRate;
        public HealthMetricRecord latestBloodPressure;
    }
}
