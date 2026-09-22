package com.example.sensecheck.ui;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensecheck.R;
import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.util.PermissionUtils;
import com.example.sensecheck.util.ScheduleManager;
import com.example.sensecheck.util.TimeUtils;

import java.util.Calendar;
import java.util.Locale;

public final class CourseSettingsActivity extends Activity {
    private static final int[] WEEKDAY_VALUES = {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };
    private static final int[] INTERVAL_VALUES = {1, 5, 15};

    private AppPreferences preferences;
    private EditText courseNameView;
    private Spinner weekdayView;
    private TextView locationView;
    private EditText radiusView;
    private Spinner intervalView;
    private Switch autoView;
    private int startMinutes;
    private int endMinutes;
    private double latitude = Double.NaN;
    private double longitude = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_settings);
        preferences = new AppPreferences(this);

        courseNameView = findViewById(R.id.etCourseName);
        weekdayView = findViewById(R.id.spWeekday);
        locationView = findViewById(R.id.tvLocation);
        radiusView = findViewById(R.id.etRadius);
        intervalView = findViewById(R.id.spInterval);
        autoView = findViewById(R.id.switchAuto);

        weekdayView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"}));
        intervalView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"1 分钟（调试）", "5 分钟", "15 分钟（正式）"}));

        loadValues();
        refreshTimeButtons();
        refreshLocationText();

        findViewById(R.id.btnStartTime).setOnClickListener(view -> pickTime(true));
        findViewById(R.id.btnEndTime).setOnClickListener(view -> pickTime(false));
        findViewById(R.id.btnUseCurrentLocation).setOnClickListener(view -> captureCurrentLocation());
        findViewById(R.id.btnSaveCourse).setOnClickListener(view -> saveCourse());
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
    }

    private void loadValues() {
        courseNameView.setText(preferences.getCourseName());
        startMinutes = preferences.getStartMinutes();
        endMinutes = preferences.getEndMinutes();
        latitude = preferences.getLatitude();
        longitude = preferences.getLongitude();
        radiusView.setText(Float.toString(preferences.getRadiusMeters()));
        autoView.setChecked(preferences.isAutoEnabled());

        for (int index = 0; index < WEEKDAY_VALUES.length; index++) {
            if (WEEKDAY_VALUES[index] == preferences.getWeekday()) {
                weekdayView.setSelection(index);
                break;
            }
        }
        for (int index = 0; index < INTERVAL_VALUES.length; index++) {
            if (INTERVAL_VALUES[index] == preferences.getIntervalMinutes()) {
                intervalView.setSelection(index);
                break;
            }
        }
    }

    private void pickTime(boolean start) {
        int value = start ? startMinutes : endMinutes;
        new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (start) {
                        startMinutes = hourOfDay * 60 + minute;
                    } else {
                        endMinutes = hourOfDay * 60 + minute;
                    }
                    refreshTimeButtons();
                },
                value / 60,
                value % 60,
                true).show();
    }

    private void refreshTimeButtons() {
        ((android.widget.Button) findViewById(R.id.btnStartTime))
                .setText("开始 " + TimeUtils.formatMinutes(startMinutes));
        ((android.widget.Button) findViewById(R.id.btnEndTime))
                .setText("结束 " + TimeUtils.formatMinutes(endMinutes));
    }

    private void refreshLocationText() {
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            locationView.setText("尚未设置，请点击下方按钮获取当前位置");
        } else {
            locationView.setText(String.format(
                    Locale.CHINA,
                    "纬度 %.6f， 经度 %.6f",
                    latitude,
                    longitude));
        }
    }

    @SuppressWarnings("MissingPermission")
    private void captureCurrentLocation() {
        if (!PermissionUtils.hasLocation(this)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionUtils.REQUEST_CODE);
            Toast.makeText(this, "授权后请再次点击获取位置", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager == null) {
            Toast.makeText(this, "本机不支持定位服务", Toast.LENGTH_SHORT).show();
            return;
        }

        Location best = null;
        for (String provider : manager.getProviders(true)) {
            try {
                Location value = manager.getLastKnownLocation(provider);
                if (value != null && (best == null || value.getTime() > best.getTime())) {
                    best = value;
                }
            } catch (SecurityException ignored) {
                break;
            }
        }
        if (best != null) {
            updateLocation(best);
        }

        String provider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ? LocationManager.GPS_PROVIDER
                : LocationManager.NETWORK_PROVIDER;
        try {
            manager.requestSingleUpdate(provider, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, Looper.getMainLooper());
            Toast.makeText(this, "正在更新当前位置…", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException exception) {
            Toast.makeText(this, "请打开手机定位开关", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        refreshLocationText();
        Toast.makeText(this, "已使用当前位置作为教室位置", Toast.LENGTH_SHORT).show();
    }

    private void saveCourse() {
        String courseName = courseNameView.getText().toString().trim();
        if (TextUtils.isEmpty(courseName)) {
            Toast.makeText(this, "课程名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endMinutes <= startMinutes) {
            Toast.makeText(this, "结束时间必须晚于开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            Toast.makeText(this, "请先设置教室位置", Toast.LENGTH_SHORT).show();
            return;
        }

        float radius;
        try {
            radius = Float.parseFloat(radiusView.getText().toString().trim());
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "请输入正确的允许距离", Toast.LENGTH_SHORT).show();
            return;
        }
        if (radius < 10f || radius > 5000f) {
            Toast.makeText(this, "允许距离建议设置在 10-5000 米之间", Toast.LENGTH_SHORT).show();
            return;
        }

        preferences.saveCourse(
                courseName,
                WEEKDAY_VALUES[weekdayView.getSelectedItemPosition()],
                startMinutes,
                endMinutes,
                latitude,
                longitude,
                radius,
                INTERVAL_VALUES[intervalView.getSelectedItemPosition()],
                autoView.isChecked());
        ScheduleManager.scheduleNext(this);
        Toast.makeText(this, "课程设置已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}
