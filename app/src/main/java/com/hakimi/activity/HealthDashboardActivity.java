package com.hakimi.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.LocalResult;
import com.hakimi.model.ExerciseData;
import com.hakimi.receiver.ExerciseReminderReceiver;
import com.hakimi.utils.SharedPrefManager;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthDashboardActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "exercise_reminder_channel";
    private static final String PREF_HEALTH = "health_dashboard_pref";
    private static final String KEY_SCHEDULES = "class_schedules";
    private static final int REQ_NOTIFICATION = 2001;

    private EditText etExerciseType;
    private EditText etExerciseLocation;
    private EditText etExerciseDuration;
    private Button btnSaveExercise;
    private BarChart barChartWeekly;

    private EditText etCourseName;
    private Spinner spWeekday;
    private EditText etReminderTime;
    private Button btnPickTime;
    private Button btnSaveSchedule;
    private TextView tvScheduleList;

    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;
    private SharedPreferences localPrefs;
    private final Gson gson = new Gson();
    private final List<ClassScheduleItem> schedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);

        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();
        localPrefs = getSharedPreferences(PREF_HEALTH, MODE_PRIVATE);

        initViews();
        setupWeekdaySpinner();
        setupChart();
        setupListeners();
        ensureNotificationChannel();
        ensureNotificationPermission();

        loadSchedules();
        loadExerciseData();
    }

    private void initViews() {
        etExerciseType = findViewById(R.id.et_exercise_type);
        etExerciseLocation = findViewById(R.id.et_exercise_location);
        etExerciseDuration = findViewById(R.id.et_exercise_duration);
        btnSaveExercise = findViewById(R.id.btn_save_exercise);
        barChartWeekly = findViewById(R.id.bar_chart_weekly);

        etCourseName = findViewById(R.id.et_course_name);
        spWeekday = findViewById(R.id.sp_weekday);
        etReminderTime = findViewById(R.id.et_reminder_time);
        btnPickTime = findViewById(R.id.btn_pick_time);
        btnSaveSchedule = findViewById(R.id.btn_save_schedule);
        tvScheduleList = findViewById(R.id.tv_schedule_list);
    }

    private void setupWeekdaySpinner() {
        List<String> weekdays = new ArrayList<>();
        weekdays.add("周一");
        weekdays.add("周二");
        weekdays.add("周三");
        weekdays.add("周四");
        weekdays.add("周五");
        weekdays.add("周六");
        weekdays.add("周日");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, weekdays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWeekday.setAdapter(adapter);
    }

    private void setupChart() {
        barChartWeekly.getDescription().setEnabled(false);
        barChartWeekly.getLegend().setEnabled(false);
        barChartWeekly.setFitBars(true);
        barChartWeekly.setDrawGridBackground(false);

        XAxis xAxis = barChartWeekly.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis left = barChartWeekly.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setGranularity(10f);
        left.setAxisLineColor(0xFFDDDDDD);

        barChartWeekly.getAxisRight().setEnabled(false);
    }

    private void setupListeners() {
        btnSaveExercise.setOnClickListener(v -> saveExerciseRecord());
        btnPickTime.setOnClickListener(v -> showTimePicker());
        btnSaveSchedule.setOnClickListener(v -> saveSchedule());
    }

    private void saveExerciseRecord() {
        String type = etExerciseType.getText().toString().trim();
        String location = etExerciseLocation.getText().toString().trim();
        String durationText = etExerciseDuration.getText().toString().trim();

        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(location) || TextUtils.isEmpty(durationText)) {
            Toast.makeText(this, "请填写完整的运动信息", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                Toast.makeText(this, "运动时长必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "运动时长请输入数字", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "未获取到当前用户", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<ExerciseData> result = repository.addExerciseRecord(userId, type, location, duration);
        if (!result.isSuccess()) {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "运动记录已保存", Toast.LENGTH_SHORT).show();
        etExerciseType.setText("");
        etExerciseLocation.setText("");
        etExerciseDuration.setText("");
        loadExerciseData();
    }

    private void loadExerciseData() {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            return;
        }
        List<ExerciseData> mine = repository.getExerciseRecordsByUser(userId);
        renderWeeklyChart(mine);
    }

    private void renderWeeklyChart(List<ExerciseData> data) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> durationByDate = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            durationByDate.put(today.minusDays(i), 0);
        }

        for (ExerciseData item : data) {
            LocalDate date = parseDate(item.getCreatedAt());
            if (date == null || !durationByDate.containsKey(date)) {
                continue;
            }
            int old = durationByDate.get(date) == null ? 0 : durationByDate.get(date);
            int d = item.getDuration() == null ? 0 : item.getDuration();
            durationByDate.put(date, old + d);
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault());
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            labels.add(formatter.format(date));
            int minutes = durationByDate.get(date) == null ? 0 : durationByDate.get(date);
            entries.add(new BarEntry(6 - i, minutes));
        }

        XAxis xAxis = barChartWeekly.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Exercise");
        dataSet.setColor(0xFF4CAF50);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) + "min";
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChartWeekly.setData(barData);
        barChartWeekly.invalidate();
        barChartWeekly.animateY(500);
    }

    private LocalDate parseDate(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toLocalDate();
        } catch (Exception ignore) {
        }
        try {
            return LocalDate.parse(value.substring(0, 10));
        } catch (Exception ignore) {
        }
        return null;
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        android.app.TimePickerDialog dialog = new android.app.TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minute) -> {
                    String value = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etReminderTime.setText(value);
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    private void saveSchedule() {
        String courseName = etCourseName.getText().toString().trim();
        String reminderTime = etReminderTime.getText().toString().trim();
        int weekdayIndex = spWeekday.getSelectedItemPosition();

        if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(reminderTime)) {
            Toast.makeText(this, "请填写完整的课表信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!reminderTime.matches("^\\d{2}:\\d{2}$")) {
            Toast.makeText(this, "提醒时间格式需为HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassScheduleItem item = new ClassScheduleItem();
        item.courseName = courseName;
        item.weekday = weekdayIndex + 1;
        item.reminderTime = reminderTime;

        schedules.add(item);
        persistSchedules();
        scheduleReminder(item);
        renderScheduleList();

        etCourseName.setText("");
        etReminderTime.setText("");
        Toast.makeText(this, "课表已保存并设置提醒", Toast.LENGTH_SHORT).show();
    }

    private void loadSchedules() {
        String json = localPrefs.getString(KEY_SCHEDULES, "[]");
        Type type = new TypeToken<List<ClassScheduleItem>>() {
        }.getType();
        List<ClassScheduleItem> list = gson.fromJson(json, type);
        schedules.clear();
        if (list != null) {
            schedules.addAll(list);
        }
        renderScheduleList();
    }

    private void persistSchedules() {
        localPrefs.edit().putString(KEY_SCHEDULES, gson.toJson(schedules)).apply();
    }

    private void renderScheduleList() {
        if (schedules.isEmpty()) {
            tvScheduleList.setText("暂无课表提醒");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < schedules.size(); i++) {
            ClassScheduleItem item = schedules.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(item.courseName)
                    .append(" - ")
                    .append(weekdayLabel(item.weekday))
                    .append(" ")
                    .append(item.reminderTime)
                    .append('\n');
        }
        tvScheduleList.setText(builder.toString().trim());
    }

    private String weekdayLabel(int day) {
        switch (day) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "";
        }
    }

    private void scheduleReminder(ClassScheduleItem item) {
        String[] hm = item.reminderTime.split(":");
        int hour = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, weekdayToCalendar(item.weekday));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        Intent intent = new Intent(this, ExerciseReminderReceiver.class);
        intent.putExtra("course_name", item.courseName);
        intent.putExtra("weekday", weekdayLabel(item.weekday));
        int requestCode = (item.courseName + "_" + item.weekday + "_" + item.reminderTime).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
            );
        }
    }

    private int weekdayToCalendar(int day) {
        switch (day) {
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
                Toast.makeText(this, "提醒通知已开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "未授权通知，提醒可能不会显示", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
        }
        return null;
    }

    private static class ClassScheduleItem {
        String courseName;
        int weekday;
        String reminderTime;
    }
}
