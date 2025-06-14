package com.example.orderfood;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.orderfood.model.Post;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    // 数据库名称和版本
    private static final String DATABASE_NAME = "HomeDB";
    private static final int DATABASE_VERSION = 12;

    // 用户表
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";







    // 帖子表
    private static final String TABLE_POSTS = "Posts";
    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_POST_TITLE = "post_title";
    private static final String COLUMN_POST_TIMESTAMP = "post_timestamp";
    private static final String COLUMN_POST_CONTENT = "post_content";
    private static final String COLUMN_POST_IMAGE = "post_image"; // 帖子图片路径
    private static final String COLUMN_POST_FAVORITE = "post_favorite"; // 帖子收藏



    // 新表的常量
    private static final String TABLE_IMAGE_URI = "ImageUris";
    private static final String COLUMN_IMAGE_URI_ID = "id"; // 自增主键
    private static final String COLUMN_IMAGE_URI_POST_ID = "post_id"; // 帖子 ID
    private static final String COLUMN_IMAGE_URI_URI = "image_uri"; // 图片 URI




    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_USER_TABLE);

// 创建帖子表
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + " ("
                + COLUMN_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_POST_TITLE + " TEXT NOT NULL, "
                + COLUMN_POST_TIMESTAMP + " TEXT NOT NULL, "
                + COLUMN_POST_CONTENT + " TEXT NOT NULL, "
                + COLUMN_POST_IMAGE + " TEXT, "
                + COLUMN_POST_FAVORITE + " INTEGER DEFAULT 0)"; // 默认未收藏
        db.execSQL(CREATE_POSTS_TABLE);


        // 创建新的图片 URI 表
        String CREATE_IMAGE_URI_TABLE = "CREATE TABLE " + TABLE_IMAGE_URI + " ("
                + COLUMN_IMAGE_URI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_IMAGE_URI_POST_ID + " INTEGER NOT NULL, "
                + COLUMN_IMAGE_URI_URI + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_IMAGE_URI_POST_ID + ") REFERENCES " + TABLE_POSTS + "(" + COLUMN_POST_ID + "))";
        db.execSQL(CREATE_IMAGE_URI_TABLE);

        // 插入初始数据
        insertMockData(db);


        insertInitialPosts(db); // 插入初始帖子数据
    }


    // 添加图片 URI
    public void addImageUri(int postId, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URI_POST_ID, postId);
        values.put(COLUMN_IMAGE_URI_URI, imageUri);
        db.insert(TABLE_IMAGE_URI, null, values);
        db.close();
    }



    // 根据 postId 获取 imageUri
    public List<String> getImageUrisByPostId(int postId) {
        List<String> imageUriList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 查询与 postId 相关的所有 imageUri
        Cursor cursor = db.query(TABLE_IMAGE_URI, new String[]{COLUMN_IMAGE_URI_URI},
                COLUMN_IMAGE_URI_POST_ID + "=?", new String[]{String.valueOf(postId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String imageUri = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI_URI));
                imageUriList.add(imageUri);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return imageUriList;
    }

    // 获取所有图片 URI
    public List<String> getAllImageUris() {
        List<String> imageUriList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_IMAGE_URI, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String imageUri = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI_URI));
                imageUriList.add(imageUri);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imageUriList;
    }


    // 更新图片 URI
    public void updateImageUri(int id, String newImageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URI_URI, newImageUri);
        db.update(TABLE_IMAGE_URI, values, COLUMN_IMAGE_URI_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }


    // 删除图片 URI
    public void deleteImageUri(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGE_URI, COLUMN_IMAGE_URI_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private void insertInitialPosts(SQLiteDatabase db) {
        // 插入第一条帖子
        Post post1 = new Post("明天会更好", "今天 11:10 发布 · 白云山景", "今天青龙山风景真美啊", R.drawable.ic_landscape);
        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_POST_TITLE, post1.getUsername());
        values1.put(COLUMN_POST_TIMESTAMP, post1.getTimestamp());
        values1.put(COLUMN_POST_CONTENT, post1.getContent());
        values1.put(COLUMN_POST_IMAGE, String.valueOf(post1.getImageRes())); // 存储图片资源路径
        values1.put(COLUMN_POST_FAVORITE,0); // 收藏
        db.insert(TABLE_POSTS, null, values1);

        // 插入第二条帖子
        Post post2 = new Post("出发旅行", "昨天 9:00 发布 · 日出山顶", "日出非常震撼！", R.drawable.ic_landscape);
        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_POST_TITLE, post2.getUsername());
        values2.put(COLUMN_POST_TIMESTAMP, post2.getTimestamp());
        values2.put(COLUMN_POST_CONTENT, post2.getContent());
        values2.put(COLUMN_POST_IMAGE, String.valueOf(post2.getImageRes())); // 存储图片资源路径
        values1.put(COLUMN_POST_FAVORITE,0); // 收藏
        db.insert(TABLE_POSTS, null, values2);
    }


    // 添加帖子
    public long addPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TITLE, post.getUsername());
        values.put(COLUMN_POST_TIMESTAMP, post.getTimestamp());
        values.put(COLUMN_POST_CONTENT, post.getContent());
        values.put(COLUMN_POST_IMAGE, String.valueOf(post.getImageRes())); // 存储图片资源路径
        values.put(COLUMN_POST_FAVORITE, post.isFavorite() ? 1 : 0); // 存储收藏状态

        // 插入新行并返回 postId
        long postId = db.insert(TABLE_POSTS, null, values);
        db.close();
        return postId; // 返回新插入的 postId
    }

    // 获取所有帖子
    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POSTS, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TITLE));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TIMESTAMP));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex(COLUMN_POST_CONTENT));
                @SuppressLint("Range") int imageResource = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_IMAGE));
                @SuppressLint("Range") int postId = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_ID));
                @SuppressLint("Range") int favorite = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_FAVORITE));
                postList.add(new Post(title, timestamp, content, imageResource,postId,favorite));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return postList;
    }

    // 更新帖子
    public void updatePost(int postId, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TITLE, title);
        values.put(COLUMN_POST_CONTENT, content);
        db.update(TABLE_POSTS, values, COLUMN_POST_ID + " = ?", new String[]{String.valueOf(postId)});
        db.close();
    }

    public void updatePostFavorite(int postId, int isFavorite) {
        System.out.println("updatePostFavorite: " + postId + " " + isFavorite);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_FAVORITE, isFavorite);
        db.update(TABLE_POSTS, values, COLUMN_POST_ID + " = ?", new String[]{String.valueOf(postId)});
        db.close();
    }

    // 根据 postId 获取帖子
    public Post getPostById(int postId) {
        Post post = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POSTS + " WHERE " + COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(postId)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TITLE));
            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TIMESTAMP));
            @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex(COLUMN_POST_CONTENT));
            @SuppressLint("Range") int imageResource = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_IMAGE));
            @SuppressLint("Range") int favorite = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_FAVORITE));
            post = new Post(title, timestamp, content, imageResource, postId, favorite);
        }
        cursor.close();
        db.close();
        return post;
    }

    // 删除帖子
    public void deletePost(int postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, COLUMN_POST_ID + " = ?", new String[]{String.valueOf(postId)});
        db.close();
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除旧表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_URI);
        onCreate(db);
    }

    // 插入初始榜单数据，包括图片路径




    // 自定义类表示榜单项


    // 插入模拟用户数据
    private void insertMockData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password) VALUES " +
                "('张三', '123456'), " +
                "('李四', '123456'), " +
                "('王五', '123456');");
    }






    // 注册用户
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // 登录验证
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password});
        boolean success = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return success;
    }

    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?",
                new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}
