# 健康饮食管理应用 - 设计文档

## 概述

本应用采用 Android 原生开发，使用 Java 语言和 SQLite 数据库。应用遵循 Material Design 3 设计规范，采用淡绿色（#4CAF50）和粉红色（#E91E63）作为主题色。整体架构采用 MVVM 模式，分离业务逻辑和 UI 层，便于维护和测试。

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Activities/Fragments)       │
│  (LoginActivity, HomeFragment, CategoryFragment, etc.)  │
│  - 直接实现业务逻辑                                      │
│  - 直接调用数据库操作                                    │
└─────────────────────────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│         Database Layer (SQLite + SQLiteOpenHelper)      │
│  (DatabaseHelper, 直接 SQL 操作)                        │
└─────────────────────────────────────────────────────────┘
```

### 技术栈

- **开发语言**：Java
- **UI 框架**：Android Framework + Material Design 3
- **数据库**：SQLite（使用 SQLiteOpenHelper 直接操作）
- **架构模式**：简单的 Activity/Fragment + 数据库直接调用
- **异步处理**：Thread/Handler（简单异步操作）

## 组件和接口

### 1. 用户系统模块

#### 核心类

- **User**：用户实体类
  - userId: String（主键）
  - username: String
  - password: String（加密存储）
  - nickname: String
  - avatar: String（头像 URL）
  - createdAt: Long
  - updatedAt: Long

- **UserPreferences**：用户偏好设置
  - userId: String（外键）
  - tasteTendency: String（口味倾向）
  - dietType: String（饮食类型）
  - healthGoal: String（健身目标）
  - restrictions: String（饮食限制，逗号分隔）

- **DatabaseHelper**：数据库操作类
  - 提供所有数据库操作方法（增删改查）
  - 管理数据库连接和事务

#### UI 组件

- **LoginActivity**：登录页面
  - 用户名输入框
  - 密码输入框
  - 登录按钮
  - 注册链接
  - 直接调用数据库验证用户信息

- **RegisterActivity**：注册页面
  - 用户名输入框
  - 密码输入框
  - 确认密码输入框
  - 注册按钮
  - 直接调用数据库保存用户信息

- **PreferencesSetupActivity**：偏好设置页面
  - 口味倾向选择器
  - 饮食类型选择器
  - 健身目标选择器
  - 保存按钮
  - 直接调用数据库保存偏好信息

### 2. 首页模块

#### 核心类

- **Recipe**：食谱实体类
  - recipeId: String（主键）
  - name: String
  - description: String
  - ingredients: String（JSON 格式）
  - instructions: String
  - nutritionInfo: String（JSON 格式）
  - category: String
  - imageUrl: String
  - createdBy: String（创建者 ID）
  - createdAt: Long

- **UserRecipe**：用户食谱安排
  - userRecipeId: String（主键）
  - userId: String（外键）
  - recipeId: String（外键）
  - date: String（YYYY-MM-DD 格式）
  - mealType: String（早/中/晚）
  - addedAt: Long

- **HealthRecord**：健康数据记录
  - recordId: String（主键）
  - userId: String（外键）
  - date: String（YYYY-MM-DD 格式）
  - weight: Float
  - waterIntake: Float
  - measurements: String（JSON 格式）
  - recordedAt: Long

#### UI 组件

- **HomeFragment**：首页
  - 日期导航栏（左右滑动、日期选择）
  - 我的食谱卡片（早/中/晚）
  - 推荐食谱列表
  - 每日记录入口
  - 直接调用数据库获取数据

- **RecipeDetailActivity**：食谱详情页
  - 食谱图片
  - 食谱名称和描述
  - 食材列表
  - 做法步骤
  - 营养信息
  - 加入食谱按钮

- **AddRecipeActivity**：添加食谱页
  - 日期选择器
  - 餐次选择（早/中/晚）
  - 冲突处理对话框（替换/加餐/取消）
  - 直接调用数据库检查冲突和保存数据

- **HealthRecordActivity**：健康记录页
  - 体重输入框
  - 饮水量输入框
  - 身体尺寸输入框
  - 保存按钮
  - 直接调用数据库保存记录

### 3. 分类模块

#### 核心类

- **RecipeCategory**：食谱分类
  - categoryId: String（主键）
  - name: String
  - parentCategoryId: String（父分类 ID，为 null 表示主分类）
  - icon: String

- **SearchHistory**：搜索历史
  - historyId: String（主键）
  - userId: String（外键）
  - keyword: String
  - searchedAt: Long

#### UI 组件

- **CategoryFragment**：分类页
  - 搜索栏
  - 左侧主分类列表（竖向滑动）
  - 右侧子分类和食谱列表
  - 直接调用数据库获取分类和食谱数据

- **SearchResultActivity**：搜索结果页
  - 搜索栏
  - 搜索历史列表
  - 搜索结果列表
  - 清空历史按钮
  - 直接调用数据库查询和管理搜索历史

### 4. 发现模块

#### 核心类

- **Post**：社区帖子
  - postId: String（主键）
  - userId: String（外键）
  - title: String
  - content: String
  - images: String（JSON 格式，图片 URL 列表）
  - tags: String（逗号分隔）
  - likes: Int
  - comments: Int
  - createdAt: Long
  - updatedAt: Long

- **PostLike**：帖子点赞记录
  - likeId: String（主键）
  - userId: String（外键）
  - postId: String（外键）
  - likedAt: Long

- **PostCollection**：帖子收藏记录
  - collectionId: String（主键）
  - userId: String（外键）
  - postId: String（外键）
  - collectedAt: Long

- **UserFollow**：用户关注记录
  - followId: String（主键）
  - followerId: String（外键，关注者）
  - followeeId: String（外键，被关注者）
  - followedAt: Long

- **HealthQuestion**：每日健康问答
  - questionId: String（主键）
  - question: String
  - answer: String
  - category: String
  - createdAt: Long

#### UI 组件

- **DiscoverFragment**：发现页
  - 帖子搜索栏
  - 功能工具卡片（食物榜单、今天吃什么、食物估重、每日问答）
  - 推荐帖子列表
  - 发布按钮
  - 直接调用数据库获取推荐帖子

- **PostDetailActivity**：帖子详情页
  - 帖子内容（图片、文字）
  - 作者信息
  - 点赞、收藏、关注按钮
  - 直接调用数据库更新点赞、收藏、关注状态

- **CreatePostActivity**：发布帖子页
  - 图片上传区域
  - 文字输入框
  - 标签输入框
  - 发布按钮
  - 直接调用数据库保存帖子

- **ToolsActivity**：工具页面
  - 食物榜单
  - 今天吃什么（随机推荐）
  - 食物估重参考表
  - 每日健康问答

### 5. 我的模块

#### 核心类

- **UserProfile**：用户个人资料
  - userId: String
  - nickname: String
  - avatar: String
  - bio: String
  - postCount: Int
  - followerCount: Int
  - followingCount: Int

- **Feedback**：用户反馈
  - feedbackId: String（主键）
  - userId: String（外键）
  - content: String
  - createdAt: Long

#### UI 组件

- **ProfileFragment**：我的页面
  - 用户头像和昵称
  - 功能菜单（我的发布、我的关注、我的收藏、账号管理、意见反馈、联系我们）
  - 直接调用数据库获取用户信息

- **MyPostsActivity**：我的发布页
  - 用户发布的帖子列表
  - 删除功能
  - 直接调用数据库获取和删除帖子

- **MyFollowingActivity**：我的关注页
  - 已关注的作者列表
  - 取消关注功能
  - 直接调用数据库管理关注关系

- **MyCollectionsActivity**：我的收藏页
  - 标签页（食谱、帖子、问题）
  - 收藏内容列表
  - 直接调用数据库获取收藏内容

- **AccountManagementActivity**：账号管理页
  - 修改密码
  - 账号注销
  - 直接调用数据库更新账号信息

- **FeedbackActivity**：意见反馈页
  - 文本输入框
  - 提交按钮
  - 直接调用数据库保存反馈

- **ContactUsActivity**：联系我们页
  - 官方邮箱
  - 官方电话
  - 社交媒体链接

## 数据模型

### 数据库表结构

```sql
-- 用户表
CREATE TABLE users (
    user_id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    nickname TEXT,
    avatar TEXT,
    created_at INTEGER,
    updated_at INTEGER
);

-- 用户偏好表
CREATE TABLE user_preferences (
    preference_id TEXT PRIMARY KEY,
    user_id TEXT UNIQUE NOT NULL,
    taste_tendency TEXT,
    diet_type TEXT,
    health_goal TEXT,
    restrictions TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 食谱表
CREATE TABLE recipes (
    recipe_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    ingredients TEXT,
    instructions TEXT,
    nutrition_info TEXT,
    category TEXT,
    image_url TEXT,
    created_by TEXT,
    created_at INTEGER,
    updated_at INTEGER
);

-- 用户食谱安排表
CREATE TABLE user_recipes (
    user_recipe_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    recipe_id TEXT NOT NULL,
    date TEXT NOT NULL,
    meal_type TEXT NOT NULL,
    added_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id),
    UNIQUE(user_id, date, meal_type)
);

-- 健康记录表
CREATE TABLE health_records (
    record_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    date TEXT NOT NULL,
    weight REAL,
    water_intake REAL,
    measurements TEXT,
    recorded_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE(user_id, date)
);

-- 食谱分类表
CREATE TABLE recipe_categories (
    category_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    parent_category_id TEXT,
    icon TEXT,
    FOREIGN KEY (parent_category_id) REFERENCES recipe_categories(category_id)
);

-- 搜索历史表
CREATE TABLE search_history (
    history_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    keyword TEXT NOT NULL,
    searched_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 社区帖子表
CREATE TABLE posts (
    post_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    content TEXT,
    images TEXT,
    tags TEXT,
    likes INTEGER DEFAULT 0,
    comments INTEGER DEFAULT 0,
    created_at INTEGER,
    updated_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 帖子点赞表
CREATE TABLE post_likes (
    like_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    post_id TEXT NOT NULL,
    liked_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    UNIQUE(user_id, post_id)
);

-- 帖子收藏表
CREATE TABLE post_collections (
    collection_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    post_id TEXT NOT NULL,
    collected_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    UNIQUE(user_id, post_id)
);

-- 用户关注表
CREATE TABLE user_follows (
    follow_id TEXT PRIMARY KEY,
    follower_id TEXT NOT NULL,
    followee_id TEXT NOT NULL,
    followed_at INTEGER,
    FOREIGN KEY (follower_id) REFERENCES users(user_id),
    FOREIGN KEY (followee_id) REFERENCES users(user_id),
    UNIQUE(follower_id, followee_id)
);

-- 每日健康问答表
CREATE TABLE health_questions (
    question_id TEXT PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT,
    category TEXT,
    created_at INTEGER
);

-- 用户反馈表
CREATE TABLE feedbacks (
    feedback_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

## 错误处理

### 异常类型

- **AuthenticationException**：认证失败（登录、注册错误）
- **ValidationException**：数据验证失败
- **DatabaseException**：数据库操作失败
- **NetworkException**：网络操作失败（预留）
- **ConflictException**：数据冲突（如食谱时间冲突）

### 错误处理策略

1. **用户输入验证**：在提交前验证所有用户输入
2. **数据库事务**：使用事务确保数据一致性
3. **用户提示**：为用户显示友好的错误提示
4. **日志记录**：记录所有错误信息便于调试

## 测试策略

### 功能测试

- 测试用户登录和注册流程
- 测试食谱添加和冲突处理
- 测试帖子发布和互动功能
- 测试数据的保存和读取

### 界面测试

- 测试页面导航和切换
- 测试用户交互（点击、输入、滑动）
- 测试数据显示的正确性
- 测试 Material Design 样式的一致性

## UI 设计规范

### 色彩方案

- **主色**：淡绿色 (#4CAF50)
- **辅助色**：粉红色 (#E91E63)
- **背景色**：白色 (#FFFFFF)
- **文字色**：深灰色 (#212121)
- **边框色**：浅灰色 (#BDBDBD)

### 字体

- **标题**：Roboto Bold, 24sp
- **副标题**：Roboto Medium, 18sp
- **正文**：Roboto Regular, 14sp
- **小文本**：Roboto Regular, 12sp

### 组件规范

- 按钮：圆角 4dp，高度 48dp
- 输入框：圆角 4dp，高度 56dp
- 卡片：圆角 8dp，阴影 2dp
- 列表项：高度 56dp-72dp

### 布局

- 屏幕边距：16dp
- 组件间距：8dp-16dp
- 底部导航栏：5 个标签页（首页、分类、发现、我的、更多）

## 页面流程图

```
登录/注册 → 偏好设置 → 首页
                    ├→ 分类
                    ├→ 发现
                    ├→ 我的
                    └→ 详情页面
```

## 实现优先级

1. **第一阶段**：用户系统、首页基础功能
2. **第二阶段**：分类、食谱管理
3. **第三阶段**：发现、社区功能
4. **第四阶段**：个人中心、高级功能
5. **第五阶段**：优化、测试、发布
