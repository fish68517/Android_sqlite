package com.Health.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.Health.local.LocalHealthRepository;

import java.time.LocalDate;

public class MedicationActionReceiver extends BroadcastReceiver {

    public static final String ACTION_MARK_TAKEN = "com.Health.action.MARK_TAKEN";
    public static final String EXTRA_REMINDER_ID = "reminder_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_MARK_TAKEN.equals(intent.getAction())) {
            return;
        }
        long reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0L);
        if (reminderId <= 0L) {
            return;
        }
        LocalHealthRepository.getInstance(context)
                .markMedicationTaken(reminderId, LocalDate.now().toString());
        Toast.makeText(context, "已标记为服用", Toast.LENGTH_SHORT).show();
    }
}
