package com.example.sensecheck.util;

import com.example.sensecheck.data.CheckinRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public final class ExportManager {
    private ExportManager() {
    }

    public static String toCsv(List<CheckinRecord> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("序号,课程,采集时间,纬度,经度,定位精度(米),距教室(米),Wi-Fi数量,蓝牙数量,Wi-Fi Direct数量,结果,原因\n");
        for (CheckinRecord record : records) {
            builder.append(record.getId()).append(',')
                    .append(escapeCsv(record.getCourseName())).append(',')
                    .append(escapeCsv(TimeUtils.formatDateTime(record.getSampleTime()))).append(',')
                    .append(number(record.getLatitude())).append(',')
                    .append(number(record.getLongitude())).append(',')
                    .append(number(record.getAccuracy())).append(',')
                    .append(number(record.getDistanceMeters())).append(',')
                    .append(record.getWifiCount()).append(',')
                    .append(record.getBluetoothCount()).append(',')
                    .append(record.getWifiDirectCount()).append(',')
                    .append(escapeCsv(record.getResult())).append(',')
                    .append(escapeCsv(record.getReason())).append('\n');
        }
        return "\uFEFF" + builder;
    }

    public static String toJson(List<CheckinRecord> records) {
        JSONArray array = new JSONArray();
        for (CheckinRecord record : records) {
            JSONObject object = new JSONObject();
            try {
                object.put("id", record.getId());
                object.put("courseName", record.getCourseName());
                object.put("sampleTime", record.getSampleTime());
                object.put("sampleTimeText", TimeUtils.formatDateTime(record.getSampleTime()));
                putNumberOrNull(object, "latitude", record.getLatitude());
                putNumberOrNull(object, "longitude", record.getLongitude());
                putNumberOrNull(object, "accuracy", record.getAccuracy());
                putNumberOrNull(object, "distanceMeters", record.getDistanceMeters());
                object.put("wifiCount", record.getWifiCount());
                object.put("bluetoothCount", record.getBluetoothCount());
                object.put("wifiDirectCount", record.getWifiDirectCount());
                object.put("result", record.getResult());
                object.put("reason", record.getReason());
                array.put(object);
            } catch (JSONException exception) {
                throw new IllegalStateException("生成 JSON 失败", exception);
            }
        }
        try {
            return array.toString(2);
        } catch (JSONException exception) {
            return array.toString();
        }
    }

    private static void putNumberOrNull(JSONObject object, String key, double value)
            throws JSONException {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            object.put(key, JSONObject.NULL);
        } else {
            object.put(key, value);
        }
    }

    private static String number(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "";
        }
        return String.format(Locale.US, "%.6f", value);
    }

    private static String escapeCsv(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }
}

