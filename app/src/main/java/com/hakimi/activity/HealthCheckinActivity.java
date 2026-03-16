package com.hakimi.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.LocalResult;
import com.hakimi.local.model.HabitStatus;
import com.hakimi.local.model.MedicationReminderItem;
import com.hakimi.reminder.MedicationReminderScheduler;
import com.hakimi.utils.SharedPrefManager;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HealthCheckinActivity extends AppCompatActivity {

    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;

    private EditText etHabitName;
    private LinearLayout layoutHabits;
    private EditText etMedName;
    private EditText etMedDosage;
    private EditText etMedTime;
    private TextView tvReminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_checkin);

        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();

        bindViews();
        bindEvents();
        renderHabits();
        renderReminderList();
    }

    private void bindViews() {
        etHabitName = findViewById(R.id.et_habit_name);
        layoutHabits = findViewById(R.id.layout_habits);
        etMedName = findViewById(R.id.et_med_name);
        etMedDosage = findViewById(R.id.et_med_dosage);
        etMedTime = findViewById(R.id.et_med_time);
        tvReminderList = findViewById(R.id.tv_reminder_list);
    }

    private void bindEvents() {
        findViewById(R.id.btn_open_reminder_center).setOnClickListener(v ->
                startActivity(new Intent(this, ReminderCenterActivity.class)));
        findViewById(R.id.btn_water).setOnClickListener(v ->
                addQuickRecord("WATER", "1 cup", "Quick water checkin"));
        findViewById(R.id.btn_medicine_checkin).setOnClickListener(v ->
                addQuickRecord("MEDICINE", "Taken", "Quick medicine checkin"));
        findViewById(R.id.btn_weight).setOnClickListener(v -> showWeightInputDialog());

        findViewById(R.id.btn_add_habit).setOnClickListener(v -> addHabit());
        findViewById(R.id.btn_pick_med_time).setOnClickListener(v -> pickReminderTime());
        findViewById(R.id.btn_add_med_reminder).setOnClickListener(v -> addMedicationReminder());
    }

    private void addQuickRecord(String type, String value, String note) {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        LocalResult<Void> result = repository.addQuickRecord(userId, type, value, note);
        Toast.makeText(this, result.isSuccess() ? "Saved" : result.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showWeightInputDialog() {
        EditText editText = new EditText(this);
        editText.setHint("Input weight, e.g. 62.5");
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Weight record")
                .setView(editText)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (d, w) -> {
                    String weight = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(weight)) {
                        Toast.makeText(this, "Please input weight", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addQuickRecord("WEIGHT", weight + "kg", "Weight record");
                })
                .show();
    }

    private void addHabit() {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = etHabitName.getText().toString().trim();
        LocalResult<Long> result = repository.addHabit(userId, name);
        if (!result.isSuccess()) {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        etHabitName.setText("");
        renderHabits();
        Toast.makeText(this, "Habit added", Toast.LENGTH_SHORT).show();
    }

    private void renderHabits() {
        long userId = getCurrentUserId();
        layoutHabits.removeAllViews();
        if (userId <= 0L) {
            return;
        }
        List<HabitStatus> habits = repository.listHabitsWithStatus(userId);
        if (habits.isEmpty()) {
            TextView empty = buildHabitText("No habit");
            layoutHabits.addView(empty);
            return;
        }
        for (HabitStatus habit : habits) {
            layoutHabits.addView(buildHabitRow(habit));
        }
    }

    private View buildHabitRow(HabitStatus habit) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 12, 0, 12);

        TextView tvName = buildHabitText(habit.getHabitName());
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameParams);

        Button btn = new Button(this);
        btn.setText(habit.isCheckedToday() ? "Checked today" : "Checkin");
        btn.setEnabled(!habit.isCheckedToday());
        btn.setOnClickListener(v -> {
            LocalResult<Void> result = repository.checkinHabit(getCurrentUserId(), habit.getHabitId());
            Toast.makeText(this, result.isSuccess() ? "Checked" : result.getMessage(), Toast.LENGTH_SHORT).show();
            renderHabits();
        });

        row.addView(tvName);
        row.addView(btn);
        return row;
    }

    private TextView buildHabitText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF333333);
        tv.setTextSize(14f);
        return tv;
    }

    private void pickReminderTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> etMedTime.setText(
                        String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    private void addMedicationReminder() {
        long userId = getCurrentUserId();
        if (userId <= 0L) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        String medName = etMedName.getText().toString().trim();
        String dosage = etMedDosage.getText().toString().trim();
        String time = etMedTime.getText().toString().trim();
        if (TextUtils.isEmpty(medName) || TextUtils.isEmpty(dosage) || TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please complete reminder fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!time.matches("^\\d{2}:\\d{2}$")) {
            Toast.makeText(this, "Time format must be HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<Long> result = repository.addMedicationReminder(userId, medName, dosage, time);
        if (!result.isSuccess() || result.getData() == null) {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        MedicationReminderScheduler.scheduleNext(this, result.getData(), time);
        etMedName.setText("");
        etMedDosage.setText("");
        etMedTime.setText("");
        renderReminderList();
        Toast.makeText(this, "Reminder created", Toast.LENGTH_SHORT).show();
    }

    private void renderReminderList() {
        long userId = getCurrentUserId();
        List<MedicationReminderItem> reminders = repository.listEnabledMedicationReminders(userId);
        if (reminders.isEmpty()) {
            tvReminderList.setText("No reminders");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < reminders.size(); i++) {
            MedicationReminderItem item = reminders.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(item.getMedicineName())
                    .append(" ")
                    .append(item.getDosage())
                    .append(" @ ")
                    .append(item.getReminderTime())
                    .append('\n');
        }
        tvReminderList.setText(builder.toString().trim());
    }

    private long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
        }
        return 0L;
    }
}
