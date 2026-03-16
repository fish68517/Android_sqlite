package com.hakimi.local;

import android.content.Context;
import android.text.TextUtils;

import com.hakimi.local.db.AppDatabase;
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
import com.hakimi.local.model.HabitStatus;
import com.hakimi.local.model.HealthCheckinSummary;
import com.hakimi.local.model.MedicationReminderItem;
import com.hakimi.local.model.QuickRecordItem;
import com.hakimi.model.Comment;
import com.hakimi.model.Diary;
import com.hakimi.model.ExerciseData;
import com.hakimi.model.FitnessPlan;
import com.hakimi.model.Post;
import com.hakimi.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalHealthRepository {

    public static final String QUICK_TYPE_WATER = "WATER";
    public static final String QUICK_TYPE_MEDICINE = "MEDICINE";
    public static final String QUICK_TYPE_WEIGHT = "WEIGHT";

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private static volatile LocalHealthRepository instance;
    private final AppDatabase database;

    private LocalHealthRepository(Context context) {
        this.database = AppDatabase.getInstance(context);
    }

    public static LocalHealthRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalHealthRepository.class) {
                if (instance == null) {
                    instance = new LocalHealthRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void ensureSeedData() {
        if (database.userDao().count() > 0) {
            return;
        }

        String now = nowText();

        UserEntity demoUser = new UserEntity();
        demoUser.username = "演示用户";
        demoUser.phone = "13800000000";
        demoUser.password = "123456";
        demoUser.email = "demo@health.local";
        demoUser.height = 170.0;
        demoUser.weight = 65.0;
        demoUser.createdAt = System.currentTimeMillis();
        demoUser.updatedAt = demoUser.createdAt;
        long userId = database.userDao().insert(demoUser);

        DiaryEntity d1 = new DiaryEntity();
        d1.userId = userId;
        d1.content = "今天完成了30分钟快走，状态不错。";
        d1.mood = 3;
        d1.createdAt = now;
        d1.updatedAt = now;
        database.diaryDao().insert(d1);

        CommunityPostEntity post = new CommunityPostEntity();
        post.userId = userId;
        post.content = "第一天使用本地版健康助手，欢迎留言交流。";
        post.likesCount = 2;
        post.createdAt = now;
        post.updatedAt = now;
        long postId = database.communityPostDao().insert(post);

        PostCommentEntity c1 = new PostCommentEntity();
        c1.postId = postId;
        c1.userId = userId;
        c1.content = "这是本地模拟评论。";
        c1.createdAt = now;
        c1.updatedAt = now;
        database.postCommentDao().insert(c1);
    }

    public LocalResult<User> register(String username, String phone, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            return LocalResult.fail("请填写完整注册信息");
        }

        UserEntity duplicated = database.userDao().findDuplicated(username, phone);
        if (duplicated != null) {
            return LocalResult.fail("用户名或手机号已存在");
        }

        long now = System.currentTimeMillis();
        UserEntity entity = new UserEntity();
        entity.username = username;
        entity.phone = phone;
        entity.password = password;
        entity.email = phone;
        entity.createdAt = now;
        entity.updatedAt = now;
        long id = database.userDao().insert(entity);
        entity.id = id;
        return LocalResult.success(toUserModel(entity));
    }

    public LocalResult<User> login(String account, String password) {
        UserEntity user = database.userDao().findByAccount(account);
        if (user == null || !password.equals(user.password)) {
            return LocalResult.fail("账号或密码错误");
        }
        return LocalResult.success(toUserModel(user));
    }

    public User getUserById(long userId) {
        return toUserModel(database.userDao().findById(userId));
    }

    public LocalResult<User> updateBodyData(long userId, String username, String email,
            Double height, Double weight, String avatar) {
        UserEntity user = database.userDao().findById(userId);
        if (user == null) {
            return LocalResult.fail("用户不存在");
        }
        if (!TextUtils.isEmpty(username)) {
            user.username = username;
        }
        user.email = email;
        user.height = height;
        user.weight = weight;
        user.avatar = avatar;
        user.updatedAt = System.currentTimeMillis();
        database.userDao().update(user);
        return LocalResult.success(toUserModel(user));
    }

    public LocalResult<Diary> createDiary(long userId, String content, int mood) {
        if (TextUtils.isEmpty(content)) {
            return LocalResult.fail("请输入日记内容");
        }

        String now = nowText();
        DiaryEntity entity = new DiaryEntity();
        entity.userId = userId;
        entity.content = content;
        entity.mood = mood;
        entity.createdAt = now;
        entity.updatedAt = now;
        long id = database.diaryDao().insert(entity);
        entity.id = id;
        return LocalResult.success(toDiaryModel(entity));
    }

    public List<Diary> getDiaries(long userId) {
        List<DiaryEntity> entities = database.diaryDao().findByUserId(userId);
        List<Diary> list = new ArrayList<>();
        for (DiaryEntity entity : entities) {
            list.add(toDiaryModel(entity));
        }
        return list;
    }

    public LocalResult<Void> addQuickRecord(long userId, String type, String value, String note) {
        if (userId <= 0 || TextUtils.isEmpty(type)) {
            return LocalResult.fail("记录参数无效");
        }
        QuickRecordEntity entity = new QuickRecordEntity();
        entity.userId = userId;
        entity.type = type;
        entity.value = value;
        entity.note = note;
        entity.createdAt = System.currentTimeMillis();
        database.quickRecordDao().insert(entity);
        return LocalResult.success(null);
    }

    public int getQuickRecordCountByType(long userId, String type) {
        return database.quickRecordDao().countByType(userId, type);
    }

    public List<QuickRecordItem> listQuickRecords(long userId, int limit) {
        List<QuickRecordEntity> entities = database.quickRecordDao().latestByUser(userId, limit);
        List<QuickRecordItem> list = new ArrayList<>();
        for (QuickRecordEntity entity : entities) {
            QuickRecordItem item = new QuickRecordItem();
            item.setType(entity.type);
            item.setValue(entity.value);
            item.setNote(entity.note);
            item.setCreatedAt(entity.createdAt);
            list.add(item);
        }
        return list;
    }

    public HealthCheckinSummary getHealthCheckinSummary(long userId) {
        HealthCheckinSummary summary = new HealthCheckinSummary();

        summary.setWaterCount(getQuickRecordCountByType(userId, QUICK_TYPE_WATER));
        summary.setMedicineCount(getQuickRecordCountByType(userId, QUICK_TYPE_MEDICINE));
        summary.setWeightCount(getQuickRecordCountByType(userId, QUICK_TYPE_WEIGHT));

        QuickRecordEntity latestWeight = database.quickRecordDao().latestByType(userId, QUICK_TYPE_WEIGHT);
        summary.setLatestWeight(latestWeight == null ? "--" : latestWeight.value);

        List<HabitStatus> habits = listHabitsWithStatus(userId);
        int checkedToday = 0;
        for (HabitStatus habit : habits) {
            if (habit.isCheckedToday()) {
                checkedToday++;
            }
        }
        summary.setHabitCount(habits.size());
        summary.setHabitCheckedTodayCount(checkedToday);

        List<MedicationReminderItem> reminders = listAllMedicationReminders(userId);
        int enabled = 0;
        for (MedicationReminderItem item : reminders) {
            if (item.isEnabled()) {
                enabled++;
            }
        }
        summary.setReminderCount(reminders.size());
        summary.setReminderEnabledCount(enabled);

        return summary;
    }

    public LocalResult<Long> addHabit(long userId, String name) {
        if (userId <= 0 || TextUtils.isEmpty(name)) {
            return LocalResult.fail("请输入习惯名称");
        }
        HabitEntity entity = new HabitEntity();
        entity.userId = userId;
        entity.name = name;
        entity.createdAt = System.currentTimeMillis();
        long id = database.habitDao().insertHabit(entity);
        return LocalResult.success(id);
    }

    public LocalResult<Void> checkinHabit(long userId, long habitId) {
        String today = LocalDate.now().toString();
        if (database.habitDao().checkinCountByDate(habitId, today) > 0) {
            return LocalResult.fail("今天已打卡");
        }
        HabitCheckinEntity entity = new HabitCheckinEntity();
        entity.userId = userId;
        entity.habitId = habitId;
        entity.checkinDate = today;
        entity.createdAt = System.currentTimeMillis();
        database.habitDao().insertCheckin(entity);
        return LocalResult.success(null);
    }

    public List<HabitStatus> listHabitsWithStatus(long userId) {
        String today = LocalDate.now().toString();
        List<HabitEntity> habits = database.habitDao().findByUserId(userId);
        List<HabitStatus> result = new ArrayList<>();
        for (HabitEntity habit : habits) {
            HabitStatus status = new HabitStatus();
            status.setHabitId(habit.id);
            status.setHabitName(habit.name);
            status.setCheckedToday(database.habitDao().checkinCountByDate(habit.id, today) > 0);
            result.add(status);
        }
        return result;
    }

    public LocalResult<Long> addMedicationReminder(long userId, String medicineName,
            String dosage, String reminderTime) {
        if (userId <= 0 || TextUtils.isEmpty(medicineName) || TextUtils.isEmpty(dosage)
                || TextUtils.isEmpty(reminderTime)) {
            return LocalResult.fail("请填写完整用药提醒信息");
        }
        MedicationReminderEntity entity = new MedicationReminderEntity();
        entity.userId = userId;
        entity.medicineName = medicineName;
        entity.dosage = dosage;
        entity.reminderTime = reminderTime;
        entity.lastTakenDate = "";
        entity.enabled = 1;
        entity.createdAt = System.currentTimeMillis();
        entity.updatedAt = entity.createdAt;
        long id = database.medicationReminderDao().insert(entity);
        return LocalResult.success(id);
    }

    public List<MedicationReminderItem> listEnabledMedicationReminders(long userId) {
        List<MedicationReminderEntity> entities = database.medicationReminderDao().enabledByUser(userId);
        List<MedicationReminderItem> result = new ArrayList<>();
        for (MedicationReminderEntity entity : entities) {
            result.add(toMedicationModel(entity));
        }
        return result;
    }

    public List<MedicationReminderItem> listAllMedicationReminders(long userId) {
        List<MedicationReminderEntity> entities = database.medicationReminderDao().allByUser(userId);
        List<MedicationReminderItem> result = new ArrayList<>();
        for (MedicationReminderEntity entity : entities) {
            result.add(toMedicationModel(entity));
        }
        return result;
    }

    public MedicationReminderItem getMedicationReminder(long reminderId) {
        return toMedicationModel(database.medicationReminderDao().findById(reminderId));
    }

    public LocalResult<Void> markMedicationTaken(long reminderId, String date) {
        MedicationReminderEntity entity = database.medicationReminderDao().findById(reminderId);
        if (entity == null) {
            return LocalResult.fail("提醒不存在");
        }
        entity.lastTakenDate = date;
        entity.updatedAt = System.currentTimeMillis();
        database.medicationReminderDao().update(entity);
        return LocalResult.success(null);
    }

    public LocalResult<Void> setMedicationReminderEnabled(long reminderId, boolean enabled) {
        int updated = database.medicationReminderDao()
                .updateEnabled(reminderId, enabled ? 1 : 0, System.currentTimeMillis());
        return updated > 0 ? LocalResult.success(null) : LocalResult.fail("更新失败");
    }

    public LocalResult<Void> deleteMedicationReminder(long reminderId) {
        int deleted = database.medicationReminderDao().deleteById(reminderId);
        return deleted > 0 ? LocalResult.success(null) : LocalResult.fail("删除失败");
    }

    public LocalResult<Post> createPost(long userId, String content, String imagePath) {
        if (userId <= 0 || TextUtils.isEmpty(content)) {
            return LocalResult.fail("帖子内容不能为空");
        }
        String now = nowText();
        CommunityPostEntity entity = new CommunityPostEntity();
        entity.userId = userId;
        entity.content = content;
        entity.imagePath = imagePath;
        entity.likesCount = 0;
        entity.createdAt = now;
        entity.updatedAt = now;
        long id = database.communityPostDao().insert(entity);
        entity.id = id;
        return LocalResult.success(toPostModel(entity, new ArrayList<>()));
    }

    public List<Post> getPosts(int limit) {
        List<CommunityPostEntity> entities = database.communityPostDao().findLatest(limit);
        List<Post> result = new ArrayList<>();
        for (CommunityPostEntity entity : entities) {
            List<PostCommentEntity> comments = database.postCommentDao().findByPostId(entity.id);
            result.add(toPostModel(entity, toCommentModels(comments)));
        }
        return result;
    }

    public LocalResult<Post> likePost(long postId) {
        CommunityPostEntity entity = database.communityPostDao().findById(postId);
        if (entity == null) {
            return LocalResult.fail("帖子不存在");
        }
        int likes = entity.likesCount + 1;
        String now = nowText();
        database.communityPostDao().updateLikes(postId, likes, now);
        entity.likesCount = likes;
        entity.updatedAt = now;
        List<PostCommentEntity> comments = database.postCommentDao().findByPostId(postId);
        return LocalResult.success(toPostModel(entity, toCommentModels(comments)));
    }

    public List<Comment> getPostComments(long postId) {
        return toCommentModels(database.postCommentDao().findByPostId(postId));
    }

    public LocalResult<Comment> createComment(long postId, long userId, String content) {
        if (postId <= 0 || userId <= 0 || TextUtils.isEmpty(content)) {
            return LocalResult.fail("评论参数无效");
        }
        if (database.communityPostDao().findById(postId) == null) {
            return LocalResult.fail("帖子不存在");
        }
        String now = nowText();
        PostCommentEntity entity = new PostCommentEntity();
        entity.postId = postId;
        entity.userId = userId;
        entity.content = content;
        entity.createdAt = now;
        entity.updatedAt = now;
        long id = database.postCommentDao().insert(entity);
        entity.id = id;
        return LocalResult.success(toCommentModel(entity));
    }

    public LocalResult<ExerciseData> addExerciseRecord(long userId, String type, String location, int duration) {
        if (userId <= 0 || TextUtils.isEmpty(type) || duration <= 0) {
            return LocalResult.fail("运动参数无效");
        }
        String now = nowText();
        ExerciseRecordEntity entity = new ExerciseRecordEntity();
        entity.userId = userId;
        entity.exerciseType = type;
        entity.location = TextUtils.isEmpty(location) ? "" : location;
        entity.duration = duration;
        entity.createdAt = now;
        entity.updatedAt = now;
        long id = database.exerciseRecordDao().insert(entity);
        entity.id = id;
        return LocalResult.success(toExerciseModel(entity));
    }

    public List<ExerciseData> getExerciseRecordsByUser(long userId) {
        List<ExerciseRecordEntity> entities = database.exerciseRecordDao().findByUserId(userId);
        List<ExerciseData> result = new ArrayList<>();
        for (ExerciseRecordEntity entity : entities) {
            result.add(toExerciseModel(entity));
        }
        return result;
    }

    public LocalResult<FitnessPlan> saveFitnessPlan(long userId, String goal, String planContent) {
        if (userId <= 0 || TextUtils.isEmpty(goal) || TextUtils.isEmpty(planContent)) {
            return LocalResult.fail("计划参数无效");
        }
        String now = nowText();
        FitnessPlanEntity entity = new FitnessPlanEntity();
        entity.userId = userId;
        entity.goal = goal;
        entity.planContent = planContent;
        entity.createdAt = now;
        entity.updatedAt = now;
        long id = database.fitnessPlanDao().insert(entity);
        entity.id = id;
        return LocalResult.success(toFitnessPlanModel(entity));
    }

    private String nowText() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    private User toUserModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.id);
        user.setUsername(entity.username);
        user.setPhone(entity.phone);
        user.setEmail(entity.email);
        user.setHeight(entity.height);
        user.setWeight(entity.weight);
        user.setAvatar(entity.avatar);
        return user;
    }

    private Diary toDiaryModel(DiaryEntity entity) {
        if (entity == null) {
            return null;
        }
        Diary diary = new Diary();
        diary.setId(entity.id);
        diary.setUserId(entity.userId);
        diary.setContent(entity.content);
        diary.setMood(entity.mood);
        diary.setCreatedAt(entity.createdAt);
        diary.setUpdatedAt(entity.updatedAt);
        return diary;
    }

    private MedicationReminderItem toMedicationModel(MedicationReminderEntity entity) {
        if (entity == null) {
            return null;
        }
        MedicationReminderItem item = new MedicationReminderItem();
        item.setId(entity.id);
        item.setMedicineName(entity.medicineName);
        item.setDosage(entity.dosage);
        item.setReminderTime(entity.reminderTime);
        item.setLastTakenDate(entity.lastTakenDate);
        item.setEnabled(entity.enabled == 1);
        return item;
    }

    private Post toPostModel(CommunityPostEntity entity, List<Comment> comments) {
        Post post = new Post();
        post.setId(entity.id);
        post.setUserId(entity.userId);
        post.setContent(entity.content);
        post.setImagePath(entity.imagePath);
        post.setLikesCount(entity.likesCount);
        post.setCreatedAt(entity.createdAt);
        post.setUpdatedAt(entity.updatedAt);
        post.setComments(comments);
        return post;
    }

    private List<Comment> toCommentModels(List<PostCommentEntity> entities) {
        List<Comment> comments = new ArrayList<>();
        for (PostCommentEntity entity : entities) {
            comments.add(toCommentModel(entity));
        }
        return comments;
    }

    private Comment toCommentModel(PostCommentEntity entity) {
        Comment comment = new Comment();
        comment.setId(entity.id);
        comment.setPostId(entity.postId);
        comment.setUserId(entity.userId);
        comment.setContent(entity.content);
        comment.setCreatedAt(entity.createdAt);
        comment.setUpdatedAt(entity.updatedAt);
        return comment;
    }

    private ExerciseData toExerciseModel(ExerciseRecordEntity entity) {
        ExerciseData data = new ExerciseData();
        data.setId(entity.id);
        data.setUserId(entity.userId);
        data.setExerciseType(entity.exerciseType);
        data.setLocation(entity.location);
        data.setDuration(entity.duration);
        data.setCreatedAt(entity.createdAt);
        data.setUpdatedAt(entity.updatedAt);
        return data;
    }

    private FitnessPlan toFitnessPlanModel(FitnessPlanEntity entity) {
        FitnessPlan plan = new FitnessPlan();
        plan.setId(entity.id);
        plan.setUserId(entity.userId);
        plan.setGoal(entity.goal);
        plan.setPlanContent(entity.planContent);
        plan.setCreatedAt(entity.createdAt);
        plan.setUpdatedAt(entity.updatedAt);
        return plan;
    }
}
