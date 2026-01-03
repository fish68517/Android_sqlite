package com.myapplication.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentAssistant.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    public static final String TABLE_COURSE = "courses";
    public static final String TABLE_HOMEWORK = "homework";
    public static final String TABLE_EXAM = "exams";
    public static final String TABLE_USER = "users";

    // 通用列
    public static final String COLUMN_ID = "_id";

    // 课程表列
    public static final String COL_COURSE_NAME = "name";
    public static final String COL_COURSE_TIME = "time";
    public static final String COL_COURSE_PLACE = "place";

    // 作业表列
    public static final String COL_HW_CONTENT = "content";
    public static final String COL_HW_DEADLINE = "deadline";

    // 考试表列
    public static final String COL_EXAM_NAME = "name";
    public static final String COL_EXAM_TIME = "time";
    public static final String COL_EXAM_REMIND = "remind_time";

    // 用户表列
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_PASS = "password";




    // 1. 新增表名和字段
    public static final String TABLE_MEDIA = "media";
    public static final String COL_MEDIA_NAME = "name";
    public static final String COL_MEDIA_PATH = "path"; // 存 assets 下的文件名
    public static final String COL_MEDIA_AUTHOR = "author";



    public StudentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 验证登录
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_ID},
                COL_USER_NAME + "=? AND " + COL_USER_PASS + "=?",
                new String[]{username, password}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 检查用户名是否存在
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_ID},
                COL_USER_NAME + "=?",
                new String[]{username}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 注册用户
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, username);
        values.put(COL_USER_PASS, password);
        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    // 3. 辅助方法：添加音频 (用于 MediaFragment 调用)
    public void addMedia(String name, String path, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MEDIA_NAME, name);
        values.put(COL_MEDIA_PATH, path);
        values.put(COL_MEDIA_AUTHOR, author);
        db.insert(TABLE_MEDIA, null, values);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. 创建用户表
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_PASS + " TEXT)";
        db.execSQL(createUserTable);

        // 2. 创建音频表
        String createMediaTable = "CREATE TABLE " + TABLE_MEDIA + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEDIA_NAME + " TEXT, " +
                COL_MEDIA_PATH + " TEXT, " +
                COL_MEDIA_AUTHOR + " TEXT)";
        db.execSQL(createMediaTable);

        // 2. 创建课程表
        String createCourseTable = "CREATE TABLE " + TABLE_COURSE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COURSE_NAME + " TEXT, " +
                COL_COURSE_TIME + " TEXT, " +
                COL_COURSE_PLACE + " TEXT)";
        db.execSQL(createCourseTable);

        // 3. 创建作业表
        String createHwTable = "CREATE TABLE " + TABLE_HOMEWORK + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HW_CONTENT + " TEXT, " +
                COL_HW_DEADLINE + " TEXT)";
        db.execSQL(createHwTable);

        // 4. 创建考试表
        String createExamTable = "CREATE TABLE " + TABLE_EXAM + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXAM_NAME + " TEXT, " +
                COL_EXAM_TIME + " TEXT, " +
                COL_EXAM_REMIND + " INTEGER)";
        db.execSQL(createExamTable);

        // 5. 初始化默认数据 (关键修改)
        initData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOMEWORK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    /**
     * 初始化默认数据
     * 包含10条课程、10条作业、10条考试数据
     */
    private void initData(SQLiteDatabase db) {
        // --- 插入 10 条课程数据 ---
        // 格式：insertCourse(db, "课程名", "时间", "地点");
        insertCourse(db, "高等数学(上)", "周一 08:00-09:35", "教学楼 A101");
        insertCourse(db, "大学英语(III)", "周二 10:00-11:35", "外语楼 305");
        insertCourse(db, "Android应用开发", "周三 14:00-15:35", "计算机中心 机房C");
        insertCourse(db, "计算机网络原理", "周四 08:00-09:35", "理工楼 B202");
        insertCourse(db, "数据结构与算法", "周五 10:00-11:35", "教学楼 C104");
        insertCourse(db, "毛泽东思想概论", "周一 14:00-15:35", "综合楼 大礼堂");
        insertCourse(db, "大学体育(IV)", "周二 16:00-17:35", "北区体育馆");
        insertCourse(db, "操作系统", "周三 08:00-09:35", "理工楼 A303");
        insertCourse(db, "线性代数", "周四 14:00-15:35", "教学楼 B102");
        insertCourse(db, "数据库系统概论", "周五 14:00-15:35", "计算机中心 机房A");

        // --- 插入 10 条作业数据 ---
        // 格式：insertHomework(db, "作业内容", "截止日期");
        insertHomework(db, "完成高数第三章习题集", "2026-03-15");
        insertHomework(db, "撰写英语作文《My Campus》", "2026-03-18");
        insertHomework(db, "实现Android计算器界面布局", "2026-03-20");
        insertHomework(db, "抓包分析TCP三次握手过程", "2026-03-22");
        insertHomework(db, "用C语言实现单链表反转", "2026-03-25");
        insertHomework(db, "撰写社会实践调查报告", "2026-04-01");
        insertHomework(db, "录制太极拳练习视频", "2026-04-05");
        insertHomework(db, "分析进程调度算法优缺点", "2026-04-10");
        insertHomework(db, "完成矩阵运算书面作业", "2026-04-12");
        insertHomework(db, "设计图书管理系统E-R图", "2026-04-15");

        // --- 插入 10 条考试数据 ---
        // 格式：insertExam(db, "考试科目", "考试时间");
        insertExam(db, "高等数学期中考试", "2026-04-20 09:00");
        insertExam(db, "大学英语四级模拟考", "2026-04-22 14:00");
        insertExam(db, "Android阶段性上机测试", "2026-04-25 10:00");
        insertExam(db, "计算机网络期末闭卷", "2026-06-15 09:00");
        insertExam(db, "数据结构期末机考", "2026-06-18 14:00");
        insertExam(db, "毛概期末开卷考试", "2026-06-20 09:00");
        insertExam(db, "体育1000米体能测试", "2026-06-22 16:00");
        insertExam(db, "操作系统期末笔试", "2026-06-25 09:00");
        insertExam(db, "线性代数全校统考", "2026-06-28 14:00");
        insertExam(db, "数据库课程设计答辩", "2026-07-02 08:30");
    }

    // 辅助方法：插入课程
    private void insertCourse(SQLiteDatabase db, String name, String time, String place) {
        ContentValues values = new ContentValues();
        values.put(COL_COURSE_NAME, name);
        values.put(COL_COURSE_TIME, time);
        values.put(COL_COURSE_PLACE, place);
        db.insert(TABLE_COURSE, null, values);
    }

    // 辅助方法：插入作业
    private void insertHomework(SQLiteDatabase db, String content, String deadline) {
        ContentValues values = new ContentValues();
        values.put(COL_HW_CONTENT, content);
        values.put(COL_HW_DEADLINE, deadline);
        db.insert(TABLE_HOMEWORK, null, values);
    }

    // 辅助方法：插入考试
    private void insertExam(SQLiteDatabase db, String name, String time) {
        ContentValues values = new ContentValues();
        values.put(COL_EXAM_NAME, name);
        values.put(COL_EXAM_TIME, time);
        values.put(COL_EXAM_REMIND, 0); // 默认提醒状态为0
        db.insert(TABLE_EXAM, null, values);
    }
}