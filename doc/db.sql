-- 创建班级表
CREATE TABLE classes (
    class_id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_name VARCHAR(50) NOT NULL,        -- 班级名称
    class_type VARCHAR(20) NOT NULL,        -- 班级类别
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建学生表
CREATE TABLE students (
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,              -- 学生姓名
    gender VARCHAR(10),                     -- 性别
    class_id INTEGER,                       -- 所属班级ID
    admission_date DATE,                    -- 入学日期
    graduation_date DATE,                   -- 毕业日期
    status VARCHAR(20) DEFAULT '在校',      -- 学籍状态（在校/毕业）
    username VARCHAR(50),                   -- 登录用户名
    password VARCHAR(50),                   -- 登录密码
    is_admin INTEGER DEFAULT 0,             -- 是否是管理员（0否，1是）
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
);

-- 创建学籍状态变更记录表
CREATE TABLE student_status_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER,                     -- 学生ID
    status VARCHAR(20) NOT NULL,            -- 状态（入学/毕业）
    change_date DATE NOT NULL,              -- 状态变更日期
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- 创建索引
CREATE INDEX idx_students_class_id ON students(class_id);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_username ON students(username);
CREATE INDEX idx_students_is_admin ON students(is_admin);
CREATE INDEX idx_student_status_history_student_id ON student_status_history(student_id);
CREATE INDEX idx_student_status_history_change_date ON student_status_history(change_date);

-- 插入默认管理员账号
INSERT INTO students (name, username, password, is_admin) 
VALUES ('管理员', 'admin', 'admin123', 1);

-- 插入班级数据
INSERT INTO classes (class_name, class_type) VALUES 
('计算机科学1班', '计算机类'),
('计算机科学2班', '计算机类'),
('软件工程1班', '计算机类'),
('电子信息1班', '电子信息类'),
('通信工程1班', '电子信息类');

-- 插入学生数据
INSERT INTO students (name, gender, class_id, admission_date, graduation_date, status, username, password, is_admin) VALUES 
('张三', '男', 1, '2020-09-01', NULL, '在校', 'zhangsan', '123456', 0),
('李四', '女', 1, '2020-09-01', NULL, '在校', 'lisi', '123456', 0),
('王五', '男', 2, '2020-09-01', '2024-07-01', '毕业', 'wangwu', '123456', 0),
('赵六', '女', 2, '2020-09-01', NULL, '在校', 'zhaoliu', '123456', 0),
('孙七', '男', 3, '2021-09-01', NULL, '在校', 'sunqi', '123456', 0),
('周八', '女', 3, '2021-09-01', NULL, '在校', 'zhouba', '123456', 0),
('吴九', '男', 4, '2021-09-01', NULL, '在校', 'wujiu', '123456', 0),
('郑十', '女', 4, '2021-09-01', NULL, '在校', 'zhengshi', '123456', 0);

-- 插入学籍状态变更记录
INSERT INTO student_status_history (student_id, status, change_date) VALUES 
(2, '入学', '2020-09-01'),
(2, '在校', '2020-09-01'),
(3, '入学', '2020-09-01'),
(3, '在校', '2020-09-01'),
(3, '毕业', '2024-07-01'),
(4, '入学', '2020-09-01'),
(4, '在校', '2020-09-01'),
(5, '入学', '2021-09-01'),
(5, '在校', '2021-09-01'),
(6, '入学', '2021-09-01'),
(6, '在校', '2021-09-01'),
(7, '入学', '2021-09-01'),
(7, '在校', '2021-09-01'),
(8, '入学', '2021-09-01'),
(8, '在校', '2021-09-01'),
(9, '入学', '2021-09-01'),
(9, '在校', '2021-09-01');