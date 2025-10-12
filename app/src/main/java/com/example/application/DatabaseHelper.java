package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.application.model.Appointment;
import com.example.application.model.CheckIn;
import com.example.application.model.Diet;
import com.example.application.model.Exercise;
import com.example.application.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "healthGuard.db";

    // Table Names
    private static final String TABLE_USER = "user_table";
    private static final String TABLE_CHECKIN = "checkin_table";
    private static final String TABLE_APPOINTMENT = "appointment_table";
    private static final String TABLE_DIET = "diet_table";
    private static final String TABLE_EXERCISE = "exercise_table";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";

    // USER Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ROLE = "role";

    // CHECKIN Table - column names
    private static final String KEY_CHECKIN_DATE = "checkin_date";

    // APPOINTMENT Table - column names
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_APPOINTMENT_TIME = "appointment_time";
    private static final String KEY_DESCRIPTION = "description";

    // DIET Table - column names
    private static final String KEY_MEAL_TYPE = "meal_type";
    private static final String KEY_FOOD_CONTENT = "food_content";
    private static final String KEY_RECORD_DATE = "record_date";

    // EXERCISE Table - column names
    private static final String KEY_EXERCISE_TYPE = "exercise_type";
    private static final String KEY_EXERCISE_DATE = "exercise_date";

    // Table Create Statements
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERNAME + " TEXT UNIQUE NOT NULL,"
            + KEY_PASSWORD + " TEXT NOT NULL,"
            + KEY_ROLE + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_CHECKIN = "CREATE TABLE " + TABLE_CHECKIN + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_CHECKIN_DATE + " TEXT)";

    private static final String CREATE_TABLE_APPOINTMENT = "CREATE TABLE " + TABLE_APPOINTMENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_DEPARTMENT + " TEXT,"
            + KEY_APPOINTMENT_TIME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT)";

    private static final String CREATE_TABLE_DIET = "CREATE TABLE " + TABLE_DIET + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_MEAL_TYPE + " TEXT,"
            + KEY_FOOD_CONTENT + " TEXT,"
            + KEY_RECORD_DATE + " TEXT)";

    private static final String CREATE_TABLE_EXERCISE = "CREATE TABLE " + TABLE_EXERCISE + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_EXERCISE_TYPE + " TEXT,"
            + KEY_EXERCISE_DATE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CHECKIN);
        db.execSQL(CREATE_TABLE_APPOINTMENT);
        db.execSQL(CREATE_TABLE_DIET);
        db.execSQL(CREATE_TABLE_EXERCISE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        // create new tables
        onCreate(db);
    }

    //--------------------------------- CRUD for User Table ---------------------------------//

    // Add a new user
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_ROLE, user.getRole());
        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    // Get a single user by username and password (for login)
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_ROLE},
                KEY_USERNAME + "=?", new String[]{username}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            cursor.close();
            db.close();
            return user;
        }
        db.close();
        return null;
    }

    // Get all users
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setUsername(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setRole(cursor.getString(3));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    // Update a user
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_ROLE, user.getRole());
        int rowsAffected = db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        return rowsAffected;
    }

    // Delete a user
    public void deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }


    //------------------------------- CRUD for CheckIn Table --------------------------------//

    public long addCheckIn(CheckIn checkIn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, checkIn.getUserId());
        values.put(KEY_CHECKIN_DATE, checkIn.getCheckinDate());
        long id = db.insert(TABLE_CHECKIN, null, values);
        db.close();
        return id;
    }

    public List<CheckIn> getAllCheckInsForUser(int userId) {
        List<CheckIn> checkInList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHECKIN + " WHERE " + KEY_USER_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Looping through all rows and adding to list...
        // ... (similar to getAllUsers)
        cursor.close();
        db.close();
        return checkInList;
    }

    // 更新签到记录
    public int updateCheckIn(CheckIn checkIn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, checkIn.getUserId());
        values.put(KEY_CHECKIN_DATE, checkIn.getCheckinDate());

        // 根据签到记录ID更新数据
        int rowsAffected = db.update(TABLE_CHECKIN, values, KEY_ID + " = ?",
                new String[]{String.valueOf(checkIn.getId())});
        db.close();
        return rowsAffected;
    }

    // 删除签到记录
    public void deleteCheckIn(long checkInId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHECKIN, KEY_ID + " = ?",
                new String[]{String.valueOf(checkInId)});
        db.close();
    }

    // 删除用户的所有签到记录
    public void deleteAllCheckInsForUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHECKIN, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }


    // ... (Update and Delete for CheckIn can be added if needed)


    //----------------------------- CRUD for Appointment Table ------------------------------//

    public long addAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, appointment.getUserId());
        values.put(KEY_DEPARTMENT, appointment.getDepartment());
        values.put(KEY_APPOINTMENT_TIME, appointment.getAppointmentTime());
        values.put(KEY_DESCRIPTION, appointment.getDescription());
        long id = db.insert(TABLE_APPOINTMENT, null, values);
        db.close();
        return id;
    }

    public List<Appointment> getAllAppointmentsForUser(int userId) {
        List<Appointment> appointmentList = new ArrayList<>();
        // ... (Implement logic similar to getAllUsers)
        return appointmentList;
    }


    //--------------------------------- CRUD for Diet Table ---------------------------------//

    public long addDietLog(Diet diet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, diet.getUserId());
        values.put(KEY_MEAL_TYPE, diet.getMealType());
        values.put(KEY_FOOD_CONTENT, diet.getFoodContent());
        values.put(KEY_RECORD_DATE, diet.getRecordDate());
        long id = db.insert(TABLE_DIET, null, values);
        db.close();
        return id;
    }

    public List<Diet> getAllDietLogsForUser(int userId) {
        List<Diet> dietList = new ArrayList<>();
        // ... (Implement logic similar to getAllUsers)
        return dietList;
    }


    //------------------------------- CRUD for Exercise Table -------------------------------//

    public long addExerciseLog(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, exercise.getUserId());
        values.put(KEY_EXERCISE_TYPE, exercise.getExerciseType());
        values.put(KEY_EXERCISE_DATE, exercise.getExerciseDate());
        long id = db.insert(TABLE_EXERCISE, null, values);
        db.close();
        return id;
    }

    public List<Exercise> getAllExerciseLogsForUser(int userId) {
        List<Exercise> exerciseList = new ArrayList<>();
        // ... (Implement logic similar to getAllUsers)
        return exerciseList;
    }

//----------------------------- Appointment CRUD 补充方法 -----------------------------//

    // 更新预约信息
    public int updateAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, appointment.getUserId());
        values.put(KEY_DEPARTMENT, appointment.getDepartment());
        values.put(KEY_APPOINTMENT_TIME, appointment.getAppointmentTime());
        values.put(KEY_DESCRIPTION, appointment.getDescription());

        int rowsAffected = db.update(TABLE_APPOINTMENT, values, KEY_ID + " = ?",
                new String[]{String.valueOf(appointment.getId())});
        db.close();
        return rowsAffected;
    }

    // 删除单个预约
    public void deleteAppointment(long appointmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPOINTMENT, KEY_ID + " = ?",
                new String[]{String.valueOf(appointmentId)});
        db.close();
    }

    // 删除用户的所有预约
    public void deleteAllAppointmentsForUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPOINTMENT, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

//--------------------------------- Diet CRUD 补充方法 ---------------------------------//

    // 更新饮食记录
    public int updateDiet(Diet diet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, diet.getUserId());
        values.put(KEY_MEAL_TYPE, diet.getMealType());
        values.put(KEY_FOOD_CONTENT, diet.getFoodContent());
        values.put(KEY_RECORD_DATE, diet.getRecordDate());

        int rowsAffected = db.update(TABLE_DIET, values, KEY_ID + " = ?",
                new String[]{String.valueOf(diet.getId())});
        db.close();
        return rowsAffected;
    }

    // 删除单条饮食记录
    public void deleteDiet(long dietId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIET, KEY_ID + " = ?",
                new String[]{String.valueOf(dietId)});
        db.close();
    }

    // 删除用户的所有饮食记录
    public void deleteAllDietsForUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIET, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

//------------------------------- Exercise CRUD 补充方法 -------------------------------//

    // 更新运动记录
    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, exercise.getUserId());
        values.put(KEY_EXERCISE_TYPE, exercise.getExerciseType());
        values.put(KEY_EXERCISE_DATE, exercise.getExerciseDate());

        int rowsAffected = db.update(TABLE_EXERCISE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(exercise.getId())});
        db.close();
        return rowsAffected;
    }

    // 删除单条运动记录
    public void deleteExercise(long exerciseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISE, KEY_ID + " = ?",
                new String[]{String.valueOf(exerciseId)});
        db.close();
    }

    // 删除用户的所有运动记录
    public void deleteAllExercisesForUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISE, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

    public User getUserById(int loggedInUserId) {

        // 获取用户信息
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_ROLE},
                KEY_ID + "=?", new String[]{String.valueOf(loggedInUserId)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            cursor.close();
            db.close();
            return user;

        } else {
            cursor.close();
            db.close();
            return null;
        }
    }
}
