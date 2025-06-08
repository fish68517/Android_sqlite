package com.archive.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.archive.app.model.User;
import com.archive.app.model.Student;

import java.util.ArrayList;
import java.util.List;

public class OpenHelperDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "textbook_system.db";
    private static final int DATABASE_VERSION = 2;

    // 用户表
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // 学生表
    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_STUDENT_PK_ID = "_id";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_STUDENT_PHONE = "phone";
    public static final String COLUMN_STUDENT_BIO = "bio";
    public static final String COLUMN_STUDENT_AVATAR_PATH = "avatar_path";

    // 创建用户表的SQL语句
    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

    // 创建学生表的SQL语句
    private static final String TABLE_CREATE_STUDENTS =
            "CREATE TABLE " + TABLE_STUDENTS + " (" +
                    COLUMN_STUDENT_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STUDENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_STUDENT_ID + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_STUDENT_PHONE + " TEXT, " +
                    COLUMN_STUDENT_BIO + " TEXT, " +
                    COLUMN_STUDENT_AVATAR_PATH + " TEXT)";

    private static final String TAG = "OpenHelperDataBase";

    public OpenHelperDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "数据库帮助类已创建。");
    }

    // 用户注册
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);

        long result = -1;
        try {
            result = db.insert(TABLE_USERS, null, values);
            if (result != -1) {
                Log.i(TAG, "用户注册成功: " + username);
            } else {
                Log.e(TAG, "用户注册失败: " + username);
            }
        } catch (Exception e) {
            Log.e(TAG, "registerUser: 注册用户时发生错误", e);
        } finally {
            db.close();
        }
        return result != -1;
    }

    // 用户登录
    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null,
                    COLUMN_USER_NAME + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                    new String[]{username, password}, null, null, null);
            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
                Log.i(TAG, "用户登录成功: " + username);
            } else {
                Log.w(TAG, "用户登录失败或用户不存在: " + username);
            }
        } catch (Exception e) {
            Log.e(TAG, "loginUser: 登录用户时发生错误", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return user;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "正在创建数据库表...");

        Log.i(TAG, "正在创建用户表 (" + TABLE_USERS + ")...");
        db.execSQL(TABLE_CREATE_USERS);
        Log.i(TAG, "用户表 (" + TABLE_USERS + ") 创建成功。");

        Log.i(TAG, "正在创建学生表 (" + TABLE_STUDENTS + ")...");
        db.execSQL(TABLE_CREATE_STUDENTS);
        Log.i(TAG, "学生表 (" + TABLE_STUDENTS + ") 创建成功。");

        Log.i(TAG, "数据库表创建完成，正在插入模拟数据...");
        insertMockData(db);
        Log.i(TAG, "模拟数据插入完成。");
    }

    private void insertMockData(SQLiteDatabase db) {
        // 插入一个用户
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_NAME, "testuser");
        userValues.put(COLUMN_USER_PASSWORD, "123456");
        long userId = db.insert(TABLE_USERS, null, userValues);

        // 插入一些学生
        ContentValues studentValues = new ContentValues();
        studentValues.put(COLUMN_STUDENT_NAME, "张三");
        studentValues.put(COLUMN_STUDENT_ID, "2023001");
        studentValues.put(COLUMN_STUDENT_PHONE, "13800138000");
        studentValues.put(COLUMN_STUDENT_BIO, "这是张三的简介。");
        db.insert(TABLE_STUDENTS, null, studentValues);

        studentValues.put(COLUMN_STUDENT_NAME, "李四");
        studentValues.put(COLUMN_STUDENT_ID, "2023002");
        studentValues.put(COLUMN_STUDENT_PHONE, "13900139000");
        studentValues.put(COLUMN_STUDENT_BIO, "这是李四的简介。");
        db.insert(TABLE_STUDENTS, null, studentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "正在升级数据库，版本从 " + oldVersion + " 到 " + newVersion + "。旧数据将被删除。");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);

        Log.i(TAG, "旧表已删除。");
        onCreate(db);
    }

    // 获取所有用户记录（管理员/全局）
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null, null, null, null, null, COLUMN_USER_ID + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    User record = new User();
                    record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                    record.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));

                    list.add(record);
                } while (cursor.moveToNext());
            }
            Log.i(TAG, "成功获取所有用户记录，数量: " + list.size());
        } catch (Exception e) {
            Log.e(TAG, "getAllUsers: 查询所有用户记录时发生错误", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // 删除用户记录
    public boolean deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = -1;
        try {
            result = db.delete(TABLE_USERS, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
            if (result > 0) {
                Log.i(TAG, "成功删除用户记录, ID: " + userId);
            } else {
                Log.w(TAG, "删除用户记录失败或用户不存在, ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteUser: 删除用户记录时发生错误", e);
        } finally {
            db.close();
        }
        return result > 0;
    }

    // ---- 用户表 (User) 操作 ----
    /**
     * 更新用户信息 (用户名和密码)
     * @param userId 用户ID
     * @param newUsername 新用户名
     * @param newPassword 新密码
     * @return 受影响的行数
     */
    public int updateUserProfile(long userId, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, newUsername);
        values.put(COLUMN_USER_PASSWORD, newPassword);
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "用户资料更新成功, ID: " + userId);
            } else {
                Log.w(TAG, "用户资料更新失败或用户不存在, ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "updateUserProfile: 更新用户资料时发生错误", e);
        } finally {
            db.close();
        }
        return rowsAffected;
    }

    // ---- 学生表 (Student) 操作 ----

    /**
     * 新增学生信息
     * @param student 学生对象 (不含id)
     * @return 新插入行的id，如果发生错误则为-1
     */
    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, student.getName());
        values.put(COLUMN_STUDENT_ID, student.getStudentId());
        values.put(COLUMN_STUDENT_PHONE, student.getPhone());
        values.put(COLUMN_STUDENT_BIO, student.getBio());
        values.put(COLUMN_STUDENT_AVATAR_PATH, student.getAvatarPath());

        long result = -1;
        try {
            result = db.insert(TABLE_STUDENTS, null, values);
            if (result != -1) {
                Log.i(TAG, "成功新增学生: " + student.getName());
            } else {
                Log.e(TAG, "新增学生失败: " + student.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "addStudent: 新增学生时发生错误", e);
        } finally {
            db.close();
        }
        return result;
    }

    /**
     * 根据主键ID获取学生信息
     * @param id 主键
     * @return 学生对象，未找到则为null
     */
    public Student getStudent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Student student = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENTS, null,
                    COLUMN_STUDENT_PK_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                student = cursorToStudent(cursor);
                Log.i(TAG, "成功获取学生信息: " + student.getName());
            } else {
                Log.w(TAG, "未找到ID为 " + id + " 的学生");
            }
        } catch (Exception e) {
            Log.e(TAG, "getStudent: 查询学生时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return student;
    }

    /**
     * 获取所有学生信息列表
     * @return 学生对象列表
     */
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENTS, null, null, null, null, null, COLUMN_STUDENT_PK_ID + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursorToStudent(cursor));
                } while (cursor.moveToNext());
            }
            Log.i(TAG, "成功获取所有学生记录，数量: " + list.size());
        } catch (Exception e) {
            Log.e(TAG, "getAllStudents: 查询所有学生时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            // db is not closed here to allow cursor to be used by adapter, close it later
        }
        return list;
    }

    /**
     * 更新学生信息
     * @param student 包含新信息的学生对象 (必须有id)
     * @return 受影响的行数
     */
    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, student.getName());
        values.put(COLUMN_STUDENT_ID, student.getStudentId());
        values.put(COLUMN_STUDENT_PHONE, student.getPhone());
        values.put(COLUMN_STUDENT_BIO, student.getBio());
        values.put(COLUMN_STUDENT_AVATAR_PATH, student.getAvatarPath());
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_PK_ID + "=?",
                    new String[]{String.valueOf(student.getId())});
            if (rowsAffected > 0) {
                Log.i(TAG, "学生信息更新成功, ID: " + student.getId());
            } else {
                Log.w(TAG, "学生信息更新失败或学生不存在, ID: " + student.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "updateStudent: 更新学生信息时发生错误", e);
        } finally {
            db.close();
        }
        return rowsAffected;
    }

    /**
     * 删除学生信息
     * @param id 要删除的学生的ID
     * @return 如果删除成功返回true
     */
    public boolean deleteStudent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = -1;
        try {
            result = db.delete(TABLE_STUDENTS, COLUMN_STUDENT_PK_ID + "=?", new String[]{String.valueOf(id)});
            if (result > 0) {
                Log.i(TAG, "成功删除学生记录, ID: " + id);
            } else {
                Log.w(TAG, "删除学生记录失败或学生不存在, ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteStudent: 删除学生时发生错误", e);
        } finally {
            db.close();
        }
        return result > 0;
    }

    /**
     * 将Cursor转换成Student对象
     * @param cursor 数据集
     * @return Student对象
     */
    private Student cursorToStudent(Cursor cursor) {
        Student student = new Student();
        student.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_PK_ID)));
        student.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)));
        student.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
        student.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_PHONE)));
        student.setBio(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_BIO)));
        student.setAvatarPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_AVATAR_PATH)));
        return student;
    }
} 