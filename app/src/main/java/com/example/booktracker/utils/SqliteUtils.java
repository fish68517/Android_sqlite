package com.example.booktracker.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;

public class SqliteUtils  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book_tracker.db";
    private static final int DATABASE_VERSION = 2; // 升级版本号
    private static final String TAG = "SqliteUtils";

    public SqliteUtils() {
        super(AppUtils.getApplication(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建并获取单例
     */
    public static SqliteUtils getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        用户表(_user)：
        _id       integer  用户id
        username  varchar  用户名
        password  varchar  密码
         */
        sqLiteDatabase.execSQL("CREATE TABLE _user(_id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR(20) ,password VARCHAR(20))");
        /*
        图书表(_book):
        _id           integer  图书id
        name          varchar  书名
        author        varchar  作者
        description   varchar  描述
        url           varchar  图片url
        total         integer  总数量
        remain        integer  剩余数量
         */
        sqLiteDatabase.execSQL("CREATE TABLE _book(_id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),author VARCHAR(20),description VARCHAR(200),url VARCHAR(200),total INTEGER,remain INTEGER)");
        /*
        借阅记录表(_borrow)：
        _id          integer   借阅id
        user_id      integer   用户id
        book_id      integer   图书id
        borrow_date  varchar   借出日期
        return_date  varchar   归还日期
         */
        sqLiteDatabase.execSQL("CREATE TABLE _borrow(_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,book_id INTEGER,borrow_date VARCHAR(20),return_date VARCHAR(20))");
        
        /*
        订单表(t_order)：修改表名，避免使用SQL关键字
        _id          integer   订单id
        user_id      integer   用户id
        book_id      integer   图书id
        book_name    varchar   图书名称
        book_url     varchar   图书封面
        quantity     integer   购买数量
        create_time  varchar   创建时间
        status       integer   订单状态：0-待发货，1-已发货，2-已收货
         */
        sqLiteDatabase.execSQL("CREATE TABLE t_order(_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,book_id INTEGER,book_name VARCHAR(20),book_url VARCHAR(200),quantity INTEGER,create_time VARCHAR(20),status INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        
        // 版本1到版本2的升级：修改订单表名
        if (oldVersion == 1 && newVersion == 2) {
            try {
                // 检查旧表是否存在
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='_order'", null);
                boolean tableExists = cursor.getCount() > 0;
                cursor.close();
                
                if (tableExists) {
                    // 步骤1：创建新表
                    sqLiteDatabase.execSQL("CREATE TABLE t_order(_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,book_id INTEGER,book_name VARCHAR(20),book_url VARCHAR(200),quantity INTEGER,create_time VARCHAR(20),status INTEGER)");
                    
                    // 步骤2：复制数据（如果有的话）
                    sqLiteDatabase.execSQL("INSERT INTO t_order SELECT * FROM _order");
                    
                    // 步骤3：删除旧表
                    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS _order");
                    
                    Log.d(TAG, "Upgrade successful: _order table renamed to t_order");
                } else {
                    // 如果旧表不存在，直接创建新表
                    sqLiteDatabase.execSQL("CREATE TABLE t_order(_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,book_id INTEGER,book_name VARCHAR(20),book_url VARCHAR(200),quantity INTEGER,create_time VARCHAR(20),status INTEGER)");
                    Log.d(TAG, "Created new table t_order directly");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during database upgrade: " + e.getMessage(), e);
                // 如果升级失败，确保至少有新表
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS t_order(_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,book_id INTEGER,book_name VARCHAR(20),book_url VARCHAR(200),quantity INTEGER,create_time VARCHAR(20),status INTEGER)");
            }
        }
    }


    private static final class InstanceHolder {
        /**
         * 单例
         */
        static final SqliteUtils instance = new SqliteUtils();
    }
}
