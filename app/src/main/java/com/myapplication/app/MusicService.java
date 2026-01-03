package com.myapplication.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.myapplication.R;
import com.myapplication.app.activity.MainActivity; // 假设主页是这个

import java.io.IOException;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "com.app.action.PLAY";
    public static final String ACTION_PAUSE = "com.app.action.PAUSE";
    public static final String ACTION_EXIT = "com.app.action.EXIT";

    private MediaPlayer mediaPlayer;
    private String currentTitle = "未播放";
    private String currentAuthor = "";
    private boolean isPaused = false;

    // 广播接收器，用于接收通知栏的点击事件
    private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_PAUSE.equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPaused = true;
                    showNotification(true); // 更新通知显示为“暂停状态”
                } else if (isPaused) {
                    mediaPlayer.start();
                    isPaused = false;
                    showNotification(false);
                }
            } else if (ACTION_EXIT.equals(action)) {
                stopSelf(); // 关闭服务
            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_EXIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(controlReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(controlReceiver, filter);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (ACTION_PLAY.equals(intent.getAction())) {
                String assetPath = intent.getStringExtra("path");
                String title = intent.getStringExtra("title");
                String author = intent.getStringExtra("author");
                playMusic(assetPath, title, author);
            } else if (ACTION_PAUSE.equals(intent.getAction())) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPaused = true;
                    showNotification(true); // 显示“暂停状态”的图标
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void playMusic(String assetPath, String title, String author) {
        try {
            currentTitle = title;
            currentAuthor = author;

            mediaPlayer.reset();
            // 关键：读取 Assets 文件
            AssetFileDescriptor afd = getAssets().openFd(assetPath);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPaused = false;

            showNotification(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(boolean isPauseIcon) {
        String channelId = "music_channel";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Music Player", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }

        // 点击通知跳转回主页
        Intent contentIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingContent = PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_IMMUTABLE);

        // 播放/暂停 Intent
        Intent pauseIntent = new Intent(ACTION_PAUSE);
        PendingIntent pendingPause = PendingIntent.getBroadcast(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        // 关闭 Intent
        Intent exitIntent = new Intent(ACTION_EXIT);
        PendingIntent pendingExit = PendingIntent.getBroadcast(this, 2, exitIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle(currentTitle)
                .setContentText(currentAuthor)
                .setContentIntent(pendingContent)
                .setOngoing(true) // 常驻通知
                .addAction(isPauseIcon ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause,
                        isPauseIcon ? "播放" : "暂停", pendingPause)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "关闭", pendingExit);

        startForeground(1001, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(controlReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}