package com.hakimi.ui.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.db.ClassScheduleDbHelper;
import com.hakimi.model.ClassSchedule;
import com.hakimi.receiver.ExerciseReminderReceiver;
import com.hakimi.ui.adapter.ClassScheduleAdapter;
import com.hakimi.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassScheduleActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "exercise_reminder_channel";
    private static final int REQ_NOTIFICATION = 3010;

    private EditText etCourseName;
    private EditText etLocation;
    private Spinner spWeekday;
    private EditText etStartTime;
    private EditText etEndTime;
    private Button btnPickStart;
    private Button btnPickEnd;
    private Button btnSave;
    private RecyclerView rvSchedules;

    private ClassScheduleDbHelper dbHelper;
    private ClassScheduleAdapter adapter;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);

        dbHelper = new ClassScheduleDbHelper(this);
        sharedPrefManager = SharedPrefManager.getInstance();

        initViews();
        setupWeekdaySpinner();
        setupList();
        setupListeners();
        ensureNotificationChannel();
        ensureNotificationPermission();
        loadSchedulesFromDb();
    }

    private void initViews() {
        etCourseName = findViewById(R.id.et_schedule_course_name);
        etLocation = findViewById(R.id.et_schedule_location);
        spWeekday = findViewById(R.id.sp_schedule_weekday);
        etStartTime = findViewById(R.id.et_schedule_start_time);
        etEndTime = findViewById(R.id.et_schedule_end_time);
        btnPickStart = findViewById(R.id.btn_pick_start_time);
        btnPickEnd = findViewById(R.id.btn_pick_end_time);
        btnSave = findViewById(R.id.btn_save_schedule_db);
        rvSchedules = findViewById(R.id.rv_schedule_list);
    }

    private void setupWeekdaySpinner() {
        List<String> days = new ArrayList<>();
        days.add("\u5468\u4e00");
        days.add("\u5468\u4e8c");
        days.add("\u5468\u4e09");
        days.add("\u5468\u56db");
        days.add("\u5468\u4e94");
        days.add("\u5468\u516d");
        days.add("\u5468\u65e5");

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWeekday.setAdapter(dayAdapter);
    }

    private void setupList() {
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassScheduleAdapter();
        rvSchedules.setAdapter(adapter);
    }

    private void setupListeners() {
        btnPickStart.setOnClickListener(v -> pickTime(etStartTime));
        btnPickEnd.setOnClickListener(v -> pickTime(etEndTime));
        btnSave.setOnClickListener(v -> saveScheduleToDb());
    }

    private void pickTime(EditText target) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    target.setText(time);
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    private void saveScheduleToDb() {
        long userId = currentUserId();
        if (userId <= 0) {
            Toast.makeText(this, "\u672a\u83b7\u53d6\u5230\u5f53\u524d\u7528\u6237", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etCourseName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String start = etStartTime.getText().toString().trim();
        String end = etEndTime.getText().toString().trim();
        int weekday = spWeekday.getSelectedItemPosition() + 1;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            Toast.makeText(this, "\u8bf7\u586b\u5199\u8bfe\u7a0b\u540d\u548c\u65f6\u95f4", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!start.matches("^\\d{2}:\\d{2}$") || !end.matches("^\\d{2}:\\d{2}$")) {
            Toast.makeText(this, "\u65f6\u95f4\u683c\u5f0f\u8bf7\u4f7f\u7528 HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassSchedule schedule = new ClassSchedule();
        schedule.setUserId(userId);
        schedule.setCourseName(name);
        schedule.setWeekday(weekday);
        schedule.setStartTime(start);
        schedule.setEndTime(end);
        schedule.setLocation(location);

        long newId = dbHelper.insert(schedule);
        if (newId <= 0) {
            Toast.makeText(this, "\u4fdd\u5b58\u5931\u8d25", Toast.LENGTH_SHORT).show();
            return;
        }

        schedule.setId(newId);
        scheduleExerciseReminder(schedule);
        Toast.makeText(this, "\u8bfe\u8868\u5df2\u4fdd\u5b58\uff0c\u5df2\u8bbe\u7f6e\u4e0b\u8bfe\u63d0\u9192", Toast.LENGTH_SHORT).show();
        clearInput();
        loadSchedulesFromDb();
    }

    private void loadSchedulesFromDb() {
        long userId = currentUserId();
        if (userId <= 0) {
            adapter.setItems(new ArrayList<>());
            return;
        }
        List<ClassSchedule> schedules = dbHelper.queryByUser(userId);
        adapter.setItems(schedules);
    }

    private void clearInput() {
        etCourseName.setText("");
        etLocation.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        spWeekday.setSelection(0);
    }

    private void scheduleExerciseReminder(ClassSchedule schedule) {
        String[] hm = schedule.getEndTime().split(":");
        int hour = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);

        Calendar trigger = Calendar.getInstance();
        trigger.set(Calendar.DAY_OF_WEEK, weekdayToCalendar(schedule.getWeekday()));
        trigger.set(Calendar.HOUR_OF_DAY, hour);
        trigger.set(Calendar.MINUTE, minute);
        trigger.set(Calendar.SECOND, 0);
        trigger.set(Calendar.MILLISECOND, 0);
        if (trigger.getTimeInMillis() <= System.currentTimeMillis()) {
            trigger.add(Calendar.DAY_OF_YEAR, 7);
        }

        Intent intent = new Intent(this, ExerciseReminderReceiver.class);
        intent.putExtra("course_name", schedule.getCourseName());
        intent.putExtra("weekday", weekdayLabel(schedule.getWeekday()));
        intent.putExtra("message", "\u8bfe\u7a0b\u5df2\u4e0b\u8bfe\uff0c\u8bb0\u5f97\u4f11\u606f\u548c\u8fd0\u52a8\u4e00\u4f1a");

        int requestCode = (schedule.getUserId() + "_" + schedule.getCourseName() + "_"
                + schedule.getWeekday() + "_" + schedule.getEndTime()).hashCode();
        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    trigger.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pi
            );
        }
    }

    private int weekdayToCalendar(int weekday) {
        switch (weekday) {
            case 1:
                return Calendar.MONDAY;
            case 2:
                return Calendar.TUESDAY;
            case 3:
                return Calendar.WEDNESDAY;
            case 4:
                return Calendar.THURSDAY;
            case 5:
                return Calendar.FRIDAY;
            case 6:
                return Calendar.SATURDAY;
            case 7:
                return Calendar.SUNDAY;
            default:
                return Calendar.MONDAY;
        }
    }

    private String weekdayLabel(int day) {
        switch (day) {
            case 1:
                return "\u5468\u4e00";
            case 2:
                return "\u5468\u4e8c";
            case 3:
                return "\u5468\u4e09";
            case 4:
                return "\u5468\u56db";
            case 5:
                return "\u5468\u4e94";
            case 6:
                return "\u5468\u516d";
            case 7:
                return "\u5468\u65e5";
            default:
                return "";
        }
    }

    private long currentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
        }
        return 0L;
    }

    private void ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Exercise Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Remind users to move after class/rest");
        manager.createNotificationChannel(channel);
    }

    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.POST_NOTIFICATIONS },
                REQ_NOTIFICATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "\u63d0\u9192\u901a\u77e5\u5df2\u5f00\u542f", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "\u672a\u5f00\u542f\u901a\u77e5\uff0c\u4ec5\u53ef\u80fd\u770b\u5230Toast\u63d0\u793a", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
