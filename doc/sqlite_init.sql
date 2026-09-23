-- 1. 用户表 (UserEntity)
CREATE TABLE IF NOT EXISTS `users` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `username` TEXT NOT NULL DEFAULT '',
    `phone` TEXT NOT NULL DEFAULT '',
    `password` TEXT NOT NULL DEFAULT '',
    `email` TEXT,
    `height` REAL,
    `weight` REAL,
    `avatar` TEXT,
    `createdAt` INTEGER NOT NULL,
    `updatedAt` INTEGER NOT NULL
);

-- 2. 社区帖子表 (CommunityPostEntity)
CREATE TABLE IF NOT EXISTS `community_posts` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `content` TEXT NOT NULL DEFAULT '',
    `imagePath` TEXT,
    `likesCount` INTEGER NOT NULL DEFAULT 0,
    `createdAt` TEXT NOT NULL DEFAULT '',
    `updatedAt` TEXT NOT NULL DEFAULT ''
);

-- 3. 帖子评论表 (PostCommentEntity)
CREATE TABLE IF NOT EXISTS `post_comments` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `postId` INTEGER NOT NULL,
    `userId` INTEGER NOT NULL,
    `content` TEXT NOT NULL DEFAULT '',
    `createdAt` TEXT NOT NULL DEFAULT '',
    `updatedAt` TEXT NOT NULL DEFAULT ''
);

-- 4. 日记表 (DiaryEntity)
CREATE TABLE IF NOT EXISTS `diaries` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `content` TEXT NOT NULL DEFAULT '',
    `mood` INTEGER NOT NULL DEFAULT 0,
    `createdAt` TEXT NOT NULL DEFAULT '',
    `updatedAt` TEXT NOT NULL DEFAULT ''
);

-- 5. 运动记录表 (ExerciseRecordEntity)
CREATE TABLE IF NOT EXISTS `exercise_records` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `exerciseType` TEXT NOT NULL DEFAULT '',
    `location` TEXT NOT NULL DEFAULT '',
    `duration` INTEGER NOT NULL DEFAULT 0,
    `createdAt` TEXT NOT NULL DEFAULT '',
    `updatedAt` TEXT NOT NULL DEFAULT ''
);

-- 6. 健身计划表 (FitnessPlanEntity)
CREATE TABLE IF NOT EXISTS `fitness_plans` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `goal` TEXT NOT NULL DEFAULT '',
    `planContent` TEXT NOT NULL DEFAULT '',
    `createdAt` TEXT NOT NULL DEFAULT '',
    `updatedAt` TEXT NOT NULL DEFAULT ''
);

-- 7. 习惯表 (HabitEntity)
CREATE TABLE IF NOT EXISTS `habits` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `name` TEXT NOT NULL DEFAULT '',
    `createdAt` INTEGER NOT NULL
);

-- 8. 习惯打卡记录表 (HabitCheckinEntity)
CREATE TABLE IF NOT EXISTS `habit_checkins` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `habitId` INTEGER NOT NULL,
    `userId` INTEGER NOT NULL,
    `checkinDate` TEXT NOT NULL DEFAULT '',
    `createdAt` INTEGER NOT NULL
);

-- 9. 用药提醒表 (MedicationReminderEntity)
CREATE TABLE IF NOT EXISTS `medication_reminders` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `medicineName` TEXT NOT NULL DEFAULT '',
    `dosage` TEXT NOT NULL DEFAULT '',
    `reminderTime` TEXT NOT NULL DEFAULT '',  -- 格式: HH:mm
    `lastTakenDate` TEXT NOT NULL DEFAULT '',
    `enabled` INTEGER NOT NULL DEFAULT 0,    -- 0 为 false, 1 为 true
    `createdAt` INTEGER NOT NULL,
    `updatedAt` INTEGER NOT NULL
);

-- 10. 快捷记录表 (QuickRecordEntity)
CREATE TABLE IF NOT EXISTS `quick_records` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `userId` INTEGER NOT NULL,
    `type` TEXT NOT NULL DEFAULT '',
    `value` TEXT,
    `note` TEXT,
    `createdAt` INTEGER NOT NULL
);