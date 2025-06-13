package com.example.studentmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

/**
 * 数据库帮助类
 * 负责数据库的创建和升级
 */
public class StudentDBHelper extends SQLiteOpenHelper {
    // 数据库名称
    private static final String DATABASE_NAME = "student_manager.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    public StudentDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建班级表
        db.execSQL("CREATE TABLE classes (" +
                "class_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "class_name VARCHAR(50) NOT NULL," +
                "class_type VARCHAR(20) NOT NULL," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

        // 创建学生表
        db.execSQL("CREATE TABLE students (" +
                "student_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(50) NOT NULL," +
                "gender VARCHAR(10)," +
                "class_id INTEGER," +
                "admission_date DATE," +
                "graduation_date DATE," +
                "status VARCHAR(20) DEFAULT '在校'," +
                "username VARCHAR(50)," +
                "password VARCHAR(50)," +
                "is_admin INTEGER DEFAULT 0," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (class_id) REFERENCES classes(class_id)" +
                ")");

        // 创建学籍状态变更记录表
        db.execSQL("CREATE TABLE student_status_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id INTEGER," +
                "status VARCHAR(20) NOT NULL," +
                "change_date DATE NOT NULL," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id)" +
                ")");

        // 创建索引
        db.execSQL("CREATE INDEX idx_students_class_id ON students(class_id)");
        db.execSQL("CREATE INDEX idx_students_status ON students(status)");
        db.execSQL("CREATE INDEX idx_students_username ON students(username)");
        db.execSQL("CREATE INDEX idx_students_is_admin ON students(is_admin)");
        db.execSQL("CREATE INDEX idx_student_status_history_student_id ON student_status_history(student_id)");
        db.execSQL("CREATE INDEX idx_student_status_history_change_date ON student_status_history(change_date)");

        // 插入班级数据
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('17移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('18移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('19移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('20移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('21移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('22移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('23移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('24移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('21移动本1-8班', '移动应用开发')");
        db.execSQL("INSERT INTO classes (class_name, class_type) VALUES ('22移动本1-8班', '移动应用开发')");

        // 插入学生数据
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, graduation_date, status, username, password) VALUES ('陈一', '男', 2, '2018-09-01', '2022-07-01', '毕业', 'chenyi', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, graduation_date, status, username, password) VALUES ('林二', '女', 3, '2019-09-01', '2023-07-01', '毕业', 'liner', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('张三', '男', 5, '2021-09-01', '在校', 'zhangsan', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('李四', '女', 5, '2021-09-01', '在校', 'lisi', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('王五', '男', 9, '2021-09-01', '在校', 'wangwu', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('赵六', '女', 6, '2022-09-01', '在校', 'zhaoliu', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('孙七', '男', 10, '2022-09-01', '在校', 'sunqi', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('周八', '女', 7, '2023-09-01', '在校', 'zhouba', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('吴九', '男', 7, '2023-09-01', '在校', 'wujiu', '123456')");
        db.execSQL("INSERT INTO students (name, gender, class_id, admission_date, status, username, password) VALUES ('郑十', '女', 8, '2024-09-01', '在校', 'zhengshi', '123456')");

        // 插入学籍状态变更记录
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (1, '入学', '2018-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (1, '在校', '2018-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (1, '毕业', '2022-07-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (2, '入学', '2019-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (2, '在校', '2019-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (2, '毕业', '2023-07-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (3, '入学', '2021-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (3, '在校', '2021-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (4, '入学', '2021-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (4, '在校', '2021-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (5, '入学', '2021-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (5, '在校', '2021-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (6, '入学', '2022-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (6, '在校', '2022-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (7, '入学', '2022-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (7, '在校', '2022-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (8, '入学', '2023-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (8, '在校', '2023-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (9, '入学', '2023-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (9, '在校', '2023-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (10, '入学', '2024-09-01')");
        db.execSQL("INSERT INTO student_status_history (student_id, status, change_date) VALUES (10, '在校', '2024-09-01')");
    }

    /**
     * 添加班级
     * @param className 班级名称
     * @return a long
     */
    public long addClass(String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_name", className);
        values.put("class_type", "未分类");
        long result = db.insert("classes", null, values);
        db.close();
        return result;
    }

    /**
     * 删除学生
     * @param studentId 学生ID
     */
    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("students", "student_id = ?", new String[]{String.valueOf(studentId)});
        db.close();
    }

    /**
     * 删除班级及其所有学生
     * @param classId 班级ID
     */
    public void deleteClass(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 首先删除该班级下的所有学生
        db.delete("students", "class_id = ?", new String[]{String.valueOf(classId)});
        // 然后删除班级
        db.delete("classes", "class_id = ?", new String[]{String.valueOf(classId)});
        db.close();
    }

    /**
     * 更新班级信息
     * @param classId 班级ID
     * @param className 新的班级名称
     * @return a int
     */
    public int updateClass(int classId, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_name", className);
        int rows = db.update("classes", values, "class_id = ?", new String[]{String.valueOf(classId)});
        db.close();
        return rows;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级逻辑
        if (oldVersion < 2) {
            // 未来版本升级时在这里添加升级逻辑
        }
    }
} 