package com.Health.health;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.Health.R;
import com.Health.activity.HealthDashboardActivity;
import com.Health.local.model.HealthAlertItem;

public final class HealthAlertNotifier {

    private HealthAlertNotifier() {
    }

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                HealthConstants.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.health_alert_channel_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription(context.getString(R.string.health_alert_channel_desc));
        manager.createNotificationChannel(channel);
    }

    public static void notifyAlert(Context context, HealthAlertItem alert) {
        ensureChannel(context);
        Intent intent = new Intent(context, HealthDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) alert.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, HealthConstants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(alert.getTitle())
                .setContentText(alert.getContent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(alert.getContent()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(context).notify((int) (50000 + alert.getId()), builder.build());
    }
}
