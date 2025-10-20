package com.example.application.model;// =================================================================================
// 文件路径: app/src/main/java/com/example/stylehub/data/local/AppDatabase.java
// 任务: 离线模式 (Offline Mode with Room) - 级别 3
// 描述: Room数据库的主类。负责创建数据库实例。
// =================================================================================


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Product.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "stylehub_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}