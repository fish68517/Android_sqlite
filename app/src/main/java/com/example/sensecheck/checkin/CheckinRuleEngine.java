package com.example.sensecheck.checkin;

import android.location.Location;

import com.example.sensecheck.data.AppPreferences;
import com.example.sensecheck.data.CheckinRecord;
import com.example.sensecheck.util.TimeUtils;

public final class CheckinRuleEngine {
    private CheckinRuleEngine() {
    }

    public static RuleResult evaluate(AppPreferences preferences, EnvironmentSnapshot snapshot) {
        if (!TimeUtils.isInCourseWindow(preferences, snapshot.getSampleTime())) {
            return new RuleResult(CheckinRecord.RESULT_FAILED, "当前不在课程签到时间内", Float.NaN);
        }
        if (!snapshot.isLocationAvailable()) {
            return new RuleResult(CheckinRecord.RESULT_UNCERTAIN, "无法获得当前位置，请检查定位权限和定位开关", Float.NaN);
        }

        float[] distanceResult = new float[1];
        Location.distanceBetween(
                snapshot.getLatitude(),
                snapshot.getLongitude(),
                preferences.getLatitude(),
                preferences.getLongitude(),
                distanceResult);
        float distance = distanceResult[0];

        if (distance > preferences.getRadiusMeters()) {
            return new RuleResult(
                    CheckinRecord.RESULT_FAILED,
                    "距离教室 " + Math.round(distance) + " 米，超过允许范围",
                    distance);
        }
        if (snapshot.getNearbyDeviceCount() == 0) {
            return new RuleResult(
                    CheckinRecord.RESULT_UNCERTAIN,
                    "位置符合，但未获取到周边 Wi-Fi、蓝牙或 Wi-Fi Direct 设备",
                    distance);
        }
        return new RuleResult(
                CheckinRecord.RESULT_SUCCESS,
                "课程时间、位置和周边环境条件均符合",
                distance);
    }
}

