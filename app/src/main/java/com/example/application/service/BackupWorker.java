package com.example.application.service;// =================================================================================

// 任务: 用于延迟任务的 WorkManager - 级别 3
// 描述: 这是一个Worker，用于执行后台的、可延迟的同步/备份任务。
// =================================================================================


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackupWorker extends Worker {
    private static final String TAG = "BackupWorker";

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 我们用一条日志来模拟这个过程。备份逻辑
        Log.d(TAG, "正在执行每日笔记备份...");

        // 模拟工作
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            return Result.failure();
        }

        Log.d(TAG, "每日备份任务完成。");
        return Result.success();
    }
}
