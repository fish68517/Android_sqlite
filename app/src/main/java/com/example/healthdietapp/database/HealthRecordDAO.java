package com.example.healthdietapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.healthdietapp.models.HealthRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * HealthRecordDAO - Data Access Object for HealthRecord operations
 * Handles health record CRUD operations
 */
public class HealthRecordDAO {
    private DatabaseHelper dbHelper;

    /**
     * 获取用户所有的健康记录，并按日期升序排列（用于绘制图表）
     */
    public List<HealthRecord> getAllHealthRecordsForUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<HealthRecord> records = new ArrayList<>();
        try {
            // 按照 date 升序排列 (ASC)
            Cursor cursor = db.query("health_records", null, "user_id = ?",
                    new String[]{userId}, null, null, "date ASC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    records.add(cursorToHealthRecord(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return records;
    }

    public HealthRecordDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Create a new health record
     */
    public boolean createHealthRecord(HealthRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (record.getRecordId() == null) {
                record.setRecordId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("record_id", record.getRecordId());
            values.put("user_id", record.getUserId());
            values.put("date", record.getDate());
            values.put("weight", record.getWeight());
            values.put("water_intake", record.getWaterIntake());
            values.put("measurements", record.getMeasurements());
            values.put("recorded_at", record.getRecordedAt());
            
            long result = db.insertWithOnConflict("health_records", null, values, 
                    SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get health record by ID
     */
    public HealthRecord getHealthRecordById(String recordId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("health_records", null, "record_id = ?", 
                    new String[]{recordId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                HealthRecord record = cursorToHealthRecord(cursor);
                cursor.close();
                return record;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get health record for a specific date
     */
    public HealthRecord getHealthRecordByDate(String userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("health_records", null, 
                    "user_id = ? AND date = ?", 
                    new String[]{userId, date}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                HealthRecord record = cursorToHealthRecord(cursor);
                cursor.close();
                return record;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get all health records for a user
     */
    public List<HealthRecord> getAllHealthRecords(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<HealthRecord> records = new ArrayList<>();
        try {
            Cursor cursor = db.query("health_records", null, "user_id = ?", 
                    new String[]{userId}, null, null, "date DESC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    records.add(cursorToHealthRecord(cursor));
                }
                cursor.close();
            }
            return records;
        } finally {
            db.close();
        }
    }

    /**
     * Get health records for a date range
     */
    public List<HealthRecord> getHealthRecordsByDateRange(String userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<HealthRecord> records = new ArrayList<>();
        try {
            Cursor cursor = db.query("health_records", null, 
                    "user_id = ? AND date >= ? AND date <= ?", 
                    new String[]{userId, startDate, endDate}, null, null, "date DESC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    records.add(cursorToHealthRecord(cursor));
                }
                cursor.close();
            }
            return records;
        } finally {
            db.close();
        }
    }

    /**
     * Update health record
     */
    public boolean updateHealthRecord(HealthRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            record.setRecordedAt(System.currentTimeMillis());
            
            ContentValues values = new ContentValues();
            values.put("weight", record.getWeight());
            values.put("water_intake", record.getWaterIntake());
            values.put("measurements", record.getMeasurements());
            values.put("recorded_at", record.getRecordedAt());
            
            int result = db.update("health_records", values, "record_id = ?", 
                    new String[]{record.getRecordId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete health record
     */
    public boolean deleteHealthRecord(String recordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("health_records", "record_id = ?", 
                    new String[]{recordId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete health record by date
     */
    public boolean deleteHealthRecordByDate(String userId, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("health_records", 
                    "user_id = ? AND date = ?", 
                    new String[]{userId, date});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete all health records for a user
     */
    public boolean deleteUserRecords(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("health_records", "user_id = ?", 
                    new String[]{userId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to HealthRecord object
     */
    private HealthRecord cursorToHealthRecord(Cursor cursor) {
        HealthRecord record = new HealthRecord();
        record.setRecordId(cursor.getString(cursor.getColumnIndexOrThrow("record_id")));
        record.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        record.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
        record.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow("weight")));
        record.setWaterIntake(cursor.getFloat(cursor.getColumnIndexOrThrow("water_intake")));
        record.setMeasurements(cursor.getString(cursor.getColumnIndexOrThrow("measurements")));
        record.setRecordedAt(cursor.getLong(cursor.getColumnIndexOrThrow("recorded_at")));
        return record;
    }
}
