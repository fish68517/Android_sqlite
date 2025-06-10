-- ----------------------------
-- 用户表 (Users)
-- ----------------------------
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

-- ----------------------------
-- 景点表 (Attractions)
-- ----------------------------
CREATE TABLE attractions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    image_url TEXT, -- 景点图片路径
    location TEXT,
    price REAL
);

-- ----------------------------
-- 预订表 (Bookings)
-- ----------------------------
CREATE TABLE bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    attraction_id INTEGER,
    booking_date TEXT, -- 预订日期
    status TEXT, -- 'confirmed', 'cancelled'
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (attraction_id) REFERENCES attractions (id)
);

-- ----------------------------
-- 行程表 (Itineraries)
-- ----------------------------
CREATE TABLE itineraries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    name TEXT NOT NULL, -- 行程名称，如 "北京三日游"
    start_date TEXT,
    end_date TEXT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ----------------------------
-- 行程项目表 (Itinerary Items)
-- ----------------------------
CREATE TABLE itinerary_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    itinerary_id INTEGER,
    attraction_id INTEGER,
    visit_date TEXT, -- 访问日期
    visit_time TEXT, -- 访问时间
    notes TEXT, -- 备注
    FOREIGN KEY (itinerary_id) REFERENCES itineraries (id),
    FOREIGN KEY (attraction_id) REFERENCES attractions (id)
);

-- ----------------------------
-- 个人动态表 (Posts)
-- ----------------------------
CREATE TABLE posts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    content TEXT,
    image_url TEXT, -- 动态图片路径
    created_at TEXT, -- 创建时间
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ----------------------------
-- 插入一些示例数据
-- ----------------------------
INSERT INTO users (username, password) VALUES ('1', '1');
INSERT INTO users (username, password) VALUES ('2', '2');

INSERT INTO attractions (name, description, image_url, location, price) VALUES ('故宫', '北京故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。', 'attraction_image3.jpg', '北京市东城区景山前街4号', 60.0);
INSERT INTO attractions (name, description, image_url, location, price) VALUES ('外滩', '上海外滩地处黄浦江畔，是上海的标志性景点之一，全长约1.5公里。', 'flight_image1.jpg', '上海市黄浦区中山东一路', 0.0);
INSERT INTO attractions (name, description, image_url, location, price) VALUES ('西湖', '杭州西湖以其秀丽的湖光山色和众多的名胜古迹而闻名中外，被誉为人间天堂。', 'attraction_image2.jpg', '浙江省杭州市西湖区', 0.0);

INSERT INTO posts (user_id, content, image_url, created_at) VALUES (1, '今天去了故宫，太宏伟了！', 'attraction_image1.jpg', '2023-10-27 14:30:00');
INSERT INTO posts (user_id, content, image_url, created_at) VALUES (2, '夜游外滩，灯火辉煌，美不胜收。', 'attraction_image2.jpg', '2023-10-28 20:00:00');