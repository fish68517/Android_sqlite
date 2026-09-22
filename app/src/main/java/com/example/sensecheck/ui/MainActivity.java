package com.example.sensecheck.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensecheck.R;
import com.example.sensecheck.checkin.CheckinCoordinator;
import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.data.AttendanceDbHelper;
import com.example.sensecheck.data.CheckinRecord;
import com.example.sensecheck.service.AttendanceForegroundService;
import com.example.sensecheck.util.PermissionUtils;
import com.example.sensecheck.util.ScheduleManager;
import com.example.sensecheck.util.TimeUtils;

import java.util.Locale;

public final class MainActivity extends Activity {
    private AppPreferences preferences;
    private AttendanceDbHelper database;
    private TextView profileView;
    private TextView courseView;
    private TextView windowStatusView;
    private TextView latestResultView;
    private TextView latestDetailView;
    private TextView statisticsView;
    private TextView permissionStatusView;
    private ProgressBar progressView;
    private Button manualCheckinButton;
    private boolean receiverRegistered;

    private final BroadcastReceiver recordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDashboard();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = new AppPreferences(this);
        database = new AttendanceDbHelper(this);

        profileView = findViewById(R.id.tvProfile);
        courseView = findViewById(R.id.tvCourse);
        windowStatusView = findViewById(R.id.tvWindowStatus);
        latestResultView = findViewById(R.id.tvLatestResult);
        latestDetailView = findViewById(R.id.tvLatestDetail);
        statisticsView = findViewById(R.id.tvStatistics);
        permissionStatusView = findViewById(R.id.tvPermissionStatus);
        progressView = findViewById(R.id.progressCheckin);
        manualCheckinButton = findViewById(R.id.btnManualCheckin);

        findViewById(R.id.btnProfile).setOnClickListener(view ->
                startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.btnCourseSettings).setOnClickListener(view ->
                startActivity(new Intent(this, CourseSettingsActivity.class)));
        findViewById(R.id.btnHistory).setOnClickListener(view ->
                startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.btnPermissions).setOnClickListener(view -> {
            if (PermissionUtils.missingPermissions(this).length == 0) {
                Toast.makeText(this, "签到所需权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                PermissionUtils.requestMissing(this);
            }
        });
        manualCheckinButton.setOnClickListener(view -> performManualCheckin());
        findViewById(R.id.btnStartService).setOnClickListener(view -> startAttendanceService());
        findViewById(R.id.btnStopService).setOnClickListener(view -> stopAttendanceService());

        preferences.getDeviceId();
        ScheduleManager.scheduleNext(this);
    }

    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AttendanceForegroundService.ACTION_RECORD_CREATED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(recordReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(recordReceiver, filter);
        }
        receiverRegistered = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    @Override
    protected void onStop() {
        if (receiverRegistered) {
            unregisterReceiver(recordReceiver);
            receiverRegistered = false;
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_CODE) {
            permissionStatusView.setText(PermissionUtils.statusText(this));
        }
    }

    private void refreshDashboard() {
        if (preferences.isProfileConfigured()) {
            String classSuffix = preferences.getClassName().isEmpty()
                    ? ""
                    : " · " + preferences.getClassName();
            profileView.setText(preferences.getName() + "（" + preferences.getStudentNo() + "）" + classSuffix);
        } else {
            profileView.setText("尚未填写个人信息");
        }

        if (preferences.isCourseConfigured()) {
            courseView.setText(String.format(
                    Locale.CHINA,
                    "%s · %s %s-%s · 半径 %.0f 米",
                    preferences.getCourseName(),
                    TimeUtils.weekdayName(preferences.getWeekday()),
                    TimeUtils.formatMinutes(preferences.getStartMinutes()),
                    TimeUtils.formatMinutes(preferences.getEndMinutes()),
                    preferences.getRadiusMeters()));
            boolean inWindow = TimeUtils.isInCourseWindow(preferences, System.currentTimeMillis());
            windowStatusView.setText(inWindow ? "当前处于课程签到时间" : "当前不在课程签到时间");
            windowStatusView.setTextColor(getColor(inWindow ? R.color.success : R.color.primary));
        } else {
            courseView.setText("尚未设置课程");
            windowStatusView.setText("等待配置");
            windowStatusView.setTextColor(getColor(R.color.primary));
        }

        CheckinRecord latest = database.getLatest();
        if (latest == null) {
            latestResultView.setText("暂无签到记录");
            latestDetailView.setText("完成配置后可立即测试");
            latestResultView.setTextColor(getColor(R.color.text_primary));
        } else {
            latestResultView.setText(latest.getResult());
            latestResultView.setTextColor(resultColor(latest.getResult()));
            latestDetailView.setText(TimeUtils.formatDateTime(latest.getSampleTime()) + " · " + latest.getReason());
        }
        int total = database.getTotalCount();
        int success = database.getSuccessCount();
        float rate = total == 0 ? 0f : success * 100f / total;
        statisticsView.setText(String.format(
                Locale.CHINA,
                "总记录 %d 次 · 成功 %d 次 · 成功率 %.1f%%",
                total,
                success,
                rate));
        permissionStatusView.setText(PermissionUtils.statusText(this));
    }

    private void performManualCheckin() {
        if (!ensureConfigured()) {
            return;
        }
        progressView.setVisibility(View.VISIBLE);
        manualCheckinButton.setEnabled(false);
        new CheckinCoordinator(this).performCheckin(new CheckinCoordinator.Callback() {
            @Override
            public void onCompleted(CheckinRecord record) {
                progressView.setVisibility(View.GONE);
                manualCheckinButton.setEnabled(true);
                Toast.makeText(MainActivity.this, record.getResult(), Toast.LENGTH_SHORT).show();
                refreshDashboard();
            }

            @Override
            public void onError(String message) {
                progressView.setVisibility(View.GONE);
                manualCheckinButton.setEnabled(true);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startAttendanceService() {
        if (!ensureConfigured()) {
            return;
        }
        if (!PermissionUtils.hasLocation(this)) {
            PermissionUtils.requestMissing(this);
            Toast.makeText(this, "启动自动签到前需要授予定位权限", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TimeUtils.isInCourseWindow(preferences, System.currentTimeMillis())) {
            ScheduleManager.scheduleNext(this);
            Toast.makeText(this, "当前不在课程时间，已保留下次课程计划", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, AttendanceForegroundService.class);
        intent.setAction(AttendanceForegroundService.ACTION_START);
        startForegroundService(intent);
        Toast.makeText(this, "自动签到服务已启动", Toast.LENGTH_SHORT).show();
    }

    private void stopAttendanceService() {
        Intent intent = new Intent(this, AttendanceForegroundService.class);
        intent.setAction(AttendanceForegroundService.ACTION_STOP);
        startService(intent);
        Toast.makeText(this, "已请求停止自动签到服务", Toast.LENGTH_SHORT).show();
    }

    private boolean ensureConfigured() {
        if (!preferences.isProfileConfigured()) {
            Toast.makeText(this, "请先填写个人信息", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
            return false;
        }
        if (!preferences.isCourseConfigured()) {
            Toast.makeText(this, "请先完成课程设置", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CourseSettingsActivity.class));
            return false;
        }
        return true;
    }

    private int resultColor(String result) {
        if (CheckinRecord.RESULT_SUCCESS.equals(result)) {
            return getColor(R.color.success);
        }
        if (CheckinRecord.RESULT_FAILED.equals(result)) {
            return getColor(R.color.danger);
        }
        return getColor(R.color.warning);
    }
}
