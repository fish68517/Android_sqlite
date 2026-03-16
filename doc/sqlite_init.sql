-- 个人健康助手本地 SQLite 初始化脚本
-- 适用于 Android Studio / sqlite3 导入

PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS diaries;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    phone TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email TEXT,
    height REAL,
    weight REAL,
    avatar TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE TABLE diaries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    mood INTEGER NOT NULL DEFAULT 2,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (username, phone, password, email, height, weight, avatar, created_at, updated_at)
VALUES
('演示用户', '13800000000', '123456', 'demo@health.local', 170.0, 65.0, NULL, 1742083200000, 1742083200000),
('张小明', '13900000001', '123456', 'xiaoming@health.local', 176.0, 72.5, NULL, 1742083200000, 1742083200000),
('李小红', '13900000002', '123456', 'xiaohong@health.local', 162.0, 54.0, NULL, 1742083200000, 1742083200000);

INSERT INTO diaries (user_id, content, mood, created_at, updated_at)
VALUES
(1, '今天完成了30分钟快走，喝水8杯，状态很好。', 3, '2026-03-14T08:30:00', '2026-03-14T08:30:00'),
(1, '中午有点疲惫，午休后做了拉伸，感觉恢复了。', 2, '2026-03-15T13:20:00', '2026-03-15T13:20:00'),
(2, '晚饭后散步40分钟，睡前记录体重。', 3, '2026-03-13T21:10:00', '2026-03-13T21:10:00'),
(3, '今天有些感冒症状，早点休息并多喝温水。', 1, '2026-03-12T22:00:00', '2026-03-12T22:00:00');
