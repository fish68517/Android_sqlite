package com.example.knowledgelabs.ch07;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class VaultDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "local_vault.db";
    private static final int DATABASE_VERSION = 1;

    public VaultDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE memo (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, created_at INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS memo");
        onCreate(db);
    }

    public long insert(String content) {
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert("memo", null, values);
    }

    public int update(long id, String content) {
        ContentValues values = new ContentValues();
        values.put("content", content);
        return getWritableDatabase().update("memo", values, "id=?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return getWritableDatabase().delete("memo", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Memo> queryAll() {
        List<Memo> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                "memo", new String[]{"id", "content", "created_at"},
                null, null, null, null, "id DESC")) {
            int idColumn = cursor.getColumnIndexOrThrow("id");
            int contentColumn = cursor.getColumnIndexOrThrow("content");
            while (cursor.moveToNext()) {
                result.add(new Memo(cursor.getLong(idColumn), cursor.getString(contentColumn)));
            }
        }
        return result;
    }

    public static final class Memo {
        public final long id;
        public final String content;

        public Memo(long id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return "#" + id + "  " + content;
        }
    }
}
