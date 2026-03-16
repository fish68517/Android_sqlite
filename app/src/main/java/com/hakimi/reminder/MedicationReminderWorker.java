package com.hakimi.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hakimi.R;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.model.MedicationReminderItem;
import com.hakimi.receiver.MedicationActionReceiver;

public class MedicationReminderWorker extends Worker {

    private static final String CHANNEL_ID = "medication_reminder_channel";

    public MedicationReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        long reminderId = getInputData().getLong(MedicationReminderScheduler.KEY_REMINDER_ID, 0L);
        if (reminderId <= 0L) {
            return Result.success();
        }

        LocalHealthRepository repository = LocalHealthRepository.getInstance(getApplicationContext());
        MedicationReminderItem item = repository.getMedicationReminder(reminderId);
        if (item == null) {
            return Result.success();
        }
        if (!item.isEnabled()) {
            return Result.success();
        }

        ensureChannel();
        sendNotification(reminderId, item);
        MedicationReminderScheduler.scheduleNext(getApplicationContext(), reminderId, item.getReminderTime());
        return Result.success();
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "用药提醒",
                NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);
    }

    private void sendNotification(long reminderId, MedicationReminderItem item) {
        Intent markTakenIntent = new Intent(getApplicationContext(), MedicationActionReceiver.class);
        markTakenIntent.setAction(MedicationActionReceiver.ACTION_MARK_TAKEN);
        markTakenIntent.putExtra(MedicationActionReceiver.EXTRA_REMINDER_ID, reminderId);
        PendingIntent markPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                (int) reminderId,
                markTakenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String content = item.getMedicineName() + " " + item.getDosage();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("用药提醒")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(0, "已服用", markPendingIntent);

        NotificationManagerCompat.from(getApplicationContext())
                .notify((int) (10000 + reminderId), builder.build());
    }
}
