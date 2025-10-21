package com.example.application.service;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/services/ExportService.java
// 任务: 前台服务 - 级别 3
// 描述: 这是一个前台服务，模拟导出笔记的耗时操作，并在通知栏显示进度。
// =================================================================================


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.application.R;

public class ExportService extends Service {
    private static final String CHANNEL_ID = "ExportChannel";
    private static final int NOTIFICATION_ID = 1;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = createNotification(0);
        startForeground(NOTIFICATION_ID, notification);

        // 模拟耗时任务
        simulateExport();

        return START_NOT_STICKY;
    }

    private void simulateExport() {
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 10) {
                try {
                    Thread.sleep(1000); // 模拟工作
                    updateNotification(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            stopSelf(); // 任务完成，停止服务
        }).start();
    }

    private void updateNotification(int progress) {
        Notification notification = createNotification(progress);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(int progress) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.export_notification_title))
                .setContentText(getString(R.string.export_notification_text))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setProgress(100, progress, false)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Export Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
        }
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}