package com.example.sensecheck.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.receiver.ScheduleAlarmReceiver;

public final class ScheduleManager {
    private static final int REQUEST_CODE = 4102;

    private ScheduleManager() {
    }

    public static void scheduleNext(Context context) {
        AppPreferences preferences = new AppPreferences(context);
        if (!preferences.isAutoEnabled() || !preferences.isCourseConfigured()) {
            cancel(context);
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        long triggerAt = TimeUtils.nextCourseStart(preferences, System.currentTimeMillis());
        alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent(context));
    }

    public static void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent(context));
        }
    }

    private static PendingIntent pendingIntent(Context context) {
        Intent intent = new Intent(context, ScheduleAlarmReceiver.class);
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}

