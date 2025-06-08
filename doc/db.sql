-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management;

-- 创建管理员表
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    remember_password TINYINT(1) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建图书分类表
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200)
);

-- 创建图书表
CREATE TABLE book (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    cover_image VARCHAR(200),
    description TEXT,
    publish_date DATE,
    category_id INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 插入管理员数据
INSERT INTO admin (username, password, remember_password) VALUES
('admin', '123456', 0);

-- 插入图书分类数据
INSERT INTO category (name, description) VALUES
('计算机', '计算机相关书籍'),
('文学', '文学类书籍'),
('历史', '历史类书籍'),
('科学', '科学类书籍');

-- 插入图书数据
INSERT INTO book (title, author, isbn, cover_image, description, publish_date, category_id) VALUES
('Java编程思想', 'Bruce Eckel', '9787111213826', 'java.jpg', 'Java编程经典著作', '2007-06-01', 1),
('红楼梦', '曹雪芹', '9787020002207', 'hongloumeng.jpg', '中国古典四大名著之一', '1996-12-01', 2),
('明朝那些事儿', '当年明月', '9787801655037', 'mingchao.jpg', '讲述明朝历史的通俗读物', '2009-04-01', 3),
('时间简史', '史蒂芬·霍金', '9787535732309', 'time.jpg', '探索宇宙奥秘的科普著作', '2010-04-01', 4),
('算法导论', 'Thomas H.Cormen', '9787111187776', 'algorithm.jpg', '计算机算法经典教材', '2009-07-01', 1);