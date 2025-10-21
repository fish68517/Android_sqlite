package com.example.application;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/applicationApplication.java
// 描述: Application类，用于初始化一些全局任务，例如WorkManager。
// =================================================================================


import android.app.Application;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.application.service.BackupWorker;

import java.util.concurrent.TimeUnit;

public class GeekNotesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupDailyBackup();
    }

    private void setupDailyBackup() {
        // 任务: 用于延迟任务的 WorkManager - 级别 3
        // 描述: 在应用启动时，安排一个每日重复的后台备份任务。
        PeriodicWorkRequest backupRequest =
                new PeriodicWorkRequest.Builder(BackupWorker.class, 1, TimeUnit.DAYS)
                        .build();

        WorkManager.getInstance(this).enqueue(backupRequest);
    }
}