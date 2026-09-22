package com.example.sensecheck.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.service.AttendanceForegroundService;
import com.example.sensecheck.util.PermissionUtils;
import com.example.sensecheck.util.ScheduleManager;
import com.example.sensecheck.util.TimeUtils;

public final class ScheduleAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppPreferences preferences = new AppPreferences(context);
        if (preferences.isAutoEnabled()
                && PermissionUtils.hasLocation(context)
                && TimeUtils.isInCourseWindow(preferences, System.currentTimeMillis())) {
            Intent serviceIntent = new Intent(context, AttendanceForegroundService.class);
            serviceIntent.setAction(AttendanceForegroundService.ACTION_START);
            context.startForegroundService(serviceIntent);
        } else {
            ScheduleManager.scheduleNext(context);
        }
    }
}
