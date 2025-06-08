-- 用户表
CREATE TABLE `users` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `username` TEXT NOT NULL UNIQUE,
  `password` TEXT NOT NULL
);

-- 笔记分类表
CREATE TABLE `categories` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `name` TEXT NOT NULL,
  `user_id` INTEGER,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

-- 笔记表
CREATE TABLE `notes` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `title` TEXT NOT NULL,
  `content` TEXT,
  `image` BLOB, -- 用于存储图片数据
  `category_id` INTEGER,
  `user_id` INTEGER,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

-- 回收站表 (用于存储删除的笔记)
CREATE TABLE `deleted_notes` (
    `id` INTEGER PRIMARY KEY,
    `title` TEXT NOT NULL,
    `content` TEXT,
    `image` BLOB,
    `category_id` INTEGER,
    `user_id` INTEGER,
    `created_at` DATETIME,
    `updated_at` DATETIME,
    `deleted_at` DATETIME DEFAULT CURRENT_TIMESTAMP
); 