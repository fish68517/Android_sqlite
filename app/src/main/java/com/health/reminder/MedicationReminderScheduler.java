package com.Health.reminder;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class MedicationReminderScheduler {

    public static final String KEY_REMINDER_ID = "reminder_id";

    private MedicationReminderScheduler() {
    }

    public static void scheduleNext(Context context, long reminderId, String hhmm) {
        cancel(context, reminderId);

        String[] hm = hhmm.split(":");
        int hour = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
        if (!next.isAfter(now)) {
            next = next.plusDays(1);
        }

        long delay = Duration.between(now, next).toMinutes();
        if (delay < 1) {
            delay = 1;
        }

        Data data = new Data.Builder()
                .putLong(KEY_REMINDER_ID, reminderId)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MedicationReminderWorker.class)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .addTag(workTag(reminderId))
                .build();
        WorkManager.getInstance(context).enqueue(request);
    }

    public static void cancel(Context context, long reminderId) {
        WorkManager.getInstance(context).cancelAllWorkByTag(workTag(reminderId));
    }

    private static String workTag(long reminderId) {
        return "medication_reminder_" + reminderId;
    }
}
