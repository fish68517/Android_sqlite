package com.Health.health;

public final class HealthConstants {

    public static final String METRIC_STEP = "STEP";
    public static final String METRIC_HEART_RATE = "HEART_RATE";
    public static final String METRIC_BLOOD_PRESSURE = "BLOOD_PRESSURE";

    public static final String SCOPE_DAILY = "DAILY";
    public static final String SCOPE_LATEST = "LATEST";
    public static final String SCOPE_INSTANT = "INSTANT";

    public static final String SOURCE_HEALTH_CONNECT = "HEALTH_CONNECT";
    public static final String SOURCE_MANUAL = "MANUAL";
    public static final String SOURCE_SENSOR = "SENSOR";
    public static final String SOURCE_MOCK = "MOCK";

    public static final String ALERT_HEART_RATE_HIGH = "HEART_RATE_HIGH";
    public static final String ALERT_HEART_RATE_LOW = "HEART_RATE_LOW";
    public static final String ALERT_BLOOD_PRESSURE_HIGH = "BLOOD_PRESSURE_HIGH";
    public static final String ALERT_BLOOD_PRESSURE_LOW = "BLOOD_PRESSURE_LOW";
    public static final String ALERT_STEP_LOW = "STEP_LOW";

    public static final String ALERT_LEVEL_WARNING = "WARNING";
    public static final String ALERT_LEVEL_DANGER = "DANGER";

    public static final String HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata";
    public static final String WORK_NAME_HEALTH_MONITOR = "health_monitor_periodic";
    public static final String WORK_NAME_HEALTH_MONITOR_IMMEDIATE = "health_monitor_immediate";
    public static final String NOTIFICATION_CHANNEL_ID = "health_alert_channel";
    public static final int NOTIFICATION_REQUEST_CODE = 3201;

    public static final String PREF_HEALTH_MONITOR = "health_monitor_pref";
    public static final String KEY_LAST_SYNC_AT = "last_sync_at";
    public static final String KEY_LAST_SYNC_MESSAGE = "last_sync_message";

    private HealthConstants() {
    }
}
