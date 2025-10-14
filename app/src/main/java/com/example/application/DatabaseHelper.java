package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.application.model.LeaveRequest;
import com.example.application.model.Notification; // 新增导入


import com.example.application.model.Appointment;
import com.example.application.model.CheckIn;
import com.example.application.model.Diet;
import com.example.application.model.Exercise;
import com.example.application.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final int DATABASE_VERSION = 4;
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

    private static final String TABLE_NOTIFICATION = "notification_table"; // 新增通知表

    // NOTIFICATION Table - column names
    private static final String KEY_TITLE = "title"; // 新增
    private static final String KEY_CONTENT = "content"; // 新增


    private static final String TABLE_LEAVE = "leave_table"; // 新增请假表

    // LEAVE Table - column names
    private static final String KEY_REASON = "reason";
    private static final String KEY_LEAVE_TIME = "leave_time";
    private static final String KEY_STATUS = "status";

    // 新增：创建请假表的语句
    private static final String CREATE_TABLE_LEAVE = "CREATE TABLE " + TABLE_LEAVE + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_REASON + " TEXT,"
            + KEY_LEAVE_TIME + " TEXT,"
            + KEY_STATUS + " TEXT)";

    // 新增：创建通知表的语句
    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TITLE + " TEXT,"
            + KEY_CONTENT + " TEXT)";

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
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_LEAVE); // 添加建表语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAVE); // 添加删表语句
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
        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE + " WHERE " + KEY_USER_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                exercise.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
                exercise.setExerciseType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE_TYPE)));
                exercise.setExerciseDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE_DATE)));
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }
        cursor.close();
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

    // Get all students
    public List<User> getAllStudents() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + KEY_ROLE + " = '学生'";
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

    // Get all user IDs that have checked in today
    public List<Integer> getTodayCheckInUserIds(String date) {
        List<Integer> userIds = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_USER_ID + " FROM " + TABLE_CHECKIN + " WHERE " + KEY_CHECKIN_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                userIds.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userIds;
    }

    // Get all appointments for the doctor
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointmentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_APPOINTMENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Appointment appointment = new Appointment();
                appointment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                appointment.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
                appointment.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
                appointment.setAppointmentTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_TIME)));
                appointment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                appointmentList.add(appointment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appointmentList;
    }

    public ArrayList<Notification> getAllNotifications() {

        ArrayList<Notification> notificationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                notification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                notification.setContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTENT)));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notificationList;
    }

    public void deleteNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATION, KEY_ID + " = ?",
                new String[]{String.valueOf(notification.getId())});
        db.close();
    }

    public void addNotification(Notification newNotification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, newNotification.getTitle());
        values.put(KEY_CONTENT, newNotification.getContent());
        db.insert(TABLE_NOTIFICATION, null, values);
        db.close();
    }

    public void updateNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, notification.getTitle());
        values.put(KEY_CONTENT, notification.getContent());
        db.update(TABLE_NOTIFICATION, values, KEY_ID + " = ?",
                new String[]{String.valueOf(notification.getId())});
        db.close();
    }


    //----------------------------- CRUD for LeaveRequest Table -----------------------------//

    // 添加请假申请
    public long addLeaveRequest(LeaveRequest leaveRequest) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, leaveRequest.getUserId());
        values.put(KEY_REASON, leaveRequest.getReason());
        values.put(KEY_LEAVE_TIME, leaveRequest.getLeaveTime());
        values.put(KEY_STATUS, leaveRequest.getStatus());
        long id = db.insert(TABLE_LEAVE, null, values);
        db.close();
        return id;
    }

    // 获取所有请假申请 (老师用)
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequestList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LEAVE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LeaveRequest lr = new LeaveRequest();
                lr.setId(cursor.getInt(0));
                lr.setUserId(cursor.getInt(1));
                lr.setReason(cursor.getString(2));
                lr.setLeaveTime(cursor.getString(3));
                lr.setStatus(cursor.getString(4));
                leaveRequestList.add(lr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return leaveRequestList;
    }

    // 更新请假申请状态 (老师用)
    public int updateLeaveRequestStatus(LeaveRequest leaveRequest) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, leaveRequest.getStatus());
        return db.update(TABLE_LEAVE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(leaveRequest.getId())});
    }
}
