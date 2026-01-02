package com.archive.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenHelperDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "textbook_system.db";
    private static final int DATABASE_VERSION = 1;

    // 用户表
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";

    // 管理员表
    public static final String TABLE_ADMIN = "admin";
    public static final String COLUMN_ADMIN_ID = "_id";
    public static final String COLUMN_ADMIN_USERNAME = "username";
    public static final String COLUMN_ADMIN_PASSWORD = "password";
    public static final String COLUMN_ADMIN_REMEMBER_PASSWORD = "remember_password";
    public static final String COLUMN_ADMIN_CREATE_TIME = "create_time";

    // 图书分类表
    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_DESCRIPTION = "description";

    // 图书表
    public static final String TABLE_BOOK = "book";
    public static final String COLUMN_BOOK_ID = "_id";
    public static final String COLUMN_BOOK_TITLE = "title";
    public static final String COLUMN_BOOK_AUTHOR = "author";
    public static final String COLUMN_BOOK_ISBN = "isbn";
    public static final String COLUMN_BOOK_COVER_IMAGE = "cover_image";
    public static final String COLUMN_BOOK_DESCRIPTION = "description";
    public static final String COLUMN_BOOK_PUBLISH_DATE = "publish_date";
    public static final String COLUMN_BOOK_CATEGORY_ID = "category_id";
    public static final String COLUMN_BOOK_CREATE_TIME = "create_time";

    // 创建用户表的SQL语句
    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_USER_ROLE + " TEXT NOT NULL);";

    // 创建管理员表的SQL语句
    private static final String TABLE_CREATE_ADMIN =
            "CREATE TABLE " + TABLE_ADMIN + " (" +
                    COLUMN_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ADMIN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_ADMIN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_ADMIN_REMEMBER_PASSWORD + " INTEGER DEFAULT 0, " +
                    COLUMN_ADMIN_CREATE_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    // 创建图书分类表的SQL语句
    private static final String TABLE_CREATE_CATEGORY =
            "CREATE TABLE " + TABLE_CATEGORY + " (" +
                    COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_DESCRIPTION + " TEXT);";

    // 创建图书表的SQL语句
    private static final String TABLE_CREATE_BOOK =
            "CREATE TABLE " + TABLE_BOOK + " (" +
                    COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BOOK_TITLE + " TEXT NOT NULL, " +
                    COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, " +
                    COLUMN_BOOK_ISBN + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_BOOK_COVER_IMAGE + " TEXT, " +
                    COLUMN_BOOK_DESCRIPTION + " TEXT, " +
                    COLUMN_BOOK_PUBLISH_DATE + " TEXT, " +
                    COLUMN_BOOK_CATEGORY_ID + " INTEGER, " +
                    COLUMN_BOOK_CREATE_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (" + COLUMN_BOOK_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + ") ON DELETE SET NULL);";

    private static final String TAG = "OpenHelperDataBase";


    public OpenHelperDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "数据库帮助类已创建。");
    }


    // 用户注册
    public boolean registerUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_ROLE, role);
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
    public User loginUser(String username, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null,
                    COLUMN_USER_NAME + "=? AND " + COLUMN_USER_PASSWORD + "=? AND " + COLUMN_USER_ROLE + "=?",
                    new String[]{username, password, role}, null, null, null);
            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)));
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

        Log.i(TAG, "正在创建管理员表 (" + TABLE_ADMIN + ")...");
        db.execSQL(TABLE_CREATE_ADMIN);
        Log.i(TAG, "管理员表 (" + TABLE_ADMIN + ") 创建成功。");
        insertInitialAdminData(db);

        Log.i(TAG, "正在创建图书分类表 (" + TABLE_CATEGORY + ")...");
        db.execSQL(TABLE_CREATE_CATEGORY);
        Log.i(TAG, "图书分类表 (" + TABLE_CATEGORY + ") 创建成功。");
        insertInitialCategoryData(db);

        Log.i(TAG, "正在创建图书表 (" + TABLE_BOOK + ")...");
        db.execSQL(TABLE_CREATE_BOOK);
        Log.i(TAG, "图书表 (" + TABLE_BOOK + ") 创建成功。");
        insertInitialBookData(db);

        Log.i(TAG, "数据库表创建和初始数据插入完成。");
    }

    private void insertInitialAdminData(SQLiteDatabase db) {
        Log.i(TAG, "正在插入管理员初始数据...");
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADMIN_USERNAME, "admin");
        values.put(COLUMN_ADMIN_PASSWORD, "123456");
        values.put(COLUMN_ADMIN_REMEMBER_PASSWORD, 0);
        long id = db.insert(TABLE_ADMIN, null, values);
        if (id != -1) {
            Log.i(TAG, "管理员初始数据插入成功, ID: " + id);
        } else {
            Log.e(TAG, "管理员初始数据插入失败。");
        }
    }

    private void insertInitialCategoryData(SQLiteDatabase db) {
        Log.i(TAG, "正在插入图书分类初始数据...");
        String[][] categories = {
                {"计算机", "计算机相关书籍"},
                {"文学", "文学类书籍"},
                {"历史", "历史类书籍"},
                {"科学", "科学类书籍"}
        };
        for (String[] category : categories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, category[0]);
            values.put(COLUMN_CATEGORY_DESCRIPTION, category[1]);
            long id = db.insert(TABLE_CATEGORY, null, values);
            if (id != -1) {
                Log.i(TAG, "图书分类数据插入成功: " + category[0] + ", ID: " + id);
            } else {
                Log.e(TAG, "图书分类数据插入失败: " + category[0]);
            }
        }
    }

    private void insertInitialBookData(SQLiteDatabase db) {
        Log.i(TAG, "正在插入图书初始数据...");
        Object[][] books = {
                {"Java编程思想", "Bruce Eckel", "9787111213826", "book_java", "Java编程经典著作", "2007-06-01", 1},
                {"红楼梦", "曹雪芹", "9787020002207", "book_ds", "中国古典四大名著之一", "1996-12-01", 2},
                {"明朝那些事儿", "当年明月", "9787801655037", "book_android", "讲述明朝历史的通俗读物", "2009-04-01", 3},
                {"时间简史", "史蒂芬·霍金", "9787535732309", "book_network", "探索宇宙奥秘的科普著作", "2010-04-01", 4},
                {"算法导论", "Thomas H.Cormen", "9787111187776", "book_os", "计算机算法经典教材", "2009-07-01", 1}
        };

        for (Object[] bookData : books) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BOOK_TITLE, (String) bookData[0]);
            values.put(COLUMN_BOOK_AUTHOR, (String) bookData[1]);
            values.put(COLUMN_BOOK_ISBN, (String) bookData[2]);
            values.put(COLUMN_BOOK_COVER_IMAGE, (String) bookData[3]);
            values.put(COLUMN_BOOK_DESCRIPTION, (String) bookData[4]);
            values.put(COLUMN_BOOK_PUBLISH_DATE, (String) bookData[5]);
            values.put(COLUMN_BOOK_CATEGORY_ID, (Integer) bookData[6]);
            long id = db.insert(TABLE_BOOK, null, values);
            if (id != -1) {
                Log.i(TAG, "图书数据插入成功: " + bookData[0] + ", ID: " + id);
            } else {
                Log.e(TAG, "图书数据插入失败: " + bookData[0]);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "正在升级数据库，版本从 " + oldVersion + " 到 " + newVersion + "。旧数据将被删除。");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
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
                    record.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)));
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

    /**
     * 添加新的图书分类
     * @param category Category对象
     * @return 新分类的ID，如果失败则返回-1
     */
    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getName());
        values.put(COLUMN_CATEGORY_DESCRIPTION, category.getDescription());
        long id = -1;
        try {
            id = db.insert(TABLE_CATEGORY, null, values);
            if (id != -1) {
                Log.i(TAG, "图书分类添加成功: " + category.getName() + ", ID: " + id);
            } else {
                Log.e(TAG, "图书分类添加失败: " + category.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "addCategory: 添加图书分类时发生错误", e);
        } finally {
            db.close();
        }
        return id;
    }

    /**
     * 根据ID获取分类信息
     * @param categoryId 分类ID
     * @return Category 对象，未找到则返回 null
     */
    public Category getCategoryById(long categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Category category = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CATEGORY, null, COLUMN_CATEGORY_ID + "=?",
                    new String[]{String.valueOf(categoryId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                category = new Category();
                category.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                category.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_DESCRIPTION)));
                Log.i(TAG, "查询到分类: " + category.getName());
            } else {
                Log.w(TAG, "未查询到分类, ID: " + categoryId);
            }
        } catch (Exception e) {
            Log.e(TAG, "getCategoryById: 查询分类时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return category;
    }

    /**
     * 获取所有图书分类
     * @return Category列表
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CATEGORY, null, null, null, null, null, COLUMN_CATEGORY_NAME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                    category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                    category.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_DESCRIPTION)));
                    categories.add(category);
                } while (cursor.moveToNext());
            }
            Log.i(TAG, "获取到 " + categories.size() + " 个分类");
        } catch (Exception e) {
            Log.e(TAG, "getAllCategories: 获取所有分类时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return categories;
    }

    /**
     * 更新分类信息
     * @param category Category对象，必须包含ID
     * @return 受影响的行数
     */
    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getName());
        values.put(COLUMN_CATEGORY_DESCRIPTION, category.getDescription());
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_CATEGORY, values, COLUMN_CATEGORY_ID + "=?",
                    new String[]{String.valueOf(category.getId())});
            if (rowsAffected > 0) {
                Log.i(TAG, "分类信息更新成功: " + category.getName());
            } else {
                Log.w(TAG, "分类信息更新失败或分类不存在: " + category.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "updateCategory: 更新分类信息时发生错误", e);
        } finally {
            db.close();
        }
        return rowsAffected;
    }

    /**
     * 删除分类
     * 注意：如果分类下有图书，根据外键约束 (ON DELETE SET NULL)，相关图书的 category_id 会被设为 NULL。
     * @param categoryId 分类ID
     * @return 受影响的行数 (通常为1如果删除成功)
     */
    public int deleteCategory(long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(TABLE_CATEGORY, COLUMN_CATEGORY_ID + "=?",
                    new String[]{String.valueOf(categoryId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "分类删除成功, ID: " + categoryId);
            } else {
                Log.w(TAG, "分类删除失败或分类不存在, ID: " + categoryId);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteCategory: 删除分类时发生错误", e);
        } finally {
            db.close();
        }
        return rowsAffected;
    }

    // ---- 图书表 (Book) 操作 ----

    /**
     * 添加新图书
     * @param book Book对象
     * @return 新图书的ID，如果失败则返回-1
     */
    public long addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_TITLE, book.getTitle());
        values.put(COLUMN_BOOK_AUTHOR, book.getAuthor());
        values.put(COLUMN_BOOK_ISBN, book.getIsbn());
        values.put(COLUMN_BOOK_COVER_IMAGE, book.getCoverImage());
        values.put(COLUMN_BOOK_DESCRIPTION, book.getDescription());
        values.put(COLUMN_BOOK_PUBLISH_DATE, book.getPublishDate());
        if (book.getCategoryId() > 0) {
            values.put(COLUMN_BOOK_CATEGORY_ID, book.getCategoryId());
        } else {
            values.putNull(COLUMN_BOOK_CATEGORY_ID);
        }

        long id = -1;
        try {
            id = db.insert(TABLE_BOOK, null, values);
            if (id != -1) {
                Log.i(TAG, "图书添加成功: " + book.getTitle() + ", ID: " + id);
            } else {
                Log.e(TAG, "图书添加失败: " + book.getTitle());
            }
        } catch (Exception e) {
            Log.e(TAG, "addBook: 添加图书时发生错误", e);
        } finally {
            db.close();
        }
        return id;
    }

    /**
     * 根据ID获取图书信息 (包含分类名称)
     * @param bookId 图书ID
     * @return Book 对象，未找到则返回 null
     */
    public Book getBookById(long bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Book book = null;
        Cursor cursor = null;
        String query = "SELECT b.*, c." + COLUMN_CATEGORY_NAME + " FROM " + TABLE_BOOK + " b LEFT JOIN " +
                       TABLE_CATEGORY + " c ON b." + COLUMN_BOOK_CATEGORY_ID + " = c." + COLUMN_CATEGORY_ID +
                       " WHERE b." + COLUMN_BOOK_ID + " = ?";
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(bookId)});
            if (cursor != null && cursor.moveToFirst()) {
                book = new Book();
                book.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR)));
                book.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ISBN)));
                book.setCoverImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_COVER_IMAGE)));
                book.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_DESCRIPTION)));
                book.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PUBLISH_DATE)));
                book.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID)));
                book.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CREATE_TIME)));
                int categoryNameColumnIndex = cursor.getColumnIndex(COLUMN_CATEGORY_NAME);
                if (categoryNameColumnIndex != -1 && !cursor.isNull(categoryNameColumnIndex)) {
                    book.setCategoryName(cursor.getString(categoryNameColumnIndex));
                } else {
                     book.setCategoryName( (book.getCategoryId()==0 || cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID))) ? "未分类" : "分类未知");
                }
                Log.i(TAG, "查询到图书: " + book.getTitle());
            } else {
                Log.w(TAG, "未查询到图书, ID: " + bookId);
            }
        } catch (Exception e) {
            Log.e(TAG, "getBookById: 查询图书时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return book;
    }

    /**
     * 获取所有图书信息 (包含分类名称)
     * @return Book列表
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT b.*, c." + COLUMN_CATEGORY_NAME + " AS category_name FROM " + TABLE_BOOK + " b LEFT JOIN " +
                       TABLE_CATEGORY + " c ON b." + COLUMN_BOOK_CATEGORY_ID + " = c." + COLUMN_CATEGORY_ID +
                       " ORDER BY b." + COLUMN_BOOK_TITLE + " ASC";
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Book book = new Book();
                    book.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                    book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_TITLE)));
                    book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR)));
                    book.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ISBN)));
                    book.setCoverImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_COVER_IMAGE)));
                    book.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_DESCRIPTION)));
                    book.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PUBLISH_DATE)));
                    book.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID)));
                    book.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CREATE_TIME)));
                    int categoryNameColumnIndex = cursor.getColumnIndex("category_name");
                     if (categoryNameColumnIndex != -1 && !cursor.isNull(categoryNameColumnIndex)) {
                        book.setCategoryName(cursor.getString(categoryNameColumnIndex));
                    } else {
                         book.setCategoryName( (book.getCategoryId()==0 || cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID))) ? "未分类" : "分类未知");
                    }
                    books.add(book);
                } while (cursor.moveToNext());
            }
            Log.i(TAG, "获取到 " + books.size() + " 本图书");
        } catch (Exception e) {
            Log.e(TAG, "getAllBooks: 获取所有图书时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return books;
    }

    /**
     * 更新图书信息
     * @param book Book对象，必须包含ID
     * @return 受影响的行数
     */
    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_TITLE, book.getTitle());
        values.put(COLUMN_BOOK_AUTHOR, book.getAuthor());
        values.put(COLUMN_BOOK_ISBN, book.getIsbn());
        values.put(COLUMN_BOOK_COVER_IMAGE, book.getCoverImage());
        values.put(COLUMN_BOOK_DESCRIPTION, book.getDescription());
        values.put(COLUMN_BOOK_PUBLISH_DATE, book.getPublishDate());
        if (book.getCategoryId() > 0) {
            values.put(COLUMN_BOOK_CATEGORY_ID, book.getCategoryId());
        } else {
            values.putNull(COLUMN_BOOK_CATEGORY_ID);
        }

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_BOOK, values, COLUMN_BOOK_ID + "=?",
                    new String[]{String.valueOf(book.getId())});
            if (rowsAffected > 0) {
                Log.i(TAG, "图书信息更新成功: " + book.getTitle());
            } else {
                Log.w(TAG, "图书信息更新失败或图书不存在: " + book.getTitle());
            }
        } catch (Exception e) {
            Log.e(TAG, "updateBook: 更新图书信息时发生错误", e);
        } finally {
            db.close();
        }
        return rowsAffected;
    }

    /**
     * 删除图书
     * @param bookId 图书ID
     * @return 受影响的行数 (通常为1如果删除成功)
     */
    public int deleteBook(long bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(TABLE_BOOK, COLUMN_BOOK_ID + "=?",
                    new String[]{String.valueOf(bookId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "图书删除成功, ID: " + bookId);
            } else {
                Log.w(TAG, "图书删除失败或图书不存在, ID: " + bookId);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteBook: 删除图书时发生错误", e);
        } finally {
            db.close();
        }
        return rowsAffected;
    }

    // ---- 统计信息 ----

    /**
     * 获取图书总数
     * @return 图书总数
     */
    public int getTotalBookCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOK, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getTotalBookCount: 获取图书总数时出错", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return count;
    }

    /**
     * 获取分类总数
     * @return 分类总数
     */
    public int getTotalCategoryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CATEGORY, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getTotalCategoryCount: 获取分类总数时出错", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return count;
    }

    /**
     * 获取各分类下的图书数量
     * @return Map<String, Integer> 其中 Key 是分类名称，Value 是该分类下的图书数量
     */
    public Map<String, Integer> getBookCountPerCategory() {
        Map<String, Integer> categoryCounts = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT c." + COLUMN_CATEGORY_NAME + ", COUNT(b." + COLUMN_BOOK_ID + ") as book_count " +
                       "FROM " + TABLE_CATEGORY + " c LEFT JOIN " + TABLE_BOOK + " b " +
                       "ON c." + COLUMN_CATEGORY_ID + " = b." + COLUMN_BOOK_CATEGORY_ID + " " +
                       "GROUP BY c." + COLUMN_CATEGORY_ID + ", c." + COLUMN_CATEGORY_NAME + " " +
                       "ORDER BY c." + COLUMN_CATEGORY_NAME + " ASC";
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
                    int bookCount = cursor.getInt(cursor.getColumnIndexOrThrow("book_count"));
                    categoryCounts.put(categoryName != null ? categoryName : "未分类", bookCount);
                } while (cursor.moveToNext());
            }
            Log.i(TAG, "获取各分类图书数量成功，分类数: " + categoryCounts.size());
        } catch (Exception e) {
            Log.e(TAG, "getBookCountPerCategory: 获取各分类图书数量时发生错误", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        String unCategorizedQuery = "SELECT COUNT(*) FROM " + TABLE_BOOK + " WHERE " + COLUMN_BOOK_CATEGORY_ID + " IS NULL OR " + COLUMN_BOOK_CATEGORY_ID + " = 0";
        Cursor uncatCursor = null;
        try {
            uncatCursor = db.rawQuery(unCategorizedQuery, null);
            if (uncatCursor.moveToFirst()) {
                int uncatCount = uncatCursor.getInt(0);
                if (uncatCount > 0) {
                    if (!categoryCounts.containsKey("未分类") || categoryCounts.get("未分类") == 0 ) {
                         categoryCounts.put("未分类图书", uncatCount);
                    } else if (categoryCounts.containsKey("未分类") && categoryCounts.get("未分类") ==0 && uncatCount >0 ) {
                        categoryCounts.put("未分类图书", uncatCount);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getBookCountPerCategory: 查询未分类图书数量时出错", e);
        } finally {
            if (uncatCursor != null) uncatCursor.close();
        }

        return categoryCounts;
    }

    // loadedBooks = dbHelper.searchBooks(query);
    // 在 OpenHelperDataBase.java 中使用这个正确的方法

    /**
     * 根据关键字搜索书籍 (标题、作者或分类名)
     * @param query 搜索关键字
     * @return 匹配的书籍列表
     */
    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        // 1. 处理边缘情况：如果搜索词为空，返回所有书籍
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // 2. 正确的SQL查询：使用 LEFT JOIN 连接分类表，以便能搜索分类名
        String searchQuery = "SELECT b.*, c." + COLUMN_CATEGORY_NAME + " AS category_name FROM " + TABLE_BOOK + " b LEFT JOIN " +
                TABLE_CATEGORY + " c ON b." + COLUMN_BOOK_CATEGORY_ID + " = c." + COLUMN_CATEGORY_ID +
                " WHERE b." + COLUMN_BOOK_TITLE + " LIKE ? OR b." + COLUMN_BOOK_AUTHOR + " LIKE ? OR c." + COLUMN_CATEGORY_NAME + " LIKE ?" + // 按标题、作者、分类名搜索
                " ORDER BY b." + COLUMN_BOOK_TITLE + " ASC";

        // 3. 为 LIKE 子句准备带通配符的参数
        String searchPattern = "%" + query + "%";

        try {
            // 4. 使用参数绑定执行查询，防止SQL注入
            cursor = db.rawQuery(searchQuery, new String[]{searchPattern, searchPattern, searchPattern});

            // 5. 正确的循环逻辑
            if (cursor.moveToFirst()) {
                do {
                    // 5a. 在循环【内部】创建新的Book对象
                    Book book = new Book();

                    // 5b. 从Cursor中读取每一列的数据，并设置到Book对象中
                    book.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)));
                    book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_TITLE)));
                    book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR)));
                    book.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ISBN)));
                    book.setCoverImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_COVER_IMAGE)));
                    book.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_DESCRIPTION)));
                    book.setPublishDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PUBLISH_DATE)));
                    book.setCategoryId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID)));
                    book.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_CREATE_TIME)));

                    int categoryNameColumnIndex = cursor.getColumnIndex("category_name");
                    if (categoryNameColumnIndex != -1 && !cursor.isNull(categoryNameColumnIndex)) {
                        book.setCategoryName(cursor.getString(categoryNameColumnIndex));
                    } else {
                        book.setCategoryName("未分类");
                    }

                    // 5c. 将【填充好数据】的Book对象添加到列表中
                    books.add(book);

                } while (cursor.moveToNext());
            }
            Log.i(TAG, "搜索 '" + query + "' 找到 " + books.size() + " 本书");
        } catch (Exception e) {
            Log.e(TAG, "searchBooks: 搜索图书时发生错误", e);
        } finally {
            // 6. 在 finally 块中确保资源被关闭
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return books;
    }

} 