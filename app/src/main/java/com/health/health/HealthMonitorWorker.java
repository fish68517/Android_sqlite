package com.Health.health;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.Health.utils.HealthDebugLogger;
import com.Health.utils.SharedPrefManager;

public class HealthMonitorWorker extends Worker {

    private static final String TAG = "HealthMonitorWorker";

    public HealthMonitorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        long userId = SharedPrefManager.getInstance().getUserId();
        HealthDebugLogger.i(TAG, "doWork start. userId=" + userId);
        if (userId <= 0L) {
            HealthDebugLogger.w(TAG, "doWork skipped because user not logged in");
            return Result.success();
        }
        try {
            HealthDataSyncManager manager = new HealthDataSyncManager(getApplicationContext());
            manager.syncHealthData(userId, true);
            HealthDebugLogger.i(TAG, "doWork success. userId=" + userId);
            return Result.success();
        } catch (Exception e) {
            HealthDebugLogger.e(TAG, "doWork failed. userId=" + userId, e);
            return Result.retry();
        }
    }
}
