package com.example.studentmanager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmanager.db.StudentDBHelper;

/**
 * 学生信息ContentProvider
 * 提供统一的数据访问接口
 */
public class StudentProvider extends ContentProvider {
    // 数据库帮助类
    private StudentDBHelper dbHelper;
    
    // URI匹配器
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    // 定义URI匹配码
    private static final int STUDENTS = 1;
    private static final int STUDENT_ID = 2;
    private static final int CLASSES = 3;
    private static final int CLASS_ID = 4;
    private static final int STATUS_HISTORY = 5;
    
    // 定义Authority
    public static final String AUTHORITY = "com.example.studentmanager.provider";
    
    // 定义Content URI
    public static final Uri CONTENT_URI_STUDENTS = Uri.parse("content://" + AUTHORITY + "/students");
    public static final Uri CONTENT_URI_CLASSES = Uri.parse("content://" + AUTHORITY + "/classes");
    public static final Uri CONTENT_URI_STATUS_HISTORY = Uri.parse("content://" + AUTHORITY + "/status_history");
    
    static {
        // 注册URI匹配规则
        uriMatcher.addURI(AUTHORITY, "students", STUDENTS);
        uriMatcher.addURI(AUTHORITY, "students/#", STUDENT_ID);
        uriMatcher.addURI(AUTHORITY, "classes", CLASSES);
        uriMatcher.addURI(AUTHORITY, "classes/#", CLASS_ID);
        uriMatcher.addURI(AUTHORITY, "status_history", STATUS_HISTORY);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new StudentDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                       @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                cursor = db.query("students", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STUDENT_ID:
                String studentId = uri.getLastPathSegment();
                cursor = db.query("students", projection, "student_id = ?", 
                        new String[]{studentId}, null, null, sortOrder);
                break;
            case CLASSES:
                cursor = db.query("classes", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STATUS_HISTORY:
                cursor = db.query("student_status_history", projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                id = db.insert("students", null, values);
                return ContentUris.withAppendedId(CONTENT_URI_STUDENTS, id);
            case CLASSES:
                id = db.insert("classes", null, values);
                return ContentUris.withAppendedId(CONTENT_URI_CLASSES, id);
            case STATUS_HISTORY:
                id = db.insert("student_status_history", null, values);
                return ContentUris.withAppendedId(CONTENT_URI_STATUS_HISTORY, id);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.delete("students", selection, selectionArgs);
                break;
            case STUDENT_ID:
                String studentId = uri.getLastPathSegment();
                count = db.delete("students", "student_id = ?", new String[]{studentId});
                break;
            case CLASSES:
                count = db.delete("classes", selection, selectionArgs);
                break;
            case STATUS_HISTORY:
                count = db.delete("student_status_history", selection, selectionArgs);
                break;
        }
        
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                     @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.update("students", values, selection, selectionArgs);
                break;
            case STUDENT_ID:
                String studentId = uri.getLastPathSegment();
                count = db.update("students", values, "student_id = ?", new String[]{studentId});
                break;
            case CLASSES:
                count = db.update("classes", values, selection, selectionArgs);
                break;
            case STATUS_HISTORY:
                count = db.update("student_status_history", values, selection, selectionArgs);
                break;
        }
        
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    public static boolean isUsernameExists(Context context, String username) {
        Cursor cursor = context.getContentResolver().query(
            CONTENT_URI_STUDENTS,
            new String[]{"username"},
            "username = ?",
            new String[]{username},
            null
        );
        
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
} 