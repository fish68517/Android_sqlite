package com.hakimi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hakimi.model.ClassSchedule;

import java.util.ArrayList;
import java.util.List;

public class ClassScheduleDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "class_schedule.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_SCHEDULE = "class_schedule";

    public ClassScheduleDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "course_name TEXT NOT NULL,"
                + "weekday INTEGER NOT NULL,"
                + "start_time TEXT NOT NULL,"
                + "end_time TEXT NOT NULL,"
                + "location TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        onCreate(db);
    }

    public long insert(ClassSchedule schedule) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", schedule.getUserId());
        values.put("course_name", schedule.getCourseName());
        values.put("weekday", schedule.getWeekday());
        values.put("start_time", schedule.getStartTime());
        values.put("end_time", schedule.getEndTime());
        values.put("location", schedule.getLocation());
        return db.insert(TABLE_SCHEDULE, null, values);
    }

    public List<ClassSchedule> queryByUser(long userId) {
        List<ClassSchedule> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SCHEDULE,
                null,
                "user_id = ?",
                new String[] { String.valueOf(userId) },
                null,
                null,
                "weekday ASC, start_time ASC, id DESC");

        try {
            while (cursor.moveToNext()) {
                ClassSchedule schedule = new ClassSchedule();
                schedule.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                schedule.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
                schedule.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow("course_name")));
                schedule.setWeekday(cursor.getInt(cursor.getColumnIndexOrThrow("weekday")));
                schedule.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                schedule.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
                schedule.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                list.add(schedule);
            }
        } finally {
            cursor.close();
        }

        return list;
    }
}
