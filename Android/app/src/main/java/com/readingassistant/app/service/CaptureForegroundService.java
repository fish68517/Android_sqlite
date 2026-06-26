package com.readingassistant.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import com.readingassistant.app.R;
import com.readingassistant.app.activity.MainActivity;
import com.readingassistant.app.capture.MediaProjectionController;
import com.readingassistant.app.scheduler.CaptureCoordinator;
import com.readingassistant.app.util.DebugLog;

public class CaptureForegroundService extends Service {
    public static final String ACTION_START = "com.readingassistant.app.action.START";
    public static final String ACTION_STOP = "com.readingassistant.app.action.STOP";
    private static final String EXTRA_RESULT_CODE = "extra_result_code";
    private static final String EXTRA_RESULT_DATA = "extra_result_data";
    private static final String CHANNEL_ID = "reading_assistant_foreground";
    private static final int NOTIFICATION_ID = 1101;

    private MediaProjectionController mediaProjectionController;
    private CaptureCoordinator coordinator;

    public static Intent createStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, CaptureForegroundService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_RESULT_CODE, resultCode);
        intent.putExtra(EXTRA_RESULT_DATA, data);
        return intent;
    }

    public static Intent createStopIntent(Context context) {
        Intent intent = new Intent(context, CaptureForegroundService.class);
        intent.setAction(ACTION_STOP);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.d("ForegroundService onCreate");
        createNotificationChannel();
        mediaProjectionController = new MediaProjectionController(this);
        coordinator = CaptureCoordinator.getInstance();
        coordinator.init(getApplicationContext());
        coordinator.setMediaProjectionController(mediaProjectionController);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.d("ForegroundService onStartCommand action="
                + (intent == null ? "null" : intent.getAction())
                + " startId=" + startId);
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        startForegroundCompat();
        if (intent != null && ACTION_START.equals(intent.getAction())) {
            int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
            Intent resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
            DebugLog.d("ForegroundService start action resultCode=" + resultCode
                    + " resultDataNull=" + (resultData == null));
            if (resultData != null) {
                mediaProjectionController.start(resultCode, resultData);
            }
            coordinator.setRunning(true);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        DebugLog.d("ForegroundService onDestroy");
        coordinator.shutdown();
        mediaProjectionController.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundCompat() {
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            DebugLog.d("ForegroundService startForeground with mediaProjection type");
            startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            );
        } else {
            DebugLog.d("ForegroundService startForeground legacy");
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private Notification buildNotification() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                1,
                activityIntent,
                pendingIntentFlags()
        );
        PendingIntent stopIntent = PendingIntent.getService(
                this,
                2,
                createStopIntent(this),
                pendingIntentFlags()
        );
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.notification_stop), stopIntent);
        return builder.build();
    }

    private int pendingIntentFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        }
        return PendingIntent.FLAG_UPDATE_CURRENT;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            DebugLog.w("Notification channel skipped: manager is null");
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
        );
        manager.createNotificationChannel(channel);
        DebugLog.d("Notification channel ensured id=" + CHANNEL_ID);
    }
}
