package com.Health.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.Health.local.db.dao.HabitDao;
import com.Health.local.db.dao.HealthAlertDao;
import com.Health.local.db.dao.HealthAlertRuleDao;
import com.Health.local.db.dao.HealthMetricDao;
import com.Health.local.db.dao.CommunityPostDao;
import com.Health.local.db.dao.DiaryDao;
import com.Health.local.db.dao.ExerciseRecordDao;
import com.Health.local.db.dao.FitnessPlanDao;
import com.Health.local.db.dao.MedicationReminderDao;
import com.Health.local.db.dao.PostCommentDao;
import com.Health.local.db.dao.QuickRecordDao;
import com.Health.local.db.dao.UserDao;
import com.Health.local.db.entity.CommunityPostEntity;
import com.Health.local.db.entity.DiaryEntity;
import com.Health.local.db.entity.ExerciseRecordEntity;
import com.Health.local.db.entity.FitnessPlanEntity;
import com.Health.local.db.entity.HabitCheckinEntity;
import com.Health.local.db.entity.HabitEntity;
import com.Health.local.db.entity.HealthAlertEntity;
import com.Health.local.db.entity.HealthAlertRuleEntity;
import com.Health.local.db.entity.HealthMetricEntity;
import com.Health.local.db.entity.MedicationReminderEntity;
import com.Health.local.db.entity.PostCommentEntity;
import com.Health.local.db.entity.QuickRecordEntity;
import com.Health.local.db.entity.UserEntity;

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
        FitnessPlanEntity.class,
        HealthMetricEntity.class,
        HealthAlertRuleEntity.class,
        HealthAlertEntity.class
}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "health_assistant.db";
    private static volatile AppDatabase instance;
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `health_metric_records` ("
                    + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`userId` INTEGER NOT NULL, "
                    + "`metricType` TEXT NOT NULL, "
                    + "`recordScope` TEXT NOT NULL, "
                    + "`valuePrimary` REAL, "
                    + "`valueSecondary` REAL, "
                    + "`unit` TEXT NOT NULL, "
                    + "`sourceType` TEXT NOT NULL, "
                    + "`sampleTime` INTEGER NOT NULL, "
                    + "`sampleDay` TEXT NOT NULL, "
                    + "`createdAt` INTEGER NOT NULL, "
                    + "`updatedAt` INTEGER NOT NULL)");
            database.execSQL("CREATE INDEX IF NOT EXISTS "
                    + "`index_health_metric_records_userId_metricType_recordScope_sampleTime` "
                    + "ON `health_metric_records` (`userId`, `metricType`, `recordScope`, `sampleTime`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS "
                    + "`index_health_metric_records_userId_metricType_recordScope_sourceType_sampleDay` "
                    + "ON `health_metric_records` (`userId`, `metricType`, `recordScope`, `sourceType`, `sampleDay`)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `health_alert_rules` ("
                    + "`userId` INTEGER NOT NULL, "
                    + "`heartRateLow` INTEGER NOT NULL, "
                    + "`heartRateHigh` INTEGER NOT NULL, "
                    + "`systolicLow` INTEGER NOT NULL, "
                    + "`systolicHigh` INTEGER NOT NULL, "
                    + "`diastolicLow` INTEGER NOT NULL, "
                    + "`diastolicHigh` INTEGER NOT NULL, "
                    + "`stepGoal` INTEGER NOT NULL, "
                    + "`stepAlertThreshold` INTEGER NOT NULL, "
                    + "`voiceEnabled` INTEGER NOT NULL, "
                    + "`updatedAt` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`userId`))");

            database.execSQL("CREATE TABLE IF NOT EXISTS `health_alert_records` ("
                    + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`userId` INTEGER NOT NULL, "
                    + "`metricType` TEXT NOT NULL, "
                    + "`alertKey` TEXT NOT NULL, "
                    + "`alertLevel` TEXT NOT NULL, "
                    + "`title` TEXT NOT NULL, "
                    + "`content` TEXT NOT NULL, "
                    + "`valuePrimary` REAL, "
                    + "`valueSecondary` REAL, "
                    + "`sourceType` TEXT NOT NULL, "
                    + "`acknowledged` INTEGER NOT NULL, "
                    + "`createdAt` INTEGER NOT NULL)");
            database.execSQL("CREATE INDEX IF NOT EXISTS "
                    + "`index_health_alert_records_userId_createdAt` "
                    + "ON `health_alert_records` (`userId`, `createdAt`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS "
                    + "`index_health_alert_records_userId_alertKey` "
                    + "ON `health_alert_records` (`userId`, `alertKey`)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .addMigrations(MIGRATION_3_4)
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

    public abstract HealthMetricDao healthMetricDao();

    public abstract HealthAlertRuleDao healthAlertRuleDao();

    public abstract HealthAlertDao healthAlertDao();
}
