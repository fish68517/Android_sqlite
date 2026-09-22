package com.example.sensecheck.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.sensecheck.R;
import com.example.sensecheck.checkin.CheckinCoordinator;
import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.data.CheckinRecord;
import com.example.sensecheck.ui.MainActivity;
import com.example.sensecheck.util.ScheduleManager;
import com.example.sensecheck.util.TimeUtils;

public final class AttendanceForegroundService extends Service {
    public static final String ACTION_START = "com.example.sensecheck.action.START_ATTENDANCE";
    public static final String ACTION_STOP = "com.example.sensecheck.action.STOP_ATTENDANCE";
    public static final String ACTION_RECORD_CREATED = "com.example.sensecheck.action.RECORD_CREATED";
    private static final String CHANNEL_ID = "attendance_channel";
    private static final int NOTIFICATION_ID = 1001;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private AppPreferences preferences;
    private CheckinCoordinator coordinator;
    private boolean collecting;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new AppPreferences(this);
        coordinator = new CheckinCoordinator(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopAttendance();
            return START_NOT_STICKY;
        }

        startForeground(NOTIFICATION_ID, buildNotification("自动签到服务已启动"));
        handler.removeCallbacks(checkinRunnable);
        handler.post(checkinRunnable);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private final Runnable checkinRunnable = new Runnable() {
        @Override
        public void run() {
            if (!preferences.isCourseConfigured() || !preferences.isProfileConfigured()) {
                updateNotification("请先完成个人信息和课程设置");
                stopAttendance();
                return;
            }
            if (!TimeUtils.isInCourseWindow(preferences, System.currentTimeMillis())) {
                updateNotification("当前不在课程时间，等待下次课程");
                stopAttendance();
                return;
            }
            if (collecting) {
                return;
            }
            collecting = true;
            updateNotification("正在采集位置和周边环境…");
            coordinator.performCheckin(new CheckinCoordinator.Callback() {
                @Override
                public void onCompleted(CheckinRecord record) {
                    collecting = false;
                    updateNotification(record.getResult() + " · " + record.getReason());
                    Intent broadcast = new Intent(ACTION_RECORD_CREATED);
                    broadcast.setPackage(getPackageName());
                    sendBroadcast(broadcast);
                    scheduleNextSample();
                }

                @Override
                public void onError(String message) {
                    collecting = false;
                    updateNotification(message);
                    scheduleNextSample();
                }
            });
        }
    };

    private void scheduleNextSample() {
        if (TimeUtils.isInCourseWindow(preferences, System.currentTimeMillis())) {
            long delay = Math.max(1, preferences.getIntervalMinutes()) * 60_000L;
            handler.postDelayed(checkinRunnable, delay);
        } else {
            stopAttendance();
        }
    }

    private void stopAttendance() {
        handler.removeCallbacksAndMessages(null);
        ScheduleManager.scheduleNext(this);
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void createNotificationChannel() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string.notification_channel_description));
        channel.enableLights(false);
        channel.enableVibration(false);
        manager.createNotificationChannel(channel);
    }

    private Notification buildNotification(String content) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, AttendanceForegroundService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                1,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("无感签到")
                .setContentText(content)
                .setColor(Color.rgb(49, 87, 213))
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .addAction(new Notification.Action.Builder(null, "停止", stopPendingIntent).build())
                .build();
    }

    private void updateNotification(String content) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(content));
        }
    }
}

