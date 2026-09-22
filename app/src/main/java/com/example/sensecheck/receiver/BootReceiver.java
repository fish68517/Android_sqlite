package com.example.sensecheck.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sensecheck.util.ScheduleManager;

public final class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleManager.scheduleNext(context);
    }
}

