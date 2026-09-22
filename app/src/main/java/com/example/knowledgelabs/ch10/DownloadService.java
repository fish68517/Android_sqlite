package com.example.knowledgelabs.ch10;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.knowledgelabs.R;

public class DownloadService extends Service {
    public static final String ACTION_PROGRESS = "com.example.knowledgelabs.DOWNLOAD_PROGRESS";
    public static final String ACTION_STOP = "com.example.knowledgelabs.STOP_DOWNLOAD";
    public static final String EXTRA_PROGRESS = "progress";
    private static final String CHANNEL_ID = "download_demo";
    private static final int NOTIFICATION_ID = 10;
    private volatile boolean running;
    private Thread worker;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "后台下载实验", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("显示模拟下载任务的前台进度");
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopDownload();
            return START_NOT_STICKY;
        }
        startForeground(NOTIFICATION_ID, buildNotification(0, "准备下载"));
        if (!running) {
            running = true;
            worker = new Thread(this::runDownload, "demo-downloader");
            worker.start();
        }
        return START_NOT_STICKY;
    }

    private void runDownload() {
        for (int progress = 0; running && progress <= 100; progress += 2) {
            int currentProgress = progress;
            mainHandler.post(() -> publishProgress(currentProgress));
            if (progress == 100) break;
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        running = false;
        mainHandler.post(() -> {
            stopForeground(STOP_FOREGROUND_DETACH);
            stopSelf();
        });
    }

    private void publishProgress(int progress) {
        String text = progress == 100 ? "下载完成" : "正在下载 " + progress + "%";
        getSystemService(NotificationManager.class)
                .notify(NOTIFICATION_ID, buildNotification(progress, text));
        Intent update = new Intent(ACTION_PROGRESS)
                .setPackage(getPackageName())
                .putExtra(EXTRA_PROGRESS, progress);
        sendBroadcast(update);
    }

    private Notification buildNotification(int progress, String text) {
        return new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("BackgroundDownloader")
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setOngoing(progress < 100)
                .setProgress(100, progress, false)
                .build();
    }

    private void stopDownload() {
        running = false;
        if (worker != null) worker.interrupt();
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        running = false;
        mainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
