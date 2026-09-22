package com.example.sensecheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public final class AttendanceDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sense_check.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECORD = "checkin_record";

    public AttendanceDbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TABLE_RECORD + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "course_name TEXT NOT NULL,"
                + "sample_time INTEGER NOT NULL,"
                + "latitude REAL,"
                + "longitude REAL,"
                + "accuracy REAL,"
                + "distance_meters REAL,"
                + "wifi_count INTEGER NOT NULL DEFAULT 0,"
                + "bluetooth_count INTEGER NOT NULL DEFAULT 0,"
                + "wifi_direct_count INTEGER NOT NULL DEFAULT 0,"
                + "result TEXT NOT NULL,"
                + "reason TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        onCreate(database);
    }

    public long insert(CheckinRecord record) {
        ContentValues values = new ContentValues();
        values.put("course_name", record.getCourseName());
        values.put("sample_time", record.getSampleTime());
        values.put("latitude", record.getLatitude());
        values.put("longitude", record.getLongitude());
        values.put("accuracy", record.getAccuracy());
        values.put("distance_meters", record.getDistanceMeters());
        values.put("wifi_count", record.getWifiCount());
        values.put("bluetooth_count", record.getBluetoothCount());
        values.put("wifi_direct_count", record.getWifiDirectCount());
        values.put("result", record.getResult());
        values.put("reason", record.getReason());
        long id = getWritableDatabase().insertOrThrow(TABLE_RECORD, null, values);
        record.setId(id);
        return id;
    }

    public List<CheckinRecord> getAll() {
        List<CheckinRecord> records = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_RECORD,
                null,
                null,
                null,
                null,
                null,
                "sample_time DESC")) {
            while (cursor.moveToNext()) {
                records.add(fromCursor(cursor));
            }
        }
        return records;
    }

    public CheckinRecord getLatest() {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_RECORD,
                null,
                null,
                null,
                null,
                null,
                "sample_time DESC",
                "1")) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    public CheckinRecord getById(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_RECORD,
                null,
                "id=?",
                new String[]{Long.toString(id)},
                null,
                null,
                null,
                "1")) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    public int getTotalCount() {
        return getCountFor(null, null);
    }

    public int getSuccessCount() {
        return getCountFor("result=?", new String[]{CheckinRecord.RESULT_SUCCESS});
    }

    public void clearAll() {
        getWritableDatabase().delete(TABLE_RECORD, null, null);
    }

    private int getCountFor(String selection, String[] selectionArgs) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_RECORD,
                new String[]{"COUNT(*)"},
                selection,
                selectionArgs,
                null,
                null,
                null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private CheckinRecord fromCursor(Cursor cursor) {
        CheckinRecord record = new CheckinRecord();
        record.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        record.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow("course_name")));
        record.setSampleTime(cursor.getLong(cursor.getColumnIndexOrThrow("sample_time")));
        record.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
        record.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
        record.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow("accuracy")));
        record.setDistanceMeters(cursor.getFloat(cursor.getColumnIndexOrThrow("distance_meters")));
        record.setWifiCount(cursor.getInt(cursor.getColumnIndexOrThrow("wifi_count")));
        record.setBluetoothCount(cursor.getInt(cursor.getColumnIndexOrThrow("bluetooth_count")));
        record.setWifiDirectCount(cursor.getInt(cursor.getColumnIndexOrThrow("wifi_direct_count")));
        record.setResult(cursor.getString(cursor.getColumnIndexOrThrow("result")));
        record.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
        return record;
    }
}

