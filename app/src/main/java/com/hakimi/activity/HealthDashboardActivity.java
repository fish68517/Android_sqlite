package com.hakimi.ui.activity;

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
import com.hakimi.model.ApiResponse;
import com.hakimi.model.ExerciseData;
import com.hakimi.model.PageResponse;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private SharedPreferences localPrefs;
    private final Gson gson = new Gson();
    private final List<ClassScheduleItem> schedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);

        apiService = RetrofitClient.getInstance().getApiService();
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
        weekdays.add("\u5468\u4e00");
        weekdays.add("\u5468\u4e8c");
        weekdays.add("\u5468\u4e09");
        weekdays.add("\u5468\u56db");
        weekdays.add("\u5468\u4e94");
        weekdays.add("\u5468\u516d");
        weekdays.add("\u5468\u65e5");
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
            Toast.makeText(this, "\u8bf7\u586b\u5199\u5b8c\u6574\u7684\u8fd0\u52a8\u4fe1\u606f", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                Toast.makeText(this, "\u8fd0\u52a8\u65f6\u957f\u5fc5\u987b\u5927\u4e8e0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "\u8fd0\u52a8\u65f6\u957f\u8bf7\u8f93\u5165\u6570\u5b57", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "\u672a\u83b7\u53d6\u5230\u5f53\u524d\u7528\u6237", Toast.LENGTH_SHORT).show();
            return;
        }

        ExerciseData body = new ExerciseData();
        body.setUserId(userId);
        body.setExerciseType(type);
        body.setLocation(location);
        body.setDuration(duration);

        apiService.dailyCheckIn(body).enqueue(new Callback<ApiResponse<ExerciseData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ExerciseData>> call,
                    @NonNull Response<ApiResponse<ExerciseData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(HealthDashboardActivity.this, "\u8fd0\u52a8\u8bb0\u5f55\u5df2\u4fdd\u5b58", Toast.LENGTH_SHORT).show();
                    etExerciseType.setText("");
                    etExerciseLocation.setText("");
                    etExerciseDuration.setText("");
                    loadExerciseData();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "\u8bf7\u7a0d\u540e\u91cd\u8bd5";
                    Toast.makeText(HealthDashboardActivity.this, "\u4fdd\u5b58\u5931\u8d25: " + msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ExerciseData>> call, @NonNull Throwable t) {
                Toast.makeText(HealthDashboardActivity.this, "\u7f51\u7edc\u9519\u8bef: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExerciseData() {
        apiService.getExerciseData(1, 200).enqueue(new Callback<ApiResponse<PageResponse<ExerciseData>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PageResponse<ExerciseData>>> call,
                    @NonNull Response<ApiResponse<PageResponse<ExerciseData>>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    return;
                }

                Long userId = getCurrentUserId();
                List<ExerciseData> all = new ArrayList<>();
                PageResponse<ExerciseData> pageResponse = response.body().getData();
                if (pageResponse != null && pageResponse.getRecords() != null) {
                    all.addAll(pageResponse.getRecords());
                }

                List<ExerciseData> mine = new ArrayList<>();
                for (ExerciseData item : all) {
                    if (userId != null && userId.equals(item.getUserId())) {
                        mine.add(item);
                    }
                }
                renderWeeklyChart(mine);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PageResponse<ExerciseData>>> call, @NonNull Throwable t) {
            }
        });
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
            Toast.makeText(this, "\u8bf7\u586b\u5199\u5b8c\u6574\u7684\u8bfe\u8868\u4fe1\u606f", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!reminderTime.matches("^\\d{2}:\\d{2}$")) {
            Toast.makeText(this, "\u63d0\u9192\u65f6\u95f4\u683c\u5f0f\u9700\u4e3aHH:mm", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "\u8bfe\u8868\u5df2\u4fdd\u5b58\u5e76\u8bbe\u7f6e\u63d0\u9192", Toast.LENGTH_SHORT).show();
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
            tvScheduleList.setText("\u6682\u65e0\u8bfe\u8868\u63d0\u9192");
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
                Toast.makeText(this, "\u63d0\u9192\u901a\u77e5\u5df2\u5f00\u542f", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "\u672a\u6388\u6743\u901a\u77e5\uff0c\u63d0\u9192\u53ef\u80fd\u4e0d\u4f1a\u663e\u793a", Toast.LENGTH_SHORT).show();
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
