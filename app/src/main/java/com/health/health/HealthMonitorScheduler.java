package com.Health.health;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class HealthMonitorScheduler {

    private HealthMonitorScheduler() {
    }

    public static void schedulePeriodic(Context context) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                HealthMonitorWorker.class, 1, TimeUnit.HOURS)
                .addTag(HealthConstants.WORK_NAME_HEALTH_MONITOR)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                HealthConstants.WORK_NAME_HEALTH_MONITOR,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );
    }

    public static void scheduleImmediate(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(HealthMonitorWorker.class)
                .addTag(HealthConstants.WORK_NAME_HEALTH_MONITOR_IMMEDIATE)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                HealthConstants.WORK_NAME_HEALTH_MONITOR_IMMEDIATE,
                ExistingWorkPolicy.REPLACE,
                request
        );
    }
}
