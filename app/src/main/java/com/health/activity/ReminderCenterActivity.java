package com.Health.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.Health.HealthApplication;
import com.Health.R;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.local.model.MedicationReminderItem;
import com.Health.reminder.MedicationReminderScheduler;
import com.Health.utils.SharedPrefManager;

import java.util.List;

public class ReminderCenterActivity extends AppCompatActivity {

    private LinearLayout layoutReminders;
    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_center);

        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();
        layoutReminders = findViewById(R.id.layout_reminders);
        renderReminders();
    }

    private void renderReminders() {
        layoutReminders.removeAllViews();
        long userId = getCurrentUserId();
        List<MedicationReminderItem> list = repository.listAllMedicationReminders(userId);
        if (list.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No medication reminders");
            tv.setTextColor(0xFF666666);
            tv.setTextSize(14f);
            layoutReminders.addView(tv);
            return;
        }

        for (MedicationReminderItem item : list) {
            layoutReminders.addView(buildReminderRow(item));
        }
    }

    private View buildReminderRow(MedicationReminderItem item) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 14, 0, 14);

        TextView title = new TextView(this);
        title.setText(item.getMedicineName() + " " + item.getDosage());
        title.setTextColor(0xFF222222);
        title.setTextSize(16f);

        TextView sub = new TextView(this);
        sub.setText("Reminder time: " + item.getReminderTime());
        sub.setTextColor(0xFF666666);
        sub.setTextSize(13f);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setPadding(0, 8, 0, 0);

        Switch enableSwitch = new Switch(this);
        enableSwitch.setText(item.isEnabled() ? "Enabled" : "Disabled");
        enableSwitch.setChecked(item.isEnabled());
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LocalResult<Void> result = repository.setMedicationReminderEnabled(item.getId(), isChecked);
            if (!result.isSuccess()) {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                buttonView.setChecked(!isChecked);
                return;
            }
            if (isChecked) {
                MedicationReminderScheduler.scheduleNext(this, item.getId(), item.getReminderTime());
            } else {
                MedicationReminderScheduler.cancel(this, item.getId());
            }
            buttonView.setText(isChecked ? "Enabled" : "Disabled");
        });

        Button btnDelete = new Button(this);
        btnDelete.setText("Delete");
        btnDelete.setOnClickListener(v -> confirmDelete(item));

        actions.addView(enableSwitch);
        actions.addView(btnDelete);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(0xFFEDEDED);

        container.addView(title);
        container.addView(sub);
        container.addView(actions);
        container.addView(divider);
        return container;
    }

    private void confirmDelete(MedicationReminderItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete reminder")
                .setMessage("Delete this medication reminder?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    LocalResult<Void> result = repository.deleteMedicationReminder(item.getId());
                    if (result.isSuccess()) {
                        MedicationReminderScheduler.cancel(this, item.getId());
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        renderReminders();
                    } else {
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HealthApplication.curUser != null && HealthApplication.curUser.getId() != null) {
            return HealthApplication.curUser.getId();
        }
        return 0L;
    }
}
