package com.hakimi.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hakimi.R;
import com.hakimi.activity.MainActivity;

public class ExerciseReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "exercise_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String courseName = intent.getStringExtra("course_name");
        String weekday = intent.getStringExtra("weekday");
        String message = intent.getStringExtra("message");
        if (courseName == null) {
            courseName = "\u8bfe\u7a0b";
        }
        if (weekday == null) {
            weekday = "";
        }
        if (message == null || message.trim().isEmpty()) {
            message = weekday + " " + courseName + " \u4e4b\u540e\u8bb0\u5f97\u8d77\u6765\u8d70\u8d70\uff0c\u505a\u4e00\u4f1a\u8fd0\u52a8";
        }

        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.putExtra("target_fragment", "HomeFragment");
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "\u8fd0\u52a8\u63d0\u9192";
        String content = message;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } else {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }
        }
        Toast.makeText(context, "\u4e0b\u8bfe\u63d0\u9192\uff1a" + courseName, Toast.LENGTH_SHORT).show();
    }
}
