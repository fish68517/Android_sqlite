package com.archive.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.archive.app.model.User;

import java.util.ArrayList;
import java.util.List;

import com.archive.app.model.Category;
import com.archive.app.model.Note;

public class OpenHelperDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "textbook_system.db";
    private static final int DATABASE_VERSION = 2;

    // 用户表
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // 分类表
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_USER_ID = "user_id";

    // 笔记表
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String COLUMN_NOTE_IMAGE = "image";
    public static final String COLUMN_NOTE_CATEGORY_ID = "category_id";
    public static final String COLUMN_NOTE_USER_ID = "user_id";
    public static final String COLUMN_NOTE_CREATED_AT = "created_at";
    public static final String COLUMN_NOTE_UPDATED_AT = "updated_at";

    // 回收站表
    public static final String TABLE_DELETED_NOTES = "deleted_notes";
    public static final String COLUMN_DELETED_NOTE_ID = "_id"; // The original note ID
    public static final String COLUMN_DELETED_NOTE_TITLE = "title";
    public static final String COLUMN_DELETED_NOTE_CONTENT = "content";
    public static final String COLUMN_DELETED_NOTE_IMAGE = "image";
    public static final String COLUMN_DELETED_NOTE_CATEGORY_ID = "category_id";
    public static final String COLUMN_DELETED_NOTE_USER_ID = "user_id";
    public static final String COLUMN_DELETED_NOTE_CREATED_AT = "created_at";
    public static final String COLUMN_DELETED_NOTE_UPDATED_AT = "updated_at";
    public static final String COLUMN_DELETED_NOTE_DELETED_AT = "deleted_at";



    // 创建用户表的SQL语句
    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

    // 创建分类表的SQL语句
    private static final String TABLE_CREATE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_USER_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_CATEGORY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    // 创建笔记表的SQL语句
    private static final String TABLE_CREATE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                    COLUMN_NOTE_CONTENT + " TEXT, " +
                    COLUMN_NOTE_IMAGE + " BLOB, " +
                    COLUMN_NOTE_CATEGORY_ID + " INTEGER, " +
                    COLUMN_NOTE_USER_ID + " INTEGER, " +
                    COLUMN_NOTE_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_NOTE_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + COLUMN_NOTE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_NOTE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    // 创建回收站表的SQL语句
    private static final String TABLE_CREATE_DELETED_NOTES =
            "CREATE TABLE " + TABLE_DELETED_NOTES + " (" +
                    COLUMN_DELETED_NOTE_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_DELETED_NOTE_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DELETED_NOTE_CONTENT + " TEXT, " +
                    COLUMN_DELETED_NOTE_IMAGE + " BLOB, " +
                    COLUMN_DELETED_NOTE_CATEGORY_ID + " INTEGER, " +
                    COLUMN_DELETED_NOTE_USER_ID + " INTEGER, " +
                    COLUMN_DELETED_NOTE_CREATED_AT + " DATETIME, " +
                    COLUMN_DELETED_NOTE_UPDATED_AT + " DATETIME, " +
                    COLUMN_DELETED_NOTE_DELETED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";



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

        Log.i(TAG, "正在创建分类表 (" + TABLE_CATEGORIES + ")...");
        db.execSQL(TABLE_CREATE_CATEGORIES);
        Log.i(TAG, "分类表 (" + TABLE_CATEGORIES + ") 创建成功。");

        Log.i(TAG, "正在创建笔记表 (" + TABLE_NOTES + ")...");
        db.execSQL(TABLE_CREATE_NOTES);
        Log.i(TAG, "笔记表 (" + TABLE_NOTES + ") 创建成功。");

        Log.i(TAG, "正在创建回收站表 (" + TABLE_DELETED_NOTES + ")...");
        db.execSQL(TABLE_CREATE_DELETED_NOTES);
        Log.i(TAG, "回收站表 (" + TABLE_DELETED_NOTES + ") 创建成功。");

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

        if (userId != -1) {
            // 插入分类
            ContentValues categoryValues1 = new ContentValues();
            categoryValues1.put(COLUMN_CATEGORY_NAME, "工作笔记");
            categoryValues1.put(COLUMN_CATEGORY_USER_ID, userId);
            long categoryId1 = db.insert(TABLE_CATEGORIES, null, categoryValues1);

            ContentValues categoryValues2 = new ContentValues();
            categoryValues2.put(COLUMN_CATEGORY_NAME, "生活杂记");
            categoryValues2.put(COLUMN_CATEGORY_USER_ID, userId);
            long categoryId2 = db.insert(TABLE_CATEGORIES, null, categoryValues2);

            // 插入笔记
            if (categoryId1 != -1) {
                ContentValues noteValues1 = new ContentValues();
                noteValues1.put(COLUMN_NOTE_TITLE, "关于安卓开发的第一次会议");
                noteValues1.put(COLUMN_NOTE_CONTENT, "会议记录：讨论了MVP架构，并确定了数据库设计。");
                noteValues1.put(COLUMN_NOTE_USER_ID, userId);
                noteValues1.put(COLUMN_NOTE_CATEGORY_ID, categoryId1);
                db.insert(TABLE_NOTES, null, noteValues1);
            }

            if (categoryId2 != -1) {
                ContentValues noteValues2 = new ContentValues();
                noteValues2.put(COLUMN_NOTE_TITLE, "购物清单");
                noteValues2.put(COLUMN_NOTE_CONTENT, "牛奶、面包、水果。");
                noteValues2.put(COLUMN_NOTE_USER_ID, userId);
                noteValues2.put(COLUMN_NOTE_CATEGORY_ID, categoryId2);
                db.insert(TABLE_NOTES, null, noteValues2);
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "正在升级数据库，版本从 " + oldVersion + " 到 " + newVersion + "。旧数据将被删除。");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

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

    // ---- 分类表 (Category) 操作 ----

    public long addCategory(String name, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        values.put(COLUMN_CATEGORY_USER_ID, userId);
        long id = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    public List<Category> getAllCategories(long userId) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, COLUMN_CATEGORY_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                category.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_USER_ID)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    public int updateCategory(long categoryId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, newName);
        int rows = db.update(TABLE_CATEGORIES, values, COLUMN_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)});
        db.close();
        return rows;
    }

    public void deleteCategory(long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 将该分类下的笔记的 category_id 设为 null
        ContentValues values = new ContentValues();
        values.putNull(COLUMN_NOTE_CATEGORY_ID);
        db.update(TABLE_NOTES, values, COLUMN_NOTE_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)});

        // 删除分类
        db.delete(TABLE_CATEGORIES, COLUMN_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)});
        db.close();
    }


    // ---- 笔记表 (Note) 操作 ----

    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getContent());
        values.put(COLUMN_NOTE_IMAGE, note.getImage());
        values.put(COLUMN_NOTE_CATEGORY_ID, note.getCategoryId());
        values.put(COLUMN_NOTE_USER_ID, note.getUserId());
        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    public Note getNote(long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)}, null, null, null);
        Note note = null;
        if (cursor.moveToFirst()) {
            note = cursorToNote(cursor);
        }
        cursor.close();
        db.close();
        return note;
    }

    public List<Note> getAllNotes(long userId) {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTE_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_NOTE_UPDATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                noteList.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public List<Note> getNotesByCategory(long userId, long categoryId) {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTE_USER_ID + "=? AND " + COLUMN_NOTE_CATEGORY_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(categoryId)}, null, null, COLUMN_NOTE_UPDATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                noteList.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public List<Note> searchNotes(long userId, String keyword) {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NOTE_USER_ID + "=? AND (" + COLUMN_NOTE_TITLE + " LIKE ? OR " + COLUMN_NOTE_CONTENT + " LIKE ?)";
        String[] selectionArgs = {String.valueOf(userId), "%" + keyword + "%", "%" + keyword + "%"};
        Cursor cursor = db.query(TABLE_NOTES, null, selection, selectionArgs, null, null, COLUMN_NOTE_UPDATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                noteList.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getContent());
        values.put(COLUMN_NOTE_IMAGE, note.getImage());
        values.put(COLUMN_NOTE_CATEGORY_ID, note.getCategoryId());
        values.put(COLUMN_NOTE_UPDATED_AT, "CURRENT_TIMESTAMP");
        int rows = db.update(TABLE_NOTES, values, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(note.getId())});
        db.close();
        return rows;
    }

    // 将笔记移动到回收站
    public void deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Note note = null;
            // Query for the note inside the current transaction
            Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)}, null, null, null);

            if (cursor.moveToFirst()) {
                note = cursorToNote(cursor);
            }
            cursor.close();

            if (note != null) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_DELETED_NOTE_ID, note.getId());
                values.put(COLUMN_DELETED_NOTE_TITLE, note.getTitle());
                values.put(COLUMN_DELETED_NOTE_CONTENT, note.getContent());
                values.put(COLUMN_DELETED_NOTE_IMAGE, note.getImage());
                values.put(COLUMN_DELETED_NOTE_CATEGORY_ID, note.getCategoryId());
                values.put(COLUMN_DELETED_NOTE_USER_ID, note.getUserId());
                values.put(COLUMN_DELETED_NOTE_CREATED_AT, note.getCreatedAt());
                values.put(COLUMN_DELETED_NOTE_UPDATED_AT, note.getUpdatedAt());
                db.insert(TABLE_DELETED_NOTES, null, values);
                db.delete(TABLE_NOTES, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    // ---- 回收站 (DeletedNote) 操作 ----

    public List<Note> getAllDeletedNotes(long userId) {
        List<Note> deletedNoteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DELETED_NOTES, null, COLUMN_DELETED_NOTE_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_DELETED_NOTE_DELETED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_CONTENT)));
                note.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_IMAGE)));
                note.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_CATEGORY_ID)));
                note.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_USER_ID)));
                note.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_CREATED_AT)));
                note.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_UPDATED_AT)));
                deletedNoteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return deletedNoteList;
    }

    // 从回收站恢复笔记
    public void restoreNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 查找回收站中的笔记
        Cursor cursor = db.query(TABLE_DELETED_NOTES, null, COLUMN_DELETED_NOTE_ID + "=?", new String[]{String.valueOf(noteId)}, null, null, null);
        if (cursor.moveToFirst()) {
            // 将其重新插入笔记表
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTE_ID, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_ID)));
            values.put(COLUMN_NOTE_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_TITLE)));
            values.put(COLUMN_NOTE_CONTENT, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_CONTENT)));
            values.put(COLUMN_NOTE_IMAGE, cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_IMAGE)));
            values.put(COLUMN_NOTE_CATEGORY_ID, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_CATEGORY_ID)));
            values.put(COLUMN_NOTE_USER_ID, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_USER_ID)));
            values.put(COLUMN_NOTE_CREATED_AT, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_CREATED_AT)));
            values.put(COLUMN_NOTE_UPDATED_AT, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_NOTE_UPDATED_AT)));

            db.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            // 从回收站删除
            db.delete(TABLE_DELETED_NOTES, COLUMN_DELETED_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
        }
        cursor.close();
        db.close();
    }

    // 从回收站永久删除笔记
    public void permanentlyDeleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DELETED_NOTES, COLUMN_DELETED_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    // ---- 统计功能 ----
    public int getNotesCount(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES + " WHERE " + COLUMN_NOTE_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getCategoriesCount(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT)));
        note.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_NOTE_IMAGE)));
        note.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CATEGORY_ID)));
        note.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTE_USER_ID)));
        note.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CREATED_AT)));
        note.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_UPDATED_AT)));
        return note;
    }
} 