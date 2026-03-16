package com.hakimi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.hakimi.model.ClassSchedule;

import java.util.ArrayList;
import java.util.List;

public class ClassScheduleDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "health_app.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_SCHEDULE = "class_schedule";
    private static final String COL_ID = "id";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_COURSE_NAME = "course_name";
    private static final String COL_WEEKDAY = "weekday";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_END_TIME = "end_time";
    private static final String COL_LOCATION = "location";

    public ClassScheduleDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USER_ID + " INTEGER NOT NULL,"
                + COL_COURSE_NAME + " TEXT NOT NULL,"
                + COL_WEEKDAY + " INTEGER NOT NULL,"
                + COL_START_TIME + " TEXT NOT NULL,"
                + COL_END_TIME + " TEXT NOT NULL,"
                + COL_LOCATION + " TEXT"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long insert(ClassSchedule schedule) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, schedule.getUserId());
        values.put(COL_COURSE_NAME, schedule.getCourseName());
        values.put(COL_WEEKDAY, schedule.getWeekday());
        values.put(COL_START_TIME, schedule.getStartTime());
        values.put(COL_END_TIME, schedule.getEndTime());
        values.put(COL_LOCATION, schedule.getLocation());
        return db.insert(TABLE_SCHEDULE, null, values);
    }

    public List<ClassSchedule> queryByUser(long userId) {
        List<ClassSchedule> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SCHEDULE,
                null,
                COL_USER_ID + "=?",
                new String[] { String.valueOf(userId) },
                null,
                null,
                COL_WEEKDAY + " ASC, " + COL_START_TIME + " ASC"
        );
        try {
            while (cursor.moveToNext()) {
                ClassSchedule item = new ClassSchedule();
                item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                item.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                item.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_NAME)));
                item.setWeekday(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WEEKDAY)));
                item.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)));
                item.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)));
                item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                list.add(item);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
