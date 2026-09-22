package com.example.sensecheck.data;

public final class CheckinRecord {
    public static final String RESULT_SUCCESS = "签到成功";
    public static final String RESULT_UNCERTAIN = "证据不足";
    public static final String RESULT_FAILED = "签到失败";

    private long id;
    private String courseName;
    private long sampleTime;
    private double latitude;
    private double longitude;
    private float accuracy;
    private float distanceMeters;
    private int wifiCount;
    private int bluetoothCount;
    private int wifiDirectCount;
    private String result;
    private String reason;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(long sampleTime) {
        this.sampleTime = sampleTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(float distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public int getWifiCount() {
        return wifiCount;
    }

    public void setWifiCount(int wifiCount) {
        this.wifiCount = wifiCount;
    }

    public int getBluetoothCount() {
        return bluetoothCount;
    }

    public void setBluetoothCount(int bluetoothCount) {
        this.bluetoothCount = bluetoothCount;
    }

    public int getWifiDirectCount() {
        return wifiDirectCount;
    }

    public void setWifiDirectCount(int wifiDirectCount) {
        this.wifiDirectCount = wifiDirectCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

