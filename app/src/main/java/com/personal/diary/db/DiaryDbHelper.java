package com.personal.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.personal.diary.model.Comment;
import com.personal.diary.model.AdminRecord;
import com.personal.diary.model.Diary;
import com.personal.diary.model.Moment;
import com.personal.diary.model.SearchItem;
import com.personal.diary.model.TreeHole;
import com.personal.diary.model.User;
import com.personal.diary.util.PasswordUtil;
import com.personal.diary.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiaryDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "personal_diary.db";
    private static final int DB_VERSION = 4;

    public DiaryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "email TEXT," +
                "nickname TEXT," +
                "signature TEXT," +
                "role TEXT DEFAULT 'user'," +
                "created_at TEXT," +
                "updated_at TEXT)");
        db.execSQL("CREATE TABLE diaries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "mood TEXT," +
                "weather TEXT," +
                "tags TEXT," +
                "status TEXT," +
                "created_at TEXT," +
                "updated_at TEXT)");
        db.execSQL("CREATE TABLE moments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "content TEXT NOT NULL," +
                "image_path TEXT," +
                "image_data BLOB," +
                "created_at TEXT," +
                "updated_at TEXT)");
        db.execSQL("CREATE TABLE tree_holes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "content TEXT NOT NULL," +
                "is_anonymous INTEGER DEFAULT 0," +
                "created_at TEXT," +
                "updated_at TEXT)");
        db.execSQL("CREATE TABLE comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "target_type TEXT NOT NULL," +
                "target_id INTEGER NOT NULL," +
                "content TEXT NOT NULL," +
                "created_at TEXT)");
        db.execSQL("CREATE TABLE likes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "target_type TEXT NOT NULL," +
                "target_id INTEGER NOT NULL," +
                "created_at TEXT," +
                "UNIQUE(user_id, target_type, target_id))");
        db.execSQL("CREATE TABLE favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "target_type TEXT NOT NULL," +
                "target_id INTEGER NOT NULL," +
                "created_at TEXT," +
                "UNIQUE(user_id, target_type, target_id))");
        db.execSQL("CREATE TABLE notices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "content TEXT," +
                "created_at TEXT)");
        db.execSQL("CREATE TABLE feedback (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "content TEXT NOT NULL," +
                "status TEXT," +
                "reply TEXT," +
                "created_at TEXT)");
        db.execSQL("CREATE TABLE posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "created_at TEXT," +
                "updated_at TEXT)");
        seedNotices(db);
        seedAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS feedback (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "content TEXT NOT NULL," +
                    "status TEXT," +
                    "reply TEXT," +
                    "created_at TEXT)");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE moments ADD COLUMN image_data BLOB");
        }
        if (oldVersion < 4) {
            addColumnIfMissing(db, "users", "role", "TEXT DEFAULT 'user'");
            addColumnIfMissing(db, "feedback", "reply", "TEXT");
            db.execSQL("CREATE TABLE IF NOT EXISTS posts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "title TEXT NOT NULL," +
                    "content TEXT NOT NULL," +
                    "created_at TEXT," +
                    "updated_at TEXT)");
            seedAdmin(db);
        }
    }

    private void addColumnIfMissing(SQLiteDatabase db, String table, String column, String type) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        try {
            while (cursor.moveToNext()) {
                if (column.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))) {
                    return;
                }
            }
        } finally {
            cursor.close();
        }
        db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
    }

    private void seedNotices(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("title", "欢迎使用个人日记");
        values.put("content", "本系统采用 Android Studio + Java + SQLiteOpenHelper 开发，数据均保存在本地。");
        values.put("created_at", TimeUtil.now());
        db.insert("notices", null, values);
    }

    private void seedAdmin(SQLiteDatabase db) {
        Cursor cursor = db.query("users", new String[]{"id"}, "username=?", new String[]{"admin"}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("role", "admin");
                db.update("users", values, "username=?", new String[]{"admin"});
                return;
            }
        } finally {
            cursor.close();
        }
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("username", "admin");
        values.put("password_hash", PasswordUtil.sha256("admin123456"));
        values.put("email", "admin@diary.local");
        values.put("nickname", "系统管理员");
        values.put("signature", "负责系统内容与数据管理");
        values.put("role", "admin");
        values.put("created_at", now);
        values.put("updated_at", now);
        db.insert("users", null, values);
    }

    public long registerUser(String username, String password, String email) {
        return registerUser(username, password, email, "user");
    }

    public long registerUser(String username, String password, String email, String role) {
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("username", username);
        values.put("password_hash", PasswordUtil.sha256(password));
        values.put("email", email);
        values.put("nickname", username);
        values.put("signature", "记录今天，也照顾明天的自己");
        values.put("role", role == null || role.trim().isEmpty() ? "user" : role);
        values.put("created_at", now);
        values.put("updated_at", now);
        return getWritableDatabase().insert("users", null, values);
    }

    public User login(String username, String password) {
        Cursor cursor = getReadableDatabase().query("users", null,
                "username=? AND password_hash=?",
                new String[]{username, PasswordUtil.sha256(password)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return readUser(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public User getUser(long userId) {
        Cursor cursor = getReadableDatabase().query("users", null, "id=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        try {
            return cursor.moveToFirst() ? readUser(cursor) : null;
        } finally {
            cursor.close();
        }
    }

    public boolean updateUser(long userId, String nickname, String email, String signature) {
        ContentValues values = new ContentValues();
        values.put("nickname", nickname);
        values.put("email", email);
        values.put("signature", signature);
        values.put("updated_at", TimeUtil.now());
        return getWritableDatabase().update("users", values, "id=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public long adminSaveUser(long id, String username, String password, String email, String nickname, String signature, String role) {
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("username", username);
        values.put("email", email);
        values.put("nickname", nickname);
        values.put("signature", signature);
        values.put("role", role);
        values.put("updated_at", now);
        if (password != null && !password.trim().isEmpty()) {
            values.put("password_hash", PasswordUtil.sha256(password));
        }
        if (id > 0) {
            getWritableDatabase().update("users", values, "id=?", new String[]{String.valueOf(id)});
            return id;
        }
        values.put("created_at", now);
        if (password == null || password.trim().isEmpty()) {
            values.put("password_hash", PasswordUtil.sha256("123456"));
        }
        return getWritableDatabase().insert("users", null, values);
    }

    public boolean adminDeleteUser(long id) {
        return getWritableDatabase().delete("users", "id=? AND username!='admin'", new String[]{String.valueOf(id)}) > 0;
    }

    public List<AdminRecord> adminList(String type, String keyword) {
        String key = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        switch (type) {
            case "users":
                return adminUsers(key);
            case "diaries":
                return adminDiaries(key);
            case "moments":
                return adminMoments(key);
            case "tree_holes":
                return adminTreeHoles(key);
            case "posts":
                return adminPosts(key);
            case "feedback":
                return adminFeedback(key);
            case "notices":
                return adminNotices(key);
            default:
                return new ArrayList<>();
        }
    }

    public long adminSaveContent(String type, long id, long userId, String one, String two, String three, String four) {
        if ("diaries".equals(type)) {
            return saveDiaryAdmin(id, userId, one, two, three, four);
        }
        if ("moments".equals(type)) {
            return saveMomentAdmin(id, userId, one);
        }
        if ("tree_holes".equals(type)) {
            return saveTreeHoleAdmin(id, userId, one);
        }
        if ("posts".equals(type)) {
            return savePostAdmin(id, userId, one, two);
        }
        if ("feedback".equals(type)) {
            return replyFeedback(id, one, two);
        }
        if ("notices".equals(type)) {
            return saveNoticeAdmin(id, one, two);
        }
        return -1;
    }

    public boolean adminDelete(String type, long id) {
        SQLiteDatabase db = getWritableDatabase();
        switch (type) {
            case "diaries":
                db.delete("likes", "target_type='diary' AND target_id=?", new String[]{String.valueOf(id)});
                db.delete("favorites", "target_type='diary' AND target_id=?", new String[]{String.valueOf(id)});
                return db.delete("diaries", "id=?", new String[]{String.valueOf(id)}) > 0;
            case "moments":
                db.delete("likes", "target_type='moment' AND target_id=?", new String[]{String.valueOf(id)});
                db.delete("favorites", "target_type='moment' AND target_id=?", new String[]{String.valueOf(id)});
                return db.delete("moments", "id=?", new String[]{String.valueOf(id)}) > 0;
            case "tree_holes":
                db.delete("comments", "target_type='tree_hole' AND target_id=?", new String[]{String.valueOf(id)});
                return db.delete("tree_holes", "id=?", new String[]{String.valueOf(id)}) > 0;
            case "posts":
                db.delete("comments", "target_type='post' AND target_id=?", new String[]{String.valueOf(id)});
                return db.delete("posts", "id=?", new String[]{String.valueOf(id)}) > 0;
            case "feedback":
                return db.delete("feedback", "id=?", new String[]{String.valueOf(id)}) > 0;
            case "notices":
                return db.delete("notices", "id=?", new String[]{String.valueOf(id)}) > 0;
            default:
                return false;
        }
    }

    public boolean changePassword(long userId, String oldPassword, String newPassword) {
        Cursor cursor = getReadableDatabase().query("users", new String[]{"id"}, "id=? AND password_hash=?",
                new String[]{String.valueOf(userId), PasswordUtil.sha256(oldPassword)}, null, null, null);
        try {
            if (!cursor.moveToFirst()) {
                return false;
            }
        } finally {
            cursor.close();
        }
        ContentValues values = new ContentValues();
        values.put("password_hash", PasswordUtil.sha256(newPassword));
        values.put("updated_at", TimeUtil.now());
        return getWritableDatabase().update("users", values, "id=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public long saveDiary(long id, long userId, String title, String content, String mood, String weather, String tags, String status) {
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("content", content);
        values.put("mood", mood);
        values.put("weather", weather);
        values.put("tags", tags);
        values.put("status", status);
        values.put("updated_at", now);
        if (id > 0) {
            getWritableDatabase().update("diaries", values, "id=? AND user_id=?",
                    new String[]{String.valueOf(id), String.valueOf(userId)});
            return id;
        }
        values.put("created_at", now);
        return getWritableDatabase().insert("diaries", null, values);
    }

    public boolean deleteDiary(long id, long userId) {
        return getWritableDatabase().delete("diaries", "id=? AND user_id=?",
                new String[]{String.valueOf(id), String.valueOf(userId)}) > 0;
    }

    public List<Diary> getDiaries(long userId) {
        Cursor cursor = getReadableDatabase().query("diaries", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, "updated_at DESC");
        List<Diary> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(readDiary(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public long saveMoment(long userId, String content, String imagePath, byte[] imageData) {
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("user_id", userId);
        values.put("content", content);
        values.put("image_path", imagePath);
        values.put("image_data", imageData);
        values.put("created_at", now);
        values.put("updated_at", now);
        return getWritableDatabase().insert("moments", null, values);
    }

    public boolean deleteMoment(long id, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("likes", "target_type='moment' AND target_id=?", new String[]{String.valueOf(id)});
        db.delete("favorites", "target_type='moment' AND target_id=?", new String[]{String.valueOf(id)});
        return db.delete("moments", "id=? AND user_id=?", new String[]{String.valueOf(id), String.valueOf(userId)}) > 0;
    }

    public List<Moment> getMoments(long currentUserId) {
        String sql = "SELECT m.*, u.nickname AS author FROM moments m LEFT JOIN users u ON u.id=m.user_id ORDER BY m.created_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        List<Moment> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Moment moment = new Moment();
                moment.id = getLong(cursor, "id");
                moment.userId = getLong(cursor, "user_id");
                moment.author = getString(cursor, "author");
                moment.content = getString(cursor, "content");
                moment.imagePath = getString(cursor, "image_path");
                moment.imageData = getBlob(cursor, "image_data");
                moment.createdAt = getString(cursor, "created_at");
                moment.likeCount = countTarget("likes", "moment", moment.id);
                moment.favoriteCount = countTarget("favorites", "moment", moment.id);
                moment.liked = existsTarget("likes", currentUserId, "moment", moment.id);
                moment.favorited = existsTarget("favorites", currentUserId, "moment", moment.id);
                result.add(moment);
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public void toggleLike(long userId, String targetType, long targetId) {
        toggleTarget("likes", userId, targetType, targetId);
    }

    public void toggleFavorite(long userId, String targetType, long targetId) {
        toggleTarget("favorites", userId, targetType, targetId);
    }

    public long saveTreeHole(long userId, String content, boolean anonymous) {
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("user_id", userId);
        values.put("content", content);
        values.put("is_anonymous", anonymous ? 1 : 0);
        values.put("created_at", now);
        values.put("updated_at", now);
        return getWritableDatabase().insert("tree_holes", null, values);
    }

    public boolean deleteTreeHole(long id, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("comments", "target_type='tree_hole' AND target_id=?", new String[]{String.valueOf(id)});
        return db.delete("tree_holes", "id=? AND user_id=?", new String[]{String.valueOf(id), String.valueOf(userId)}) > 0;
    }

    public List<TreeHole> getTreeHoles() {
        String sql = "SELECT t.*, u.nickname AS author FROM tree_holes t LEFT JOIN users u ON u.id=t.user_id ORDER BY t.created_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        List<TreeHole> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                TreeHole item = new TreeHole();
                item.id = getLong(cursor, "id");
                item.userId = getLong(cursor, "user_id");
                item.author = getString(cursor, "author");
                item.content = getString(cursor, "content");
                item.anonymous = getInt(cursor, "is_anonymous") == 1;
                item.createdAt = getString(cursor, "created_at");
                item.commentCount = countTarget("comments", "tree_hole", item.id);
                result.add(item);
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public long addComment(long userId, String targetType, long targetId, String content) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("target_type", targetType);
        values.put("target_id", targetId);
        values.put("content", content);
        values.put("created_at", TimeUtil.now());
        return getWritableDatabase().insert("comments", null, values);
    }

    public List<Comment> getComments(String targetType, long targetId) {
        String sql = "SELECT c.*, u.nickname AS author FROM comments c LEFT JOIN users u ON u.id=c.user_id " +
                "WHERE c.target_type=? AND c.target_id=? ORDER BY c.created_at ASC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{targetType, String.valueOf(targetId)});
        List<Comment> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Comment comment = new Comment();
                comment.id = getLong(cursor, "id");
                comment.author = getString(cursor, "author");
                comment.content = getString(cursor, "content");
                comment.createdAt = getString(cursor, "created_at");
                result.add(comment);
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public List<SearchItem> search(long userId, String keyword) {
        String key = "%" + keyword + "%";
        List<SearchItem> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor diaryCursor = db.rawQuery("SELECT id,title,content,updated_at FROM diaries WHERE user_id=? AND (title LIKE ? OR content LIKE ? OR tags LIKE ?) ORDER BY updated_at DESC",
                new String[]{String.valueOf(userId), key, key, key});
        try {
            while (diaryCursor.moveToNext()) {
                SearchItem item = new SearchItem();
                item.id = getLong(diaryCursor, "id");
                item.type = "日记";
                item.title = getString(diaryCursor, "title");
                item.body = getString(diaryCursor, "content");
                item.meta = "日记 · " + getString(diaryCursor, "updated_at");
                result.add(item);
            }
        } finally {
            diaryCursor.close();
        }
        Cursor momentCursor = db.rawQuery("SELECT id,content,created_at FROM moments WHERE content LIKE ? ORDER BY created_at DESC", new String[]{key});
        try {
            while (momentCursor.moveToNext()) {
                SearchItem item = new SearchItem();
                item.id = getLong(momentCursor, "id");
                item.type = "幸福瞬间";
                item.title = "幸福瞬间";
                item.body = getString(momentCursor, "content");
                item.meta = "幸福瞬间 · " + getString(momentCursor, "created_at");
                result.add(item);
            }
        } finally {
            momentCursor.close();
        }
        Cursor treeCursor = db.rawQuery("SELECT id,content,created_at FROM tree_holes WHERE content LIKE ? ORDER BY created_at DESC", new String[]{key});
        try {
            while (treeCursor.moveToNext()) {
                SearchItem item = new SearchItem();
                item.id = getLong(treeCursor, "id");
                item.type = "树洞";
                item.title = "树洞";
                item.body = getString(treeCursor, "content");
                item.meta = "树洞 · " + getString(treeCursor, "created_at");
                result.add(item);
            }
        } finally {
            treeCursor.close();
        }
        return result;
    }

    public int count(String table, long userId) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table + " WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public int countFavorites(long userId) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM favorites WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public String[] getRecentSevenDayLabels() {
        String[] labels = new String[7];
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            labels[i] = String.format(Locale.CHINA, "%02d/%02d",
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return labels;
    }

    public int[] getRecentSevenDayRecordCounts(long userId) {
        int[] values = new int[7];
        String[] days = getRecentSevenDayKeys();
        SQLiteDatabase db = getReadableDatabase();
        for (int i = 0; i < days.length; i++) {
            values[i] = countByDate(db, "diaries", userId, days[i])
                    + countByDate(db, "moments", userId, days[i])
                    + countByDate(db, "tree_holes", userId, days[i]);
        }
        return values;
    }

    public int[] getRecentSevenDayMoodScores(long userId) {
        int[] values = new int[7];
        String[] days = getRecentSevenDayKeys();
        SQLiteDatabase db = getReadableDatabase();
        for (int i = 0; i < days.length; i++) {
            Cursor cursor = db.rawQuery("SELECT mood FROM diaries WHERE user_id=? AND substr(created_at, 1, 10)=?",
                    new String[]{String.valueOf(userId), days[i]});
            int total = 0;
            int count = 0;
            try {
                while (cursor.moveToNext()) {
                    total += moodScore(getString(cursor, "mood"));
                    count++;
                }
            } finally {
                cursor.close();
            }
            values[i] = count == 0 ? 3 : Math.max(1, Math.min(5, Math.round(total / (float) count)));
        }
        return values;
    }

    private String[] getRecentSevenDayKeys() {
        String[] days = new String[7];
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            days[i] = String.format(Locale.CHINA, "%04d-%02d-%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return days;
    }

    private int countByDate(SQLiteDatabase db, String table, long userId, String day) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table + " WHERE user_id=? AND substr(created_at, 1, 10)=?",
                new String[]{String.valueOf(userId), day});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    private int moodScore(String mood) {
        if (mood == null) {
            return 3;
        }
        if (mood.contains("开心") || mood.contains("高兴") || mood.contains("快乐") || mood.contains("幸福")) {
            return 5;
        }
        if (mood.contains("平静") || mood.contains("一般") || mood.contains("普通")) {
            return 3;
        }
        if (mood.contains("焦虑") || mood.contains("烦") || mood.contains("累")) {
            return 2;
        }
        if (mood.contains("难过") || mood.contains("伤心") || mood.contains("失落")) {
            return 1;
        }
        return 4;
    }

    private List<AdminRecord> adminUsers(String key) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE username LIKE ? OR nickname LIKE ? OR email LIKE ? ORDER BY id DESC",
                new String[]{key, key, key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(new AdminRecord(
                        getLong(cursor, "id"),
                        getLong(cursor, "id"),
                        getString(cursor, "username"),
                        getString(cursor, "nickname"),
                        getString(cursor, "email"),
                        getString(cursor, "role") + "|" + getString(cursor, "signature") + "|" + getString(cursor, "created_at")));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private List<AdminRecord> adminDiaries(String key) {
        String sql = "SELECT d.*, u.username AS author FROM diaries d LEFT JOIN users u ON u.id=d.user_id " +
                "WHERE d.title LIKE ? OR d.content LIKE ? OR d.tags LIKE ? ORDER BY d.updated_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{key, key, key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(new AdminRecord(
                        getLong(cursor, "id"),
                        getLong(cursor, "user_id"),
                        getString(cursor, "title"),
                        getString(cursor, "content"),
                        getString(cursor, "author"),
                        getString(cursor, "mood") + "|" + getString(cursor, "weather") + "|" + getString(cursor, "tags") + "|" + getString(cursor, "status")));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private List<AdminRecord> adminMoments(String key) {
        String sql = "SELECT m.*, u.username AS author FROM moments m LEFT JOIN users u ON u.id=m.user_id " +
                "WHERE m.content LIKE ? ORDER BY m.created_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                long id = getLong(cursor, "id");
                result.add(new AdminRecord(
                        id,
                        getLong(cursor, "user_id"),
                        "幸福瞬间",
                        getString(cursor, "content"),
                        getString(cursor, "author"),
                        countTarget("likes", "moment", id) + "|" + countTarget("favorites", "moment", id)));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private List<AdminRecord> adminTreeHoles(String key) {
        String sql = "SELECT t.*, u.username AS author FROM tree_holes t LEFT JOIN users u ON u.id=t.user_id " +
                "WHERE t.content LIKE ? ORDER BY t.created_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                long id = getLong(cursor, "id");
                result.add(new AdminRecord(
                        id,
                        getLong(cursor, "user_id"),
                        getInt(cursor, "is_anonymous") == 1 ? "匿名树洞" : "树洞",
                        getString(cursor, "content"),
                        getString(cursor, "author"),
                        countTarget("comments", "tree_hole", id) + "|" + getInt(cursor, "is_anonymous")));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private List<AdminRecord> adminPosts(String key) {
        String sql = "SELECT p.*, u.username AS author FROM posts p LEFT JOIN users u ON u.id=p.user_id " +
                "WHERE p.title LIKE ? OR p.content LIKE ? ORDER BY p.updated_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{key, key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                long id = getLong(cursor, "id");
                result.add(new AdminRecord(
                        id,
                        getLong(cursor, "user_id"),
                        getString(cursor, "title"),
                        getString(cursor, "content"),
                        getString(cursor, "author"),
                        String.valueOf(countTarget("comments", "post", id))));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private List<AdminRecord> adminFeedback(String key) {
        String sql = "SELECT f.*, u.username AS author FROM feedback f LEFT JOIN users u ON u.id=f.user_id " +
                "WHERE f.content LIKE ? OR f.status LIKE ? OR f.reply LIKE ? ORDER BY f.created_at DESC";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{key, key, key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(new AdminRecord(
                        getLong(cursor, "id"),
                        getLong(cursor, "user_id"),
                        "用户反馈",
                        getString(cursor, "content"),
                        getString(cursor, "author"),
                        getString(cursor, "status") + "|" + getString(cursor, "reply")));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private List<AdminRecord> adminNotices(String key) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM notices WHERE title LIKE ? OR content LIKE ? ORDER BY created_at DESC",
                new String[]{key, key});
        List<AdminRecord> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(new AdminRecord(
                        getLong(cursor, "id"),
                        0,
                        getString(cursor, "title"),
                        getString(cursor, "content"),
                        getString(cursor, "created_at"),
                        ""));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private long saveDiaryAdmin(long id, long userId, String title, String content, String mood, String weather) {
        return saveDiary(id, userId, title, content, mood, weather, "", "已发布");
    }

    private long saveMomentAdmin(long id, long userId, String content) {
        if (id <= 0) {
            return saveMoment(userId, content, "", null);
        }
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("updated_at", TimeUtil.now());
        getWritableDatabase().update("moments", values, "id=?", new String[]{String.valueOf(id)});
        return id;
    }

    private long saveTreeHoleAdmin(long id, long userId, String content) {
        if (id <= 0) {
            return saveTreeHole(userId, content, true);
        }
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("updated_at", TimeUtil.now());
        getWritableDatabase().update("tree_holes", values, "id=?", new String[]{String.valueOf(id)});
        return id;
    }

    private long savePostAdmin(long id, long userId, String title, String content) {
        ContentValues values = new ContentValues();
        String now = TimeUtil.now();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("content", content);
        values.put("updated_at", now);
        if (id > 0) {
            getWritableDatabase().update("posts", values, "id=?", new String[]{String.valueOf(id)});
            return id;
        }
        values.put("created_at", now);
        return getWritableDatabase().insert("posts", null, values);
    }

    private long replyFeedback(long id, String reply, String status) {
        ContentValues values = new ContentValues();
        values.put("reply", reply);
        values.put("status", status == null || status.trim().isEmpty() ? "已回复" : status);
        getWritableDatabase().update("feedback", values, "id=?", new String[]{String.valueOf(id)});
        return id;
    }

    private long saveNoticeAdmin(long id, String title, String content) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        if (id > 0) {
            getWritableDatabase().update("notices", values, "id=?", new String[]{String.valueOf(id)});
            return id;
        }
        values.put("created_at", TimeUtil.now());
        return getWritableDatabase().insert("notices", null, values);
    }

    public int countAll(String table) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public int countTargetAll(String table, String targetType) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table + " WHERE target_type=?",
                new String[]{targetType});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public int[] getRecentSevenDayAllCounts() {
        int[] values = new int[7];
        String[] days = getRecentSevenDayKeys();
        SQLiteDatabase db = getReadableDatabase();
        for (int i = 0; i < days.length; i++) {
            values[i] = countAllByDate(db, "diaries", days[i])
                    + countAllByDate(db, "moments", days[i])
                    + countAllByDate(db, "tree_holes", days[i])
                    + countAllByDate(db, "posts", days[i]);
        }
        return values;
    }

    public int[] getRecentSevenDayInteractionCounts() {
        int[] values = new int[7];
        String[] days = getRecentSevenDayKeys();
        SQLiteDatabase db = getReadableDatabase();
        for (int i = 0; i < days.length; i++) {
            values[i] = countAllByDate(db, "likes", days[i])
                    + countAllByDate(db, "favorites", days[i])
                    + countAllByDate(db, "comments", days[i]);
        }
        return values;
    }

    private int countAllByDate(SQLiteDatabase db, String table, String day) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table + " WHERE substr(created_at, 1, 10)=?",
                new String[]{day});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public long addFeedback(long userId, String content) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("content", content);
        values.put("status", "待处理");
        values.put("created_at", TimeUtil.now());
        return getWritableDatabase().insert("feedback", null, values);
    }

    public String getNoticesText() {
        Cursor cursor = getReadableDatabase().query("notices", null, null, null, null, null, "created_at DESC");
        StringBuilder builder = new StringBuilder();
        try {
            while (cursor.moveToNext()) {
                builder.append(getString(cursor, "title")).append("\n")
                        .append(getString(cursor, "content")).append("\n")
                        .append(getString(cursor, "created_at")).append("\n\n");
            }
        } finally {
            cursor.close();
        }
        return builder.length() == 0 ? "暂无公告" : builder.toString();
    }

    public String getFeedbackText(long userId) {
        Cursor cursor = getReadableDatabase().query("feedback", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, "created_at DESC");
        StringBuilder builder = new StringBuilder();
        try {
            while (cursor.moveToNext()) {
                builder.append(getString(cursor, "content")).append("\n状态：")
                        .append(getString(cursor, "status")).append("  ")
                        .append(getString(cursor, "created_at")).append("\n\n");
            }
        } finally {
            cursor.close();
        }
        return builder.length() == 0 ? "暂无反馈记录" : builder.toString();
    }

    private User readUser(Cursor cursor) {
        User user = new User();
        user.id = getLong(cursor, "id");
        user.username = getString(cursor, "username");
        user.email = getString(cursor, "email");
        user.nickname = getString(cursor, "nickname");
        user.signature = getString(cursor, "signature");
        user.role = getString(cursor, "role");
        user.createdAt = getString(cursor, "created_at");
        return user;
    }

    private Diary readDiary(Cursor cursor) {
        Diary diary = new Diary();
        diary.id = getLong(cursor, "id");
        diary.userId = getLong(cursor, "user_id");
        diary.title = getString(cursor, "title");
        diary.content = getString(cursor, "content");
        diary.mood = getString(cursor, "mood");
        diary.weather = getString(cursor, "weather");
        diary.tags = getString(cursor, "tags");
        diary.status = getString(cursor, "status");
        diary.createdAt = getString(cursor, "created_at");
        diary.updatedAt = getString(cursor, "updated_at");
        return diary;
    }

    private void toggleTarget(String table, long userId, String targetType, long targetId) {
        SQLiteDatabase db = getWritableDatabase();
        if (existsTarget(table, userId, targetType, targetId)) {
            db.delete(table, "user_id=? AND target_type=? AND target_id=?",
                    new String[]{String.valueOf(userId), targetType, String.valueOf(targetId)});
        } else {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("target_type", targetType);
            values.put("target_id", targetId);
            values.put("created_at", TimeUtil.now());
            db.insert(table, null, values);
        }
    }

    private boolean existsTarget(String table, long userId, String targetType, long targetId) {
        Cursor cursor = getReadableDatabase().query(table, new String[]{"id"},
                "user_id=? AND target_type=? AND target_id=?",
                new String[]{String.valueOf(userId), targetType, String.valueOf(targetId)}, null, null, null);
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    private int countTarget(String table, String targetType, long targetId) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table + " WHERE target_type=? AND target_id=?",
                new String[]{targetType, String.valueOf(targetId)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    private String getString(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index >= 0 && !cursor.isNull(index) ? cursor.getString(index) : "";
    }

    private long getLong(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index >= 0 ? cursor.getLong(index) : 0L;
    }

    private int getInt(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index >= 0 ? cursor.getInt(index) : 0;
    }

    private byte[] getBlob(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index >= 0 && !cursor.isNull(index) ? cursor.getBlob(index) : null;
    }
}
