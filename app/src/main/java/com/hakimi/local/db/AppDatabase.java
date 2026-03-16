package com.hakimi.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hakimi.local.db.dao.HabitDao;
import com.hakimi.local.db.dao.CommunityPostDao;
import com.hakimi.local.db.dao.DiaryDao;
import com.hakimi.local.db.dao.ExerciseRecordDao;
import com.hakimi.local.db.dao.FitnessPlanDao;
import com.hakimi.local.db.dao.MedicationReminderDao;
import com.hakimi.local.db.dao.PostCommentDao;
import com.hakimi.local.db.dao.QuickRecordDao;
import com.hakimi.local.db.dao.UserDao;
import com.hakimi.local.db.entity.CommunityPostEntity;
import com.hakimi.local.db.entity.DiaryEntity;
import com.hakimi.local.db.entity.ExerciseRecordEntity;
import com.hakimi.local.db.entity.FitnessPlanEntity;
import com.hakimi.local.db.entity.HabitCheckinEntity;
import com.hakimi.local.db.entity.HabitEntity;
import com.hakimi.local.db.entity.MedicationReminderEntity;
import com.hakimi.local.db.entity.PostCommentEntity;
import com.hakimi.local.db.entity.QuickRecordEntity;
import com.hakimi.local.db.entity.UserEntity;

@Database(entities = {
        UserEntity.class,
        DiaryEntity.class,
        QuickRecordEntity.class,
        HabitEntity.class,
        HabitCheckinEntity.class,
        MedicationReminderEntity.class,
        CommunityPostEntity.class,
        PostCommentEntity.class,
        ExerciseRecordEntity.class,
        FitnessPlanEntity.class
}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "health_assistant.db";
    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract UserDao userDao();

    public abstract DiaryDao diaryDao();

    public abstract QuickRecordDao quickRecordDao();

    public abstract HabitDao habitDao();

    public abstract MedicationReminderDao medicationReminderDao();

    public abstract CommunityPostDao communityPostDao();

    public abstract PostCommentDao postCommentDao();

    public abstract ExerciseRecordDao exerciseRecordDao();

    public abstract FitnessPlanDao fitnessPlanDao();
}
