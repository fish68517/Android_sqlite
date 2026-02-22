package com.example.healthdietapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper - Manages SQLite database creation and upgrades
 * Handles all database table creation and schema management
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "health_diet_app.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all database tables
        createUsersTables(db);
        createRecipeTables(db);
        createCommunityTables(db);
        createUtilityTables(db);

        // Insert simplified Chinese mock data (only runs when DB is first created)
        insertMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database version upgrades
        // For now, drop all tables and recreate them
        dropAllTables(db);
        onCreate(db);
    }

    private void createUsersTables(SQLiteDatabase db) {
        // Users table
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT PRIMARY KEY," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "nickname TEXT," +
                "avatar TEXT," +
                "created_at INTEGER," +
                "updated_at INTEGER)");

        // User preferences table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_preferences (" +
                "preference_id TEXT PRIMARY KEY," +
                "user_id TEXT UNIQUE NOT NULL," +
                "taste_tendency TEXT," +
                "diet_type TEXT," +
                "health_goal TEXT," +
                "restrictions TEXT," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");
    }

    private void createRecipeTables(SQLiteDatabase db) {
        // Recipes table
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                "recipe_id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "ingredients TEXT," +
                "instructions TEXT," +
                "nutrition_info TEXT," +
                "category TEXT," +
                "image_url TEXT," +
                "created_by TEXT," +
                "created_at INTEGER," +
                "updated_at INTEGER)");

        // User recipes table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_recipes (" +
                "user_recipe_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "recipe_id TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "meal_type TEXT NOT NULL," +
                "added_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)," +
                "UNIQUE(user_id, date, meal_type))");

        // Health records table
        db.execSQL("CREATE TABLE IF NOT EXISTS health_records (" +
                "record_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "weight REAL," +
                "water_intake REAL," +
                "measurements TEXT," +
                "recorded_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "UNIQUE(user_id, date))");

        // Recipe categories table
        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_categories (" +
                "category_id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "parent_category_id TEXT," +
                "icon TEXT," +
                "FOREIGN KEY (parent_category_id) REFERENCES recipe_categories(category_id))");
    }

    private void createCommunityTables(SQLiteDatabase db) {
        // Posts table
        db.execSQL("CREATE TABLE IF NOT EXISTS posts (" +
                "post_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT," +
                "images TEXT," +
                "tags TEXT," +
                "likes INTEGER DEFAULT 0," +
                "comments INTEGER DEFAULT 0," +
                "created_at INTEGER," +
                "updated_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");

        // Post likes table
        db.execSQL("CREATE TABLE IF NOT EXISTS post_likes (" +
                "like_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "post_id TEXT NOT NULL," +
                "liked_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (post_id) REFERENCES posts(post_id)," +
                "UNIQUE(user_id, post_id))");

        // Post collections table
        db.execSQL("CREATE TABLE IF NOT EXISTS post_collections (" +
                "collection_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "post_id TEXT NOT NULL," +
                "collected_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (post_id) REFERENCES posts(post_id)," +
                "UNIQUE(user_id, post_id))");

        // User follows table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_follows (" +
                "follow_id TEXT PRIMARY KEY," +
                "follower_id TEXT NOT NULL," +
                "followee_id TEXT NOT NULL," +
                "followed_at INTEGER," +
                "FOREIGN KEY (follower_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (followee_id) REFERENCES users(user_id)," +
                "UNIQUE(follower_id, followee_id))");

        // Health questions table
        db.execSQL("CREATE TABLE IF NOT EXISTS health_questions (" +
                "question_id TEXT PRIMARY KEY," +
                "question TEXT NOT NULL," +
                "answer TEXT," +
                "category TEXT," +
                "created_at INTEGER)");
    }

    private void createUtilityTables(SQLiteDatabase db) {
        // Search history table
        db.execSQL("CREATE TABLE IF NOT EXISTS search_history (" +
                "history_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "keyword TEXT NOT NULL," +
                "searched_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");

        // Feedbacks table
        db.execSQL("CREATE TABLE IF NOT EXISTS feedbacks (" +
                "feedback_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "created_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");
    }


    /**
     * 插入简体中文模拟数据（每张表 5-6 条）。
     * 注意：该方法仅在数据库首次创建（onCreate）时调用一次。
     */
    private void insertMockData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // 开启外键约束（Android SQLite 默认可能关闭）
            db.execSQL("PRAGMA foreign_keys=ON;");

            String[] sqlList = new String[] {
                    "INSERT INTO users (user_id, username, password, nickname, avatar, created_at, updated_at) VALUES ('U001', 'zhangsan', '123456', '张三', 'avatar_zhang.png', 1769127890, 1771633490);",
                    "INSERT INTO users (user_id, username, password, nickname, avatar, created_at, updated_at) VALUES ('U002', 'lisi', '123456', '李四', 'avatar_li.png', 1769300690, 1771547090);",
                    "INSERT INTO users (user_id, username, password, nickname, avatar, created_at, updated_at) VALUES ('U003', 'wangwu', '123456', '王五', 'avatar_wang.png', 1769991890, 1771460690);",
                    "INSERT INTO users (user_id, username, password, nickname, avatar, created_at, updated_at) VALUES ('U004', 'zhaoliu', '123456', '赵六', 'avatar_zhao.png', 1770164690, 1771374290);",
                    "INSERT INTO users (user_id, username, password, nickname, avatar, created_at, updated_at) VALUES ('U005', 'chenqi', '123456', '陈七', 'avatar_chen.png', 1770855890, 1771633490);",
                    "INSERT INTO users (user_id, username, password, nickname, avatar, created_at, updated_at) VALUES ('U006', 'sunba', '123456', '孙八', 'avatar_sun.png', 1771287890, 1771719890);",
                    "INSERT INTO user_preferences (preference_id, user_id, taste_tendency, diet_type, health_goal, restrictions) VALUES ('P001', 'U001', '偏清淡', '低脂', '减脂', '不吃辣;不喝含糖饮料');",
                    "INSERT INTO user_preferences (preference_id, user_id, taste_tendency, diet_type, health_goal, restrictions) VALUES ('P002', 'U002', '偏重口', '均衡', '增肌', '乳糖不耐受');",
                    "INSERT INTO user_preferences (preference_id, user_id, taste_tendency, diet_type, health_goal, restrictions) VALUES ('P003', 'U003', '偏清淡', '高蛋白', '控糖', '花生过敏');",
                    "INSERT INTO user_preferences (preference_id, user_id, taste_tendency, diet_type, health_goal, restrictions) VALUES ('P004', 'U004', '偏重口', '低碳', '塑形', '不吃海鲜');",
                    "INSERT INTO user_preferences (preference_id, user_id, taste_tendency, diet_type, health_goal, restrictions) VALUES ('P005', 'U005', '偏清淡', '素食', '健康管理', '无');",
                    "INSERT INTO user_preferences (preference_id, user_id, taste_tendency, diet_type, health_goal, restrictions) VALUES ('P006', 'U006', '偏重口', '地中海', '减脂', '不吃内脏');",
                    "INSERT INTO recipe_categories (category_id, name, parent_category_id, icon) VALUES ('C002', '主食', NULL, 'icon_staple.png');",
                    "INSERT INTO recipe_categories (category_id, name, parent_category_id, icon) VALUES ('C003', '轻食', NULL, 'icon_salad.png');",
                    "INSERT INTO recipe_categories (category_id, name, parent_category_id, icon) VALUES ('C004', '家常菜', NULL, 'icon_home.png');",
                    "INSERT INTO recipe_categories (category_id, name, parent_category_id, icon) VALUES ('C005', '汤羹', NULL, 'icon_soup.png');",
                    "INSERT INTO recipe_categories (category_id, name, parent_category_id, icon) VALUES ('C006', '能量碗', NULL, 'icon_bowl.png');",
                    "INSERT INTO recipes (recipe_id, name, description, ingredients, instructions, nutrition_info, category, image_url, created_by, created_at, updated_at) VALUES ('R001', '番茄鸡胸肉意面', '高蛋白低脂，适合减脂期', '鸡胸肉,全麦意面,番茄,洋葱,橄榄油', '1.鸡胸切丁煎熟 2.番茄炒出汁 3.煮意面拌匀', '热量:450kcal;蛋白质:38g;脂肪:10g;碳水:52g', '主食', 'img_r001.jpg', 'U001', 1769559890, 1771287890);",
                    "INSERT INTO recipes (recipe_id, name, description, ingredients, instructions, nutrition_info, category, image_url, created_by, created_at, updated_at) VALUES ('R002', '清炒西兰花虾仁', '清淡鲜香，补充优质蛋白', '西兰花,虾仁,蒜,盐,黑胡椒', '1.焯西兰花 2.虾仁滑炒 3.合炒调味', '热量:220kcal;蛋白质:24g;脂肪:8g;碳水:12g', '家常菜', 'img_r002.jpg', 'U002', 1769819090, 1771201490);",
                    "INSERT INTO recipes (recipe_id, name, description, ingredients, instructions, nutrition_info, category, image_url, created_by, created_at, updated_at) VALUES ('R003', '燕麦酸奶水果杯', '早餐快手，饱腹感强', '燕麦,无糖酸奶,香蕉,蓝莓,坚果', '1.杯底铺燕麦 2.加酸奶 3.放水果', '热量:310kcal;蛋白质:16g;脂肪:9g;碳水:42g', '早餐', 'img_r003.jpg', 'U003', 1770423890, 1771547090);",
                    "INSERT INTO recipes (recipe_id, name, description, ingredients, instructions, nutrition_info, category, image_url, created_by, created_at, updated_at) VALUES ('R004', '牛油果鸡蛋沙拉', '低碳高脂，适合控糖', '牛油果,鸡蛋,生菜,柠檬汁', '1.鸡蛋煮熟切块 2.牛油果拌匀 3.加柠檬汁', '热量:380kcal;蛋白质:18g;脂肪:28g;碳水:14g', '轻食', 'img_r004.jpg', 'U004', 1770683090, 1771633490);",
                    "INSERT INTO recipes (recipe_id, name, description, ingredients, instructions, nutrition_info, category, image_url, created_by, created_at, updated_at) VALUES ('R005', '菌菇豆腐汤', '低脂暖胃，适合晚餐', '豆腐,香菇,金针菇,小葱', '1.菌菇煮开 2.下豆腐 3.少盐调味', '热量:160kcal;蛋白质:14g;脂肪:6g;碳水:10g', '汤羹', 'img_r005.jpg', 'U005', 1770942290, 1771633490);",
                    "INSERT INTO recipes (recipe_id, name, description, ingredients, instructions, nutrition_info, category, image_url, created_by, created_at, updated_at) VALUES ('R006', '黑椒牛肉蔬菜碗', '高蛋白能量餐，适合增肌', '牛里脊,彩椒,洋葱,糙米,黑胡椒', '1.牛肉快炒 2.蔬菜翻炒 3.铺糙米装碗', '热量:520kcal;蛋白质:40g;脂肪:14g;碳水:58g', '能量碗', 'img_r006.jpg', 'U002', 1771115090, 1771719890);",
                    "INSERT INTO user_recipes (user_recipe_id, user_id, recipe_id, date, meal_type, added_at) VALUES ('UR001', 'U001', 'R003', '2026-02-18', '早餐', 1771374290);",
                    "INSERT INTO user_recipes (user_recipe_id, user_id, recipe_id, date, meal_type, added_at) VALUES ('UR002', 'U001', 'R002', '2026-02-19', '午餐', 1771460690);",
                    "INSERT INTO user_recipes (user_recipe_id, user_id, recipe_id, date, meal_type, added_at) VALUES ('UR003', 'U002', 'R006', '2026-02-19', '晚餐', 1771460690);",
                    "INSERT INTO user_recipes (user_recipe_id, user_id, recipe_id, date, meal_type, added_at) VALUES ('UR004', 'U003', 'R001', '2026-02-20', '午餐', 1771547090);",
                    "INSERT INTO user_recipes (user_recipe_id, user_id, recipe_id, date, meal_type, added_at) VALUES ('UR005', 'U004', 'R004', '2026-02-21', '晚餐', 1771633490);",
                    "INSERT INTO user_recipes (user_recipe_id, user_id, recipe_id, date, meal_type, added_at) VALUES ('UR006', 'U005', 'R005', '2026-02-21', '晚餐', 1771633490);",
                    "INSERT INTO health_records (record_id, user_id, date, weight, water_intake, measurements, recorded_at) VALUES ('HR001', 'U001', '2026-02-18', 68.5, 1.8, '{\"waist\":78,\"hip\":94}', 1771374290);",
                    "INSERT INTO health_records (record_id, user_id, date, weight, water_intake, measurements, recorded_at) VALUES ('HR002', 'U001', '2026-02-21', 67.9, 2.2, '{\"waist\":77,\"hip\":93}', 1771633490);",
                    "INSERT INTO health_records (record_id, user_id, date, weight, water_intake, measurements, recorded_at) VALUES ('HR003', 'U002', '2026-02-19', 75.2, 1.5, '{\"waist\":84,\"hip\":98}', 1771460690);",
                    "INSERT INTO health_records (record_id, user_id, date, weight, water_intake, measurements, recorded_at) VALUES ('HR004', 'U003', '2026-02-20', 62.0, 2.0, '{\"waist\":70,\"hip\":90}', 1771547090);",
                    "INSERT INTO health_records (record_id, user_id, date, weight, water_intake, measurements, recorded_at) VALUES ('HR005', 'U004', '2026-02-21', 80.3, 1.6, '{\"waist\":90,\"hip\":102}', 1771633490);",
                    "INSERT INTO health_records (record_id, user_id, date, weight, water_intake, measurements, recorded_at) VALUES ('HR006', 'U005', '2026-02-21', 55.8, 2.3, '{\"waist\":66,\"hip\":88}', 1771633490);",
                    "INSERT INTO posts (post_id, user_id, title, content, images, tags, likes, comments, created_at, updated_at) VALUES ('POST001', 'U001', '一周减脂打卡分享', '这周坚持每天走一万步，体重下降了0.6kg，饮食以清淡为主。', 'img_post1_a.jpg,img_post1_b.jpg', '减脂,打卡,运动', 12, 3, 1771287890, 1771287890);",
                    "INSERT INTO posts (post_id, user_id, title, content, images, tags, likes, comments, created_at, updated_at) VALUES ('POST002', 'U002', '增肌期怎么吃更舒服？', '我把主食换成糙米和土豆，训练后补充乳清，感觉恢复更快。', 'img_post2.jpg', '增肌,饮食,训练', 8, 2, 1771374290, 1771287890);",
                    "INSERT INTO posts (post_id, user_id, title, content, images, tags, likes, comments, created_at, updated_at) VALUES ('POST003', 'U003', '控糖早餐推荐', '燕麦+无糖酸奶+蓝莓真的很顶，饱腹又不容易犯困。', 'img_post3.jpg', '控糖,早餐,燕麦', 15, 5, 1771460690, 1771287890);",
                    "INSERT INTO posts (post_id, user_id, title, content, images, tags, likes, comments, created_at, updated_at) VALUES ('POST004', 'U004', '低碳也能好吃', '牛油果鸡蛋沙拉加一点柠檬汁，口感很清爽。', 'img_post4.jpg', '低碳,轻食', 6, 1, 1771547090, 1771287890);",
                    "INSERT INTO posts (post_id, user_id, title, content, images, tags, likes, comments, created_at, updated_at) VALUES ('POST005', 'U005', '素食晚餐记录', '今晚做了菌菇豆腐汤，简单但很满足，睡前也不饿。', 'img_post5.jpg', '素食,晚餐,汤', 9, 0, 1771633490, 1771287890);",
                    "INSERT INTO posts (post_id, user_id, title, content, images, tags, likes, comments, created_at, updated_at) VALUES ('POST006', 'U006', '喝水真的有用吗？', '我这两天把喝水量提高到2L以上，皮肤状态确实变好一些。', 'img_post6.jpg', '喝水,习惯', 4, 0, 1771719890, 1771287890);",
                    "INSERT INTO post_likes (like_id, user_id, post_id, liked_at) VALUES ('L001', 'U002', 'POST001', 1771374290);",
                    "INSERT INTO post_likes (like_id, user_id, post_id, liked_at) VALUES ('L002', 'U003', 'POST001', 1771374290);",
                    "INSERT INTO post_likes (like_id, user_id, post_id, liked_at) VALUES ('L003', 'U001', 'POST003', 1771547090);",
                    "INSERT INTO post_likes (like_id, user_id, post_id, liked_at) VALUES ('L004', 'U004', 'POST003', 1771547090);",
                    "INSERT INTO post_likes (like_id, user_id, post_id, liked_at) VALUES ('L005', 'U005', 'POST002', 1771460690);",
                    "INSERT INTO post_likes (like_id, user_id, post_id, liked_at) VALUES ('L006', 'U006', 'POST005', 1771719890);",
                    "INSERT INTO post_collections (collection_id, user_id, post_id, collected_at) VALUES ('CL001', 'U001', 'POST003', 1771547090);",
                    "INSERT INTO post_collections (collection_id, user_id, post_id, collected_at) VALUES ('CL002', 'U002', 'POST001', 1771374290);",
                    "INSERT INTO post_collections (collection_id, user_id, post_id, collected_at) VALUES ('CL003', 'U003', 'POST004', 1771547090);",
                    "INSERT INTO post_collections (collection_id, user_id, post_id, collected_at) VALUES ('CL004', 'U004', 'POST002', 1771460690);",
                    "INSERT INTO post_collections (collection_id, user_id, post_id, collected_at) VALUES ('CL005', 'U005', 'POST001', 1771633490);",
                    "INSERT INTO post_collections (collection_id, user_id, post_id, collected_at) VALUES ('CL006', 'U006', 'POST003', 1771719890);",
                    "INSERT INTO user_follows (follow_id, follower_id, followee_id, followed_at) VALUES ('F001', 'U001', 'U002', 1769991890);",
                    "INSERT INTO user_follows (follow_id, follower_id, followee_id, followed_at) VALUES ('F002', 'U001', 'U003', 1770164690);",
                    "INSERT INTO user_follows (follow_id, follower_id, followee_id, followed_at) VALUES ('F003', 'U002', 'U001', 1770423890);",
                    "INSERT INTO user_follows (follow_id, follower_id, followee_id, followed_at) VALUES ('F004', 'U003', 'U005', 1770855890);",
                    "INSERT INTO user_follows (follow_id, follower_id, followee_id, followed_at) VALUES ('F005', 'U004', 'U001', 1771028690);",
                    "INSERT INTO user_follows (follow_id, follower_id, followee_id, followed_at) VALUES ('F006', 'U006', 'U003', 1771547090);",
                    "INSERT INTO health_questions (question_id, question, answer, category, created_at) VALUES ('Q001', '减脂期一天吃几顿比较好？', '建议根据个人作息与饥饿感安排，一般 3 餐为主，必要时加 1 次健康加餐，关键在于总热量控制。', '减脂', 1768263890);",
                    "INSERT INTO health_questions (question_id, question, answer, category, created_at) VALUES ('Q002', '为什么我运动后体重反而上升？', '运动后可能出现肌肉糖原补充与水分潴留，短期波动正常，建议观察 1-2 周趋势。', '运动', 1768695890);",
                    "INSERT INTO health_questions (question_id, question, answer, category, created_at) VALUES ('Q003', '喝水量应该怎么估算？', '可按体重估算，每公斤体重约 30-35ml，并结合运动量和气温调整。', '习惯', 1769127890);",
                    "INSERT INTO health_questions (question_id, question, answer, category, created_at) VALUES ('Q004', '控糖饮食是不是完全不能吃主食？', '不必完全不吃，可选择低 GI 主食并控制份量，如糙米、燕麦、红薯等。', '控糖', 1769559890);",
                    "INSERT INTO health_questions (question_id, question, answer, category, created_at) VALUES ('Q005', '增肌期蛋白质要吃多少？', '一般建议每公斤体重 1.6-2.2g 蛋白质，分散到多餐摄入更利于合成。', '增肌', 1769991890);",
                    "INSERT INTO health_questions (question_id, question, answer, category, created_at) VALUES ('Q006', '晚餐太晚会影响减脂吗？', '更重要的是全天总摄入与睡眠质量。若晚餐较晚，建议清淡、适量，并避免高糖高脂。', '作息', 1770423890);",
                    "INSERT INTO search_history (history_id, user_id, keyword, searched_at) VALUES ('H001', 'U001', '低脂早餐', 1771115090);",
                    "INSERT INTO search_history (history_id, user_id, keyword, searched_at) VALUES ('H002', 'U001', '番茄鸡胸肉', 1771201490);",
                    "INSERT INTO search_history (history_id, user_id, keyword, searched_at) VALUES ('H003', 'U002', '增肌食谱', 1771287890);",
                    "INSERT INTO search_history (history_id, user_id, keyword, searched_at) VALUES ('H004', 'U003', '控糖', 1771374290);",
                    "INSERT INTO search_history (history_id, user_id, keyword, searched_at) VALUES ('H005', 'U004', '低碳沙拉', 1771460690);",
                    "INSERT INTO search_history (history_id, user_id, keyword, searched_at) VALUES ('H006', 'U005', '素食汤', 1771547090);",
                    "INSERT INTO feedbacks (feedback_id, user_id, content, created_at) VALUES ('FB001', 'U001', '希望首页能显示今日摄入热量和剩余目标，更直观。', 1771201490);",
                    "INSERT INTO feedbacks (feedback_id, user_id, content, created_at) VALUES ('FB002', 'U002', '食谱筛选能否增加“高蛋白/低碳”标签组合？', 1771287890);",
                    "INSERT INTO feedbacks (feedback_id, user_id, content, created_at) VALUES ('FB003', 'U003', '社区帖子加载有时会卡顿，建议加下拉刷新。', 1771374290);",
                    "INSERT INTO feedbacks (feedback_id, user_id, content, created_at) VALUES ('FB004', 'U004', '想要更多适合乳糖不耐受的早餐方案。', 1771460690);",
                    "INSERT INTO feedbacks (feedback_id, user_id, content, created_at) VALUES ('FB005', 'U005', '可以增加素食专区和推荐列表吗？', 1771547090);",
                    "INSERT INTO feedbacks (feedback_id, user_id, content, created_at) VALUES ('FB006', 'U006', '夜间模式下部分文字对比度偏低。', 1771633490);"
            };

            for (String sql : sqlList) {
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS feedbacks");
        db.execSQL("DROP TABLE IF EXISTS search_history");
        db.execSQL("DROP TABLE IF EXISTS health_questions");
        db.execSQL("DROP TABLE IF EXISTS user_follows");
        db.execSQL("DROP TABLE IF EXISTS post_collections");
        db.execSQL("DROP TABLE IF EXISTS post_likes");
        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS recipe_categories");
        db.execSQL("DROP TABLE IF EXISTS health_records");
        db.execSQL("DROP TABLE IF EXISTS user_recipes");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS user_preferences");
        db.execSQL("DROP TABLE IF EXISTS users");
    }
}
