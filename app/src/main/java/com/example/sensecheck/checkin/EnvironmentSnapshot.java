package com.example.sensecheck.checkin;

public final class EnvironmentSnapshot {
    private final long sampleTime = System.currentTimeMillis();
    private boolean locationAvailable;
    private double latitude;
    private double longitude;
    private float accuracy;
    private int wifiCount;
    private int bluetoothCount;
    private int wifiDirectCount;

    public long getSampleTime() {
        return sampleTime;
    }

    public boolean isLocationAvailable() {
        return locationAvailable;
    }

    public void setLocation(double latitude, double longitude, float accuracy) {
        this.locationAvailable = true;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public int getWifiCount() {
        return wifiCount;
    }

    public void setWifiCount(int wifiCount) {
        this.wifiCount = Math.max(0, wifiCount);
    }

    public int getBluetoothCount() {
        return bluetoothCount;
    }

    public void setBluetoothCount(int bluetoothCount) {
        this.bluetoothCount = Math.max(0, bluetoothCount);
    }

    public int getWifiDirectCount() {
        return wifiDirectCount;
    }

    public void setWifiDirectCount(int wifiDirectCount) {
        this.wifiDirectCount = Math.max(0, wifiDirectCount);
    }

    public int getNearbyDeviceCount() {
        return wifiCount + bluetoothCount + wifiDirectCount;
    }
}

