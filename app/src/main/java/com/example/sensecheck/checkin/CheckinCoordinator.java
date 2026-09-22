package com.example.sensecheck.checkin;

import android.content.Context;

import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.data.AttendanceDbHelper;
import com.example.sensecheck.data.CheckinRecord;

public final class CheckinCoordinator {
    public interface Callback {
        void onCompleted(CheckinRecord record);

        void onError(String message);
    }

    private final Context context;
    private final AppPreferences preferences;
    private final AttendanceDbHelper database;

    public CheckinCoordinator(Context context) {
        this.context = context.getApplicationContext();
        preferences = new AppPreferences(context);
        database = new AttendanceDbHelper(context);
    }

    public void performCheckin(Callback callback) {
        if (!preferences.isProfileConfigured()) {
            callback.onError("请先填写学号和姓名");
            return;
        }
        if (!preferences.isCourseConfigured()) {
            callback.onError("请先设置课程时间和教室位置");
            return;
        }

        new EnvironmentCollector(context).collect(snapshot -> {
            RuleResult ruleResult = CheckinRuleEngine.evaluate(preferences, snapshot);
            CheckinRecord record = new CheckinRecord();
            record.setCourseName(preferences.getCourseName());
            record.setSampleTime(snapshot.getSampleTime());
            record.setLatitude(snapshot.isLocationAvailable() ? snapshot.getLatitude() : Double.NaN);
            record.setLongitude(snapshot.isLocationAvailable() ? snapshot.getLongitude() : Double.NaN);
            record.setAccuracy(snapshot.isLocationAvailable() ? snapshot.getAccuracy() : Float.NaN);
            record.setDistanceMeters(ruleResult.getDistanceMeters());
            record.setWifiCount(snapshot.getWifiCount());
            record.setBluetoothCount(snapshot.getBluetoothCount());
            record.setWifiDirectCount(snapshot.getWifiDirectCount());
            record.setResult(ruleResult.getResult());
            record.setReason(ruleResult.getReason());

            try {
                database.insert(record);
                callback.onCompleted(record);
            } catch (RuntimeException exception) {
                callback.onError("保存签到记录失败：" + exception.getMessage());
            }
        });
    }
}

