package com.example.orderfood;

import android.os.Handler;
import android.os.Looper;

import com.example.orderfood.model.BrowseRecord;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.DishCategory;
import com.example.orderfood.model.MerchantBean;
import com.example.orderfood.model.Orders;
import com.example.orderfood.model.Review;
import com.example.orderfood.model.SalesStats;
import com.example.orderfood.model.SearchRecord;
import com.example.orderfood.model.Student;
import com.example.orderfood.model.UserAddress;
import com.google.gson.Gson;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DBMysqlHelper {
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    private static final String URL = "jdbc:mysql://192.168.8.111:3306/foodorder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static DBMysqlHelper instance = null;

    private Connection connection;
    
    // 创建固定大小的线程池
    private final ExecutorService executorService;
    private static final int THREAD_POOL_SIZE = 5; // 可以根据需求调整线程池大小

    private final Handler mainHandler;

    private DBMysqlHelper() {
        // 初始化线程池
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        // 初始化主线程Handler
        mainHandler = new Handler(Looper.getMainLooper());
        
        // 在线程池中初始化数据库连接
        executorService.submit(() -> {
            try {
                Class.forName(DRIVER);
                System.out.println("JDBC驱动加载成功");
                
                System.out.println("正在尝试连接到: " + URL);
                System.out.println("用户名: " + USER);
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("数据库连接成功");
            } catch (ClassNotFoundException e) {
                System.out.println("JDBC驱动加载失败: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("数据库连接失败");
                System.out.println("错误代码: " + e.getErrorCode());
                System.out.println("SQL状态: " + e.getSQLState());
                System.out.println("错误信息: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static synchronized DBMysqlHelper getInstance() {
        if (instance == null) {
            instance = new DBMysqlHelper();
        }
        return instance;
    }

    // 关闭线程池和数据库连接的方法
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // 使用线程池执行数据库操作的通用方法
    private <T> void executeDbOperation(DatabaseOperation<T> operation, DatabaseCallback<T> callback) {
        executorService.submit(() -> {
            try {
                T result = operation.execute(getConnection());
                // 在主线程中执行成功回调
                mainHandler.post(() -> callback.onSuccess(result));
            } catch (Exception e) {
                // 在主线程中执行错误回调
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    // 定义数据库操作接口
    private interface DatabaseOperation<T> {
        T execute(Connection connection) throws SQLException;
    }

    // 重构后的注册方法
    public void registerStudent(Student student, DatabaseCallback<Student> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO students (name, password, contact_info) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getPassword());
                pstmt.setString(3, student.getContactInfo());
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    student.setStudentId(rs.getInt(1));
                }
                return student;
            }
        }, callback);
    }

    // 重构后的登录方法
    public void loginStudent(String name, String password, DatabaseCallback<Map<String, Object>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM students WHERE name = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                Map<String, Object> result = new HashMap<>();
                if (rs.next()) {
                    String token = UUID.randomUUID().toString();
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setName(rs.getString("name"));
                    
                    result.put("token", token);
                    result.put("student", new Gson().toJson(student));
                }
                return result;
            }
        }, callback);
    }

    // 获取所有学生
    public void getAllStudents(DatabaseCallback<List<Student>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM students";
            List<Student> students = new ArrayList<>();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setName(rs.getString("name"));
                    student.setContactInfo(rs.getString("contact_info"));
                    students.add(student);
                }
                return students;
            }
        }, callback);
    }

    // 获取学生信息
    public void getStudentInfo(int id, DatabaseCallback<Student> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setName(rs.getString("name"));
                    student.setContactInfo(rs.getString("contact_info"));
                    return student;
                }
                return null;
            }
        }, callback);
    }

    // 更新学生信息
    public void updateStudentInfo(int id, Student student, DatabaseCallback<Student> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE students SET name = ?, password = ?, contact_info = ? WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getPassword());
                pstmt.setString(3, student.getContactInfo());
                pstmt.setInt(4, id);
                
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0 ? student : null;
            }
        }, callback);
    }

    // 商家相关方法
    public void registerMerchant(MerchantBean merchant, DatabaseCallback<MerchantBean> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO merchants (merchant_id, name, password, window_location, business_hours, category,content," +
                    "browse_count,sales)" +
                    " VALUES (?, ?, ?, ?, ?,?,?,?,?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, MyApplication.lastMerchantId+1);
                pstmt.setString(2, merchant.getName());
                pstmt.setString(3, merchant.getPassword());
                pstmt.setString(4, merchant.getWindowLocation());
                pstmt.setString(5, merchant.getBusinessHours());
                pstmt.setString(6, merchant.getCategory());
                pstmt.setString(7, "新店开业，欢迎光临！");
                pstmt.setInt(8, 0);
                pstmt.setInt(9, 0);



                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    merchant.setMerchantId(rs.getInt(1));
                }
                return merchant;
            }
        }, callback);
    }

    public void loginMerchant(String name, String password, DatabaseCallback<Map<String, String>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM merchants WHERE name = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                
                Map<String, String> result = new HashMap<>();
                if (rs.next()) {
                    String token = UUID.randomUUID().toString();
                    com.example.orderfood.model.MerchantBean merchant = new com.example.orderfood.model.MerchantBean();
                    merchant.setMerchantId(rs.getInt("merchant_id"));
                    merchant.setName(rs.getString("name"));
                    merchant.setWindowLocation(rs.getString("window_location"));
                    merchant.setBusinessHours(rs.getString("business_hours"));
                    merchant.setPassword(rs.getString("password"));

                    result.put("token", token);
                    Gson gson = new Gson();
                    result.put("merchant",gson.toJson(merchant));
                }
                return result;
            }
        }, callback);
    }


    public void findMerchantByName(String name, DatabaseCallback<Map<String, String>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM merchants WHERE name = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                ResultSet rs = pstmt.executeQuery();

                Map<String, String> result = new HashMap<>();
                if (rs.next()) {
                    com.example.orderfood.model.MerchantBean merchant = new com.example.orderfood.model.MerchantBean();
                    merchant.setMerchantId(rs.getInt("merchant_id"));
                    merchant.setName(rs.getString("name"));
                    merchant.setWindowLocation(rs.getString("window_location"));
                    merchant.setBusinessHours(rs.getString("business_hours"));

                    Gson gson = new Gson();
                    result.put("merchant", gson.toJson(merchant));
                }  else {
                    result.put("message", "Merchant not found");
                }
                return result;
            }
        }, callback);
    }

    // 菜品相关方法
    public void getAllDishes(DatabaseCallback<List<Dish>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM dishes";
            List<Dish> dishes = new ArrayList<>();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Dish dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("name"));
                    dish.setPrice(rs.getDouble("price"));
                    dish.setImageUrl(rs.getString("image_url"));
                    dish.setCategory(rs.getString("category"));
                    dish.setMerchantId(rs.getInt("merchant_id"));
                    dish.setDescription(rs.getString("description"));
                    dish.setBrowseCount(rs.getInt("browse_count"));
                    dish.setSales(rs.getInt("sales"));
                    dish.setStock(rs.getInt("stock"));
                    dish.setSpecifications(rs.getString("specifications"));
                    dishes.add(dish);
                }
                return dishes;
            }
        }, callback);
    }

    // 订单相关方法
    public void getAllOrders(DatabaseCallback<List<Orders>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM orders";
            List<Orders> ordersList = new ArrayList<>();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setOrderTime(rs.getString("order_time"));
                    order.setStudentId(rs.getInt("student_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setDishList(rs.getString("dish_list"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setDishId(rs.getInt("dish_id"));
                    order.setDiningOption(rs.getString("dining_option"));
                    order.setOrderQuantity(rs.getInt("order_quantity"));
                    ordersList.add(order);
                }
                return ordersList;
            }
        }, callback);
    }

    public void getOrderById(int id, DatabaseCallback<Orders> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM orders WHERE order_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setOrderTime(rs.getString("order_time"));
                    order.setStudentId(rs.getInt("student_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setDishList(rs.getString("dish_list"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setDishId(rs.getInt("dish_id"));
                    order.setDiningOption(rs.getString("dining_option"));
                    order.setOrderQuantity(rs.getInt("order_quantity"));
                    return order;
                }
                return null;
            }
        }, callback);
    }

    public void createOrder(Orders order, DatabaseCallback<Orders> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO orders (order_time, student_id, merchant_id, dish_list, " +
                        "total_price, order_status, dish_id, dining_option, order_quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, order.getOrderTime());
                pstmt.setInt(2, order.getStudentId());
                pstmt.setInt(3, order.getMerchantId());
                pstmt.setString(4, order.getDishList());
                pstmt.setDouble(5, order.getTotalPrice());
                pstmt.setString(6, order.getOrderStatus());
                pstmt.setInt(7, order.getDishId());
                pstmt.setString(8, order.getDiningOption());
                pstmt.setInt(9, order.getOrderQuantity());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    order.setOrderId(rs.getInt(1));
                }
                return order;
            }
        }, callback);
    }

    public void deleteOrder(int id, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM orders WHERE order_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        }, callback);
    }



    // 评价相关方法
    public void getReviewById(int id, DatabaseCallback<Review> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM reviews WHERE review_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setStudentId(rs.getInt("student_id"));
                    review.setDishId(rs.getInt("dish_id"));
                    review.setMerchantId(rs.getInt("merchant_id"));
                    review.setContent(rs.getString("content"));
                    review.setRating(rs.getInt("rating"));
                    review.setReviewTime(rs.getString("review_time"));
                    return review;
                }
                return null;
            }
        }, callback);
    }

    public void createReview(Review review, DatabaseCallback<Review> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO reviews (student_id, dish_id, merchant_id, content, rating, review_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, review.getStudentId());
                pstmt.setInt(2, review.getDishId());
                pstmt.setInt(3, review.getMerchantId());
                pstmt.setString(4, review.getContent());
                pstmt.setInt(5, review.getRating());
                pstmt.setString(6, review.getReviewTime());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    review.setReviewId(rs.getInt(1));
                }
                return review;
            }
        }, callback);
    }

    public void updateReview(int id, Review review, DatabaseCallback<Review> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE reviews SET content = ?, rating = ? WHERE review_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, review.getContent());
                pstmt.setInt(2, review.getRating());
                pstmt.setInt(3, id);
                
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0 ? review : null;
            }
        }, callback);
    }

    public void deleteReview(int id, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM reviews WHERE review_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        }, callback);
    }

    public void getReviewsByDishId(int dishId, DatabaseCallback<List<Review>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM reviews WHERE dish_id = ? ORDER BY review_time DESC";
            List<Review> reviews = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, dishId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setStudentId(rs.getInt("student_id"));
                    review.setDishId(rs.getInt("dish_id"));
                    review.setMerchantId(rs.getInt("merchant_id"));
                    review.setContent(rs.getString("content"));
                    review.setRating(rs.getInt("rating"));
                    review.setReviewTime(rs.getString("review_time"));
                    reviews.add(review);
                }
                return reviews;
            }
        }, callback);
    }

    // 搜索记录相关方法
    public void addSearchRecord(SearchRecord record, DatabaseCallback<SearchRecord> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO search_records (student_id, keyword, search_time) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, record.getStudentId());
                pstmt.setString(2, record.getKeyword());
                pstmt.setString(3, record.getSearchTime());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    record.setSearchId(rs.getInt(1));
                }
                return record;
            }
        }, callback);
    }

    public void getSearchHistory(int studentId, DatabaseCallback<List<SearchRecord>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM search_records WHERE student_id = ? ORDER BY search_time DESC";
            List<SearchRecord> records = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    SearchRecord record = new SearchRecord();
                    record.setSearchId(rs.getInt("search_id"));
                    record.setStudentId(rs.getInt("student_id"));
                    record.setKeyword(rs.getString("keyword"));
                    record.setSearchTime(rs.getString("search_time"));
                    records.add(record);
                }
                return records;
            }
        }, callback);
    }

    public void clearSearchHistory(int studentId, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM search_records WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        }, callback);
    }

    // 种类相关方法
    public void getAllCategories(DatabaseCallback<List<DishCategory>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM category";
            List<DishCategory> categories = new ArrayList<>();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    DishCategory category = new DishCategory();
                    category.setCategoryId(rs.getInt("category_id"));
                    category.setName(rs.getString("name"));
                    category.setImageUrl(rs.getString("image_url"));
                    categories.add(category);
                }
                return categories;
            }
        }, callback);
    }

    public void addCategory(DishCategory category, DatabaseCallback<DishCategory> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO category (name, image_url) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, category.getName());
                pstmt.setString(2, category.getImageUrl());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    category.setCategoryId(rs.getInt(1));
                }
                return category;
            }
        }, callback);
    }

    // 推荐相关方法
    public void getDishRecommendations(int studentId, DatabaseCallback<List<Dish>> callback) {
        executeDbOperation(connection -> {
            // 基于用户历史订单的推荐
            String sql = "SELECT d.*, COUNT(*) as order_count " +
                        "FROM dishes d " +
                        "JOIN orders o ON d.dish_id = o.dish_id " +
                        "WHERE o.student_id = ? " +
                        "GROUP BY d.dish_id " +
                        "ORDER BY order_count DESC, d.sales DESC " +
                        "LIMIT 10";
            
            List<Dish> recommendations = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Dish dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("name"));
                    dish.setPrice(rs.getDouble("price"));
                    dish.setImageUrl(rs.getString("image_url"));
                    dish.setCategory(rs.getString("category"));
                    dish.setMerchantId(rs.getInt("merchant_id"));
                    dish.setDescription(rs.getString("description"));
                    dish.setBrowseCount(rs.getInt("browse_count"));
                    dish.setSales(rs.getInt("sales"));
                    recommendations.add(dish);
                }
                return recommendations;
            }
        }, callback);
    }

    public void getPopularDishes(DatabaseCallback<List<Dish>> callback) {
        executeDbOperation(connection -> {
            // 基于销量和浏览量的推荐
            String sql = "SELECT * FROM dishes ORDER BY sales DESC, browse_count DESC LIMIT 10";
            List<Dish> popularDishes = new ArrayList<>();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Dish dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("name"));
                    dish.setPrice(rs.getDouble("price"));
                    dish.setImageUrl(rs.getString("image_url"));
                    dish.setCategory(rs.getString("category"));
                    dish.setMerchantId(rs.getInt("merchant_id"));
                    dish.setDescription(rs.getString("description"));
                    dish.setBrowseCount(rs.getInt("browse_count"));
                    dish.setSales(rs.getInt("sales"));
                    popularDishes.add(dish);
                }
                return popularDishes;
            }
        }, callback);
    }

    // 浏览记录相关方法
    public void addDishBrowseRecord(BrowseRecord record, DatabaseCallback<BrowseRecord> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO browserecords (student_id, dish_id, browse_time) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, record.getStudentId());
                pstmt.setInt(2, record.getDishId());
                pstmt.setString(3, record.getBrowseTime());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    record.setRecordId(rs.getInt(1));
                }
                
                // 更新菜品浏览次数
                updateDishBrowseCount(connection, record.getDishId());
                
                return record;
            }
        }, callback);
    }

    private void updateDishBrowseCount(Connection connection, int dishId) throws SQLException {
        String sql = "UPDATE dishes SET browse_count = browse_count + 1 WHERE dish_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            pstmt.executeUpdate();
        }
    }

    public void getDishBrowseHistory(int studentId, DatabaseCallback<List<BrowseRecord>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT b.*, d.name as dish_name FROM browserecords b " +
                        "JOIN dishes d ON b.dish_id = d.dish_id " +
                        "WHERE b.student_id = ? " +
                        "ORDER BY b.browse_time DESC";
            
            List<BrowseRecord> records = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    BrowseRecord record = new BrowseRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setStudentId(rs.getInt("student_id"));
                    record.setDishId(rs.getInt("dish_id"));
                    record.setBrowseTime(rs.getString("browse_time"));
                    records.add(record);
                }
                return records;
            }
        }, callback);
    }

    public void clearBrowseHistory(int studentId, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM browserecords WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        }, callback);
    }

    // 获取商家的订单列表
    public void getOrdersByMerchantId(int merchantId, DatabaseCallback<List<Orders>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM orders WHERE merchant_id = ? AND (order_status = 'completed' OR order_status = 'paid') ORDER BY order_time DESC";
            List<Orders> ordersList = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, merchantId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setOrderTime(rs.getString("order_time"));
                    order.setStudentId(rs.getInt("student_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setDishList(rs.getString("dish_list"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setDishId(rs.getInt("dish_id"));
                    order.setDiningOption(rs.getString("dining_option"));
                    order.setOrderQuantity(rs.getInt("order_quantity"));
                    ordersList.add(order);
                }
                return ordersList;
            }
        }, callback);
    }

    // 获取商家信息
    public void getMerchantInfo(int id, DatabaseCallback<com.example.orderfood.model.MerchantBean> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM merchants WHERE merchant_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return getMerchantFromResultSet(rs);
                }
                return null;
            }
        }, callback);
    }

    // 根据 merchantId 列表获取完整的商家信息列表
    public void getMerchantListByMerchantIds(List<Integer> merchantIds, DatabaseCallback<List<com.example.orderfood.model.MerchantBean>> callback) {
        executeDbOperation(connection -> {
            // 1. 构建 SQL 语句
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM merchants WHERE merchant_id IN (");
            for (int i = 0; i < merchantIds.size(); i++) {
                sqlBuilder.append("?");
                if (i < merchantIds.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(")");
            String sql = sqlBuilder.toString();

            // 2. 执行 SQL 查询
            List<com.example.orderfood.model.MerchantBean> merchantList = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // 设置参数
                for (int i = 0; i < merchantIds.size(); i++) {
                    pstmt.setInt(i + 1, merchantIds.get(i));
                }

                ResultSet rs = pstmt.executeQuery();

                // 3. 将 ResultSet 转换为 MerchantBean 对象列表
                while (rs.next()) {
                    com.example.orderfood.model.MerchantBean merchant = getMerchantFromResultSet(rs);
                    merchantList.add(merchant);
                }

                return merchantList;
            }
        }, callback);
    }


    // 下单
    public void placeOrder(Orders order, DatabaseCallback<Orders> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO orders (order_time, student_id, merchant_id, dish_list, " +
                        "total_price, order_status, dish_id, dining_option, order_quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, order.getOrderTime());
                pstmt.setInt(2, order.getStudentId());
                pstmt.setInt(3, order.getMerchantId());
                pstmt.setString(4, order.getDishList());
                pstmt.setDouble(5, order.getTotalPrice());
                pstmt.setString(6, order.getOrderStatus());
                pstmt.setInt(7, order.getDishId());
                pstmt.setString(8, order.getDiningOption());
                pstmt.setInt(9, order.getOrderQuantity());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    order.setOrderId(rs.getInt(1));
                }

                // 更新菜品销量和库存
                updateDishSalesAndStock(connection, order.getDishId(), order.getOrderQuantity());
                
                return order;
            }
        }, callback);
    }

    // 辅助方法：更新菜品销量和库存
    private void updateDishSalesAndStock(Connection connection, int dishId, int quantity) throws SQLException {
        String sql = "UPDATE dishes SET sales = sales + ?, stock = stock - ? WHERE dish_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, dishId);
            pstmt.executeUpdate();
        }
    }


    public void getPurchasedMerchantsByCompletedOrders(int studentId,
                                                       DatabaseCallback<List<com.example.orderfood.model.MerchantBean>> callback) {
        executeDbOperation(connection -> {
            // 通过订单表关联商家表，获取用户购买过的商家，且订单状态为 "completed" 或 "paid"
            String sql = "SELECT DISTINCT m.* FROM merchants m " +
                    "INNER JOIN orders o ON m.merchant_id = o.merchant_id " +
                    "WHERE o.student_id = ? AND (o.order_status = 'completed' OR o.order_status = 'paid')"; // 移除 ORDER BY 子句

            List<com.example.orderfood.model.MerchantBean> merchants = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                   /* com.example.orderfood.model.MerchantBean merchant = new com.example.orderfood.model.MerchantBean();
                    merchant.setMerchantId(rs.getInt("merchant_id"));
                    merchant.setName(rs.getString("name"));
                    merchant.setWindowLocation(rs.getString("window_location"));
                    merchant.setBusinessHours(rs.getString("business_hours"));
                    merchant.setCategory(rs.getString("category"));
                    merchants.add(merchant);*/
                    merchants.add(getMerchantFromResultSet(rs));
                }
                return merchants;
            }
        }, callback);
    }



    // 获取用户购买过的商家
    public void getPurchasedMerchants(int studentId, DatabaseCallback<List<com.example.orderfood.model.MerchantBean>> callback) {
        executeDbOperation(connection -> {
            // 通过订单表关联商家表，获取用户购买过的商家
            String sql = "SELECT DISTINCT m.* FROM merchants m " +
                        "INNER JOIN orders o ON m.merchant_id = o.merchant_id " +
                        "WHERE o.student_id = ? " +
                        "ORDER BY o.order_time DESC";
            
            List<com.example.orderfood.model.MerchantBean> merchants = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    com.example.orderfood.model.MerchantBean merchant = new com.example.orderfood.model.MerchantBean();
                    merchant.setMerchantId(rs.getInt("merchant_id"));
                    merchant.setName(rs.getString("name"));

                    merchant.setWindowLocation(rs.getString("window_location"));
                    merchant.setBusinessHours(rs.getString("business_hours"));

                    merchant.setCategory(rs.getString("category"));
                    merchants.add(merchant);
                }
                return merchants;
            }
        }, callback);
    }

    // 根据分类获取商家列表
    public void getMerchantsByCategory(String category, DatabaseCallback<List<com.example.orderfood.model.MerchantBean>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM merchants WHERE category = ? ORDER BY browse_count DESC";
            List<com.example.orderfood.model.MerchantBean> merchants = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, category);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    merchants.add(getMerchantFromResultSet(rs));
                }
                return merchants;
            }
        }, callback);
    }

    private com.example.orderfood.model.MerchantBean getMerchantFromResultSet(ResultSet rs) throws SQLException {
        com.example.orderfood.model.MerchantBean merchant = new com.example.orderfood.model.MerchantBean();
        merchant.setMerchantId(rs.getInt("merchant_id"));
        merchant.setName(rs.getString("name"));
        merchant.setImageName(rs.getString("image_name"));
        merchant.setRating(rs.getDouble("rating"));
        merchant.setSales(rs.getInt("sales"));
        merchant.setMinPrice(rs.getDouble("min_price"));
        merchant.setDiscountInfo(rs.getString("discount_info"));
        merchant.setDelivery(rs.getBoolean("delivery"));
        merchant.setRemark(rs.getString("remark"));
        merchant.setCategory(rs.getString("category"));
        merchant.setWindowLocation(rs.getString("window_location"));
        merchant.setBusinessHours(rs.getString("business_hours"));
        merchant.setContent(rs.getString("content"));
        return merchant;
    }

    // 添加商品到购物车
    public void addToCart(int studentId, int merchantId, int dishId, int quantity, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            // 先检查是否已存在该商品的记录
            String checkSql = "SELECT quantity FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, studentId);
                checkStmt.setInt(2, merchantId);
                checkStmt.setInt(3, dishId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // 已存在，更新数量
                    String updateSql = "UPDATE orderrecords SET quantity = quantity + ? WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, studentId);
                        updateStmt.setInt(3, merchantId);
                        updateStmt.setInt(4, dishId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // 不存在，插入新记录
                    String insertSql = "INSERT INTO orderrecords (student_id, merchant_id, dish_id, quantity, status, create_time) VALUES (?, ?, ?, ?, 'cart', NOW())";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, studentId);
                        insertStmt.setInt(2, merchantId);
                        insertStmt.setInt(3, dishId);
                        insertStmt.setInt(4, quantity);
                        insertStmt.executeUpdate();
                    }
                }
                return null;
            }
        }, callback);
    }

    // 从购物车移除商品
    public void removeFromCart(int studentId, int merchantId, int dishId, int quantity, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE orderrecords SET quantity = quantity - ? WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, studentId);
                pstmt.setInt(3, merchantId);
                pstmt.setInt(4, dishId);
                pstmt.executeUpdate();

                // 如果数量为0，删除记录
                String deleteSql = "DELETE FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND quantity <= 0 AND status = 'cart'";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, studentId);
                    deleteStmt.setInt(2, merchantId);
                    deleteStmt.setInt(3, dishId);
                    deleteStmt.executeUpdate();
                }
                return null;
            }
        }, callback);
    }

    // 获取购物车中商品数量
    public void getCartItemQuantity(int studentId, int merchantId, int dishId, DatabaseCallback<Integer> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT quantity FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, merchantId);
                pstmt.setInt(3, dishId);
                ResultSet rs = pstmt.executeQuery();
                return rs.next() ? rs.getInt("quantity") : 0;
            }
        }, callback);
    }

    // 获取用户购物车中的所有商品
    public void getCartItems(int studentId, DatabaseCallback<List<CartItem>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT r.*, d.name as dish_name, d.price, d.image_url, m.name as merchant_name " +
                        "FROM orderrecords r " +
                        "JOIN dishes d ON r.dish_id = d.dish_id " +
                        "JOIN merchants m ON r.merchant_id = m.merchant_id " +
                        "WHERE r.student_id = ? AND r.status = 'cart' " +
                        "ORDER BY r.merchant_id, r.create_time";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                List<CartItem> cartItems = new ArrayList<>();
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setRecordId(rs.getInt("record_id"));
                    item.setStudentId(rs.getInt("student_id"));
                    item.setMerchantId(rs.getInt("merchant_id"));
                    item.setDishId(rs.getInt("dish_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDishName(rs.getString("dish_name"));
                    item.setPrice(rs.getDouble("price"));
                    item.setImageUrl(rs.getString("image_url"));
                    try {
                        item.setMerchantName(rs.getString("merchant_name"));
                    } catch (Exception e) {
                        System.out.println("merchantname is null");
                    }

                    cartItems.add(item);
                }
                return cartItems;
            }
        }, callback);
    }

    // 获取购物车中特定商家的商品
    public void getCartItemsByMerchant(int studentId, int merchantId, DatabaseCallback<List<CartItem>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT r.*, d.name as dish_name, d.price, d.image_url " +
                        "FROM orderrecords r " +
                        "JOIN dishes d ON r.dish_id = d.dish_id " +
                        "WHERE r.student_id = ? AND r.merchant_id = ? AND r.status = 'cart' " +
                        "ORDER BY r.create_time";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, merchantId);
                ResultSet rs = pstmt.executeQuery();
                
                List<CartItem> cartItems = new ArrayList<>();
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setRecordId(rs.getInt("record_id"));
                    item.setDishId(rs.getInt("dish_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDishName(rs.getString("dish_name"));
                    item.setPrice(rs.getDouble("price"));
                    item.setImageUrl(rs.getString("image_url"));
                    cartItems.add(item);
                }
                return cartItems;
            }
        }, callback);
    }

    // 更新购物车商品数量
    public void updateCartItemQuantity(int recordId, int quantity, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE orderrecords SET quantity = ? WHERE record_id = ? AND status = 'cart'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, recordId);
                pstmt.executeUpdate();
                return null;
            }
        }, callback);
    }

    // 清空用户购物车
    public void clearCart(int studentId, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM orderrecords WHERE student_id = ? AND status = 'cart'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.executeUpdate();
                return null;
            }
        }, callback);
    }

    // 清空特定商家的购物车商品
    public void clearMerchantCart(int studentId, int merchantId, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND status = 'cart'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, merchantId);
                pstmt.executeUpdate();
                return null;
            }
        }, callback);
    }

    // 获取购物车商品总数
    public void getCartItemCount(int studentId, DatabaseCallback<Integer> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT COUNT(*) FROM orderrecords WHERE student_id = ? AND status = 'cart'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                return rs.next() ? rs.getInt(1) : 0;
            }
        }, callback);
    }

    // 获取用户地址
    public void getUserAddress(int userId, DatabaseCallback<UserAddress> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM user_address WHERE user_id = ? AND is_default = 1";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    UserAddress address = new UserAddress();
                    address.setId(rs.getInt("id"));
                    address.setUserId(rs.getInt("user_id"));
                    address.setName(rs.getString("name"));
                    address.setPhone(rs.getString("phone"));
                    address.setAddress(rs.getString("address"));
                    address.setDefault(rs.getBoolean("is_default"));
                    return address;
                }
                return null;
            }
        }, callback);
    }

    // 获取用户所有地址
    public void getUserAddresses(int userId, DatabaseCallback<List<UserAddress>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM user_address WHERE user_id = ? ORDER BY is_default DESC";
            List<UserAddress> addresses = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    UserAddress address = new UserAddress();
                    address.setId(rs.getInt("id"));
                    address.setUserId(rs.getInt("user_id"));
                    address.setName(rs.getString("name"));
                    address.setPhone(rs.getString("phone"));
                    address.setAddress(rs.getString("address"));
                    address.setDefault(rs.getBoolean("is_default"));
                    addresses.add(address);
                }
                return addresses;
            }
        }, callback);
    }

    // 添加新地址
    public void addUserAddress(UserAddress address, DatabaseCallback<UserAddress> callback) {
        executeDbOperation(connection -> {
            // 如果新地址是默认地址，先将其他地址设为非默认
            if (address.isDefault()) {
                String updateSql = "UPDATE user_address SET is_default = 0 WHERE user_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                    pstmt.setInt(1, address.getUserId());
                    pstmt.executeUpdate();
                }
            }

            String sql = "INSERT INTO user_address (user_id, name, phone, address, is_default) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, address.getUserId());
                pstmt.setString(2, address.getName());
                pstmt.setString(3, address.getPhone());
                pstmt.setString(4, address.getAddress());
                pstmt.setBoolean(5, address.isDefault());
                
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    address.setId(rs.getInt(1));
                }
                return address;
            }
        }, callback);
    }

    // 更新地址
    public void updateUserAddress(UserAddress address, DatabaseCallback<UserAddress> callback) {
        executeDbOperation(connection -> {
            // 如果更新为默认地址，先将其他地址设为非默认
            if (address.isDefault()) {
                String updateSql = "UPDATE user_address SET is_default = 0 WHERE user_id = ? AND id != ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                    pstmt.setInt(1, address.getUserId());
                    pstmt.setInt(2, address.getId());
                    pstmt.executeUpdate();
                }
            }

            String sql = "UPDATE user_address SET name = ?, phone = ?, address = ?, is_default = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, address.getName());
                pstmt.setString(2, address.getPhone());
                pstmt.setString(3, address.getAddress());
                pstmt.setBoolean(4, address.isDefault());
                pstmt.setInt(5, address.getId());
                
                pstmt.executeUpdate();
                return address;
            }
        }, callback);
    }

    // 删除地址
    public void deleteUserAddress(int addressId, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            String sql = "DELETE FROM user_address WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, addressId);
                pstmt.executeUpdate();
                return null;
            }
        }, callback);
    }

    // 批量删除购物车商品
    public void removeCartItems(List<Integer> recordIds, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            if (recordIds == null || recordIds.isEmpty()) {
                return null;
            }

            // 构建IN子句的参数占位符
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < recordIds.size(); i++) {
                if (i > 0) {
                    placeholders.append(",");
                }
                placeholders.append("?");
            }

            String sql = "DELETE FROM orderrecords WHERE record_id IN (" + placeholders.toString() + ")";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // 设置IN子句的参数值
                for (int i = 0; i < recordIds.size(); i++) {
                    pstmt.setInt(i + 1, recordIds.get(i));
                }
                pstmt.executeUpdate();
                return null;
            }
        }, callback);
    }

    // 获取学生的所有订单
    public void getOrdersByStudent(int studentId, DatabaseCallback<List<Orders>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM orders WHERE student_id = ? ORDER BY order_time DESC";
            List<Orders> ordersList = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Orders order = new Orders();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setOrderTime(rs.getString("order_time"));
                    order.setStudentId(rs.getInt("student_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setDishList(rs.getString("dish_list"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setDishId(rs.getInt("dish_id"));
                    order.setDiningOption(rs.getString("dining_option"));
                    order.setOrderQuantity(rs.getInt("order_quantity"));
                    ordersList.add(order);
                }
                return ordersList;
            }
        }, callback);
    }

    // 更新订单状态
    public void updateOrderStatus(int orderId, String status, DatabaseCallback<Void> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
                return null;
            }
        }, callback);
    }


/*    public void updateOrderStatus(int orderId, String status, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, orderId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        }, callback);
    }*/

    // 获取菜品信息
    public void getDishInfo(int dishId, DatabaseCallback<Dish> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM dishes WHERE dish_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, dishId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Dish dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("name"));
                    dish.setPrice(rs.getDouble("price"));
                    dish.setImageUrl(rs.getString("image_url"));
                    dish.setDescription(rs.getString("description"));
                    dish.setStock(rs.getInt("stock"));
                    dish.setSales(rs.getInt("sales"));
                    dish.setMerchantName(rs.getString("merchant_name"));
                    return dish;
                }
                return null;
            }
        }, callback);
    }

    // 获取商家的所有菜品
    public void getMerchantDishes(int merchantId, DatabaseCallback<List<Dish>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM dishes WHERE merchant_id = ? ORDER BY sales DESC";
            List<Dish> dishes = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, merchantId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Dish dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("name"));
                    dish.setPrice(rs.getDouble("price"));
                    dish.setImageUrl(rs.getString("image_url"));
                    dish.setDescription(rs.getString("description"));
                    dish.setStock(rs.getInt("stock"));
                    dish.setSales(rs.getInt("sales"));
                    dish.setMerchantId(rs.getInt("merchant_id"));
                    dish.setCategory(rs.getString("category"));
                    dish.setMerchantName(rs.getString("merchant_name"));
                    dishes.add(dish);
                }
                return dishes;
            }
        }, callback);
    }

    // 添加新菜品
    public void addDish(Dish dish, DatabaseCallback<Dish> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO dishes (name, price, image_url, category,merchant_id,browse_count, description, sales,stock, specifications, merchant_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, dish.getName());
                pstmt.setDouble(2, dish.getPrice());
                pstmt.setString(3, dish.getImageUrl());
                pstmt.setString(4, dish.getCategory());
                pstmt.setInt(5, dish.getMerchantId());
                pstmt.setInt(6, dish.getBrowseCount());
                pstmt.setString(7, dish.getDescription());
                pstmt.setInt(8, dish.getSales());
                pstmt.setInt(9, dish.getStock());
                pstmt.setString(10, dish.getSpecifications());
                pstmt.setString(11, dish.getMerchantName());

                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    dish.setDishId(rs.getInt(1));
                }
                return dish;
            }
        }, callback);
    }

    // 更新菜品信息
    public void updateDish(Dish dish, DatabaseCallback<Dish> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE dishes SET name = ?, price = ?, image_url = ?, description = ?, " +
                        "stock = ?, category = ? WHERE dish_id = ? AND merchant_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, dish.getName());
                pstmt.setDouble(2, dish.getPrice());
                pstmt.setString(3, dish.getImageUrl());
                pstmt.setString(4, dish.getDescription());
                pstmt.setInt(5, dish.getStock());
                pstmt.setString(6, dish.getCategory());
                pstmt.setInt(7, dish.getDishId());
                pstmt.setInt(8, dish.getMerchantId());
                
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0 ? dish : null;
            }
        }, callback);
    }

    // 删除菜品
    public void deleteDish(int dishId, int merchantId, DatabaseCallback<Boolean> callback) {
        executeDbOperation(new DatabaseOperation<Boolean>() {
            @Override
            public Boolean execute(Connection connection) throws SQLException {
                String sql = "DELETE FROM dishes WHERE dish_id = ? AND merchant_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, dishId);
                    pstmt.setInt(2, merchantId);
                    int affectedRows = pstmt.executeUpdate();
                    return affectedRows > 0;
                }
            }
        }, callback);
    }

    // 生成模拟订单数据
    public void generateMockOrders(int merchantId, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "INSERT INTO orders (order_time, student_id, merchant_id, dish_list, total_price, " +
                        "order_status, dish_id, dining_option, order_quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // 获取商家的所有菜品
                String dishSql = "SELECT dish_id, price FROM dishes WHERE merchant_id = ?";
                PreparedStatement dishStmt = connection.prepareStatement(dishSql);
                dishStmt.setInt(1, merchantId);
                ResultSet dishRs = dishStmt.executeQuery();
                
                List<Integer> dishIds = new ArrayList<>();
                Map<Integer, Double> dishPrices = new HashMap<>();
                while (dishRs.next()) {
                    int dishId = dishRs.getInt("dish_id");
                    dishIds.add(dishId);
                    dishPrices.put(dishId, dishRs.getDouble("price"));
                }
                System.out.println("dishIds: " + dishIds);
                if (dishIds.isEmpty()) {
                    return false;
                }
                
                // 生成过去7天的订单
                Random random = new Random();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -7); // 从7天前开始
                
                for (int day = 0; day < 7; day++) {
                    // 每天生成3-10个订单
                    int ordersPerDay = random.nextInt(8) + 3;
                    
                    for (int i = 0; i < ordersPerDay; i++) {
                        // 随机选择1-3个菜品
                        int dishCount = random.nextInt(3) + 1;
                        StringBuilder dishList = new StringBuilder();
                        double totalPrice = 0;
                        
                        // 随机选择主菜品(用于dish_id字段)
                        int mainDishId = dishIds.get(random.nextInt(dishIds.size()));
                        int mainDishQuantity = random.nextInt(3) + 1;
                        
                        // 构建dish_list
                        List<Integer> selectedDishes = new ArrayList<>();
                        selectedDishes.add(mainDishId);
                        dishList.append(mainDishId).append(":").append(mainDishQuantity);
                        totalPrice += dishPrices.get(mainDishId) * mainDishQuantity;
                        System.out.println("selectedDishes: " + selectedDishes);
                        // 添加其他菜品
                        for (int j = 1; j < dishCount; j++) {
                            int dishId;
                            do {
                                dishId = dishIds.get(random.nextInt(dishIds.size()));
                            } while (selectedDishes.contains(dishId));
                            
                            selectedDishes.add(dishId);
                            int quantity = random.nextInt(3) + 1;
                            dishList.append(",").append(dishId).append(":").append(quantity);
                            totalPrice += dishPrices.get(dishId) * quantity;
                        }
                        System.out.println("totalPrice: " + totalPrice);
                        // 设置订单时间(当天随机时间)
                        calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(14) + 8); // 8:00 - 22:00
                        calendar.set(Calendar.MINUTE, random.nextInt(60));
                        
                        pstmt.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                        pstmt.setInt(2, random.nextInt(5) + 1); // 随机学生ID(1-5)
                        pstmt.setInt(3, merchantId);
                        pstmt.setString(4, dishList.toString());
                        pstmt.setDouble(5, totalPrice);
                        pstmt.setString(6, "completed"); // 已完成的订单
                        pstmt.setInt(7, mainDishId); // 主菜品ID
                        pstmt.setString(8, random.nextBoolean() ? "delivery" : "pickup"); // 随机配送方式
                        pstmt.setInt(9, mainDishQuantity);
                        System.out.println("Generated SQL: " + pstmt.toString());
                        pstmt.addBatch();
                    }
                    
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // 下一天
                }
                // 打印psmt
                System.out.println("Generated SQL: " + pstmt.toString());
                pstmt.executeBatch();
                return true;
                
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Generated SQL: " + sql);
                return false;
            }
        }, callback);
    }

    // 获取商家销售统计数据
    public void getMerchantSalesStats(int merchantId, DatabaseCallback<List<SalesStats>> callback) {
        executeDbOperation(connection -> {
            // 首先获取商家所有菜品信息用于名称映射
            Map<Integer, String> dishNames = new HashMap<>();
            String dishSql = "SELECT dish_id, name FROM dishes WHERE merchant_id = ?";
            try (PreparedStatement dishStmt = connection.prepareStatement(dishSql)) {
                dishStmt.setInt(1, merchantId);
                ResultSet dishRs = dishStmt.executeQuery();
                while (dishRs.next()) {
                    dishNames.put(dishRs.getInt("dish_id"), dishRs.getString("name"));
                }
            }

            // 获取最近7天的订单，按天统计
            String sql = "SELECT DATE(order_time) as sale_date, dish_list " +
                    "FROM orders " +
                    "WHERE merchant_id = ? " +
                    "AND order_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    "AND order_status = 'completed' " +
                    "ORDER BY sale_date ASC";

            List<SalesStats> statsList = new ArrayList<>();
            Map<String, Map<Integer, Integer>> dailySales = new LinkedHashMap<>(); // 使用LinkedHashMap保持日期顺序
            
            // 初始化最近7天的日期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -6); // 从6天前开始
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < 7; i++) {
                String date = dateFormat.format(calendar.getTime());
                dailySales.put(date, new HashMap<>());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, merchantId);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    String saleDate = rs.getString("sale_date");
                    String dishList = rs.getString("dish_list");
                    
                    // 解析dish_list
                    String[] items = dishList.split(",");
                    for (String item : items) {
                        String[] parts = item.split(":");
                        int dishId = Integer.parseInt(parts[0]);
                        int quantity = Integer.parseInt(parts[1]);
                        
                        // 按日期累加每个菜品的销量
                        Map<Integer, Integer> dayStats = dailySales.get(saleDate);
                        if (dayStats != null) {
                            dayStats.merge(dishId, quantity, Integer::sum);
                        }
                    }
                }
                
                // 转换数据格式
                for (Map.Entry<String, Map<Integer, Integer>> dateEntry : dailySales.entrySet()) {
                    String date = dateEntry.getKey();
                    Map<Integer, Integer> dishSales = dateEntry.getValue();
                    
                    // 为每个菜品创建一个统计项
                    for (Map.Entry<Integer, String> dishEntry : dishNames.entrySet()) {
                        int dishId = dishEntry.getKey();
                        String dishName = dishEntry.getValue();
                        int quantity = dishSales.getOrDefault(dishId, 0);
                        
                        SalesStats stats = new SalesStats(dishName, quantity, 0);
                        stats.setOrderDate(date);
                        stats.setDishId(dishId);
                        statsList.add(stats);
                    }
                }
                
                return statsList;
            }
        }, callback);
    }

    /**
     * 获取最近7天的菜品销售数据
     * @param merchantId 商家ID
     * @param callback 回调接口，返回List<Map<String, Object>>，每个Map包含:
     *                - date: 日期
     *                - dishName: 菜品名称
     *                - quantity: 销售数量
     */
    public void getWeekDishSales(int merchantId, DatabaseCallback<List<Map<String, Object>>> callback) {
        executeDbOperation(connection -> {
            List<Map<String, Object>> resultList = new ArrayList<>();
            
            // 1. 首先获取商家所有菜品的名称映射
            Map<Integer, String> dishNames = new HashMap<>();
            String dishSql = "SELECT dish_id, name FROM dishes WHERE merchant_id = ?";
            try (PreparedStatement dishStmt = connection.prepareStatement(dishSql)) {
                dishStmt.setInt(1, merchantId);
                ResultSet dishRs = dishStmt.executeQuery();
                while (dishRs.next()) {
                    int dishId = dishRs.getInt("dish_id");
                    String name = dishRs.getString("name");
                    dishNames.put(dishId, name);
                    System.out.println("找到菜品: ID=" + dishId + ", 名称=" + name);
                }
            }
            
            // 2. 先检查所有订单状态
            String checkStatusSql = "SELECT DISTINCT order_status FROM orders WHERE merchant_id = ?";
            try (PreparedStatement statusStmt = connection.prepareStatement(checkStatusSql)) {
                statusStmt.setInt(1, merchantId);
                ResultSet statusRs = statusStmt.executeQuery();
                System.out.println("\n当前所有订单状态：");
                while (statusRs.next()) {
                    System.out.println("订单状态: " + statusRs.getString("order_status"));
                }
            }
            

        /*    String orderSql = "SELECT DATE(order_time) as sale_date, dish_list, order_status " +
                    "FROM orders " +
                    "WHERE merchant_id = ? " +
                    "AND order_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    "AND order_time <= DATE_ADD(CURDATE(), INTERVAL 1 DAY) " +
                    "ORDER BY sale_date ASC";*/

            String orderSql = "SELECT DATE(order_time) as sale_date, dish_list, order_status " +
                    "FROM orders " +
                    "WHERE merchant_id = ? " +
                    "ORDER BY sale_date ASC";

            System.out.println("\n执行的SQL查询: " + orderSql);
            System.out.println("商家ID: " + merchantId);
                
            try (PreparedStatement pstmt = connection.prepareStatement(orderSql)) {
                pstmt.setInt(1, merchantId);
                ResultSet rs = pstmt.executeQuery();
                
                // 初始化7天的日期列表（包括今天）
                List<String> dates = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -6); // 从6天前开始
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                
                System.out.println("\n预期的7天日期范围：");
                for (int i = 0; i < 7; i++) {
                    String date = dateFormat.format(calendar.getTime());
                    dates.add(date);
                    System.out.println("第" + (i+1) + "天: " + date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                
                System.out.println("\n开始处理订单数据：");
                while (rs.next()) {
                    String saleDate = rs.getString("sale_date");
                    String dishList = rs.getString("dish_list");
                    String orderStatus = rs.getString("order_status");
                    System.out.println("\n处理订单 - 日期: " + saleDate + ", 状态: " + orderStatus + ", 菜品列表: " + dishList);
                    
                    // 解析dish_list
                    String[] items = dishList.split(",");
                    for (String item : items) {
                        String[] parts = item.split(":");
                        int dishId = Integer.parseInt(parts[0]);
                        int quantity = Integer.parseInt(parts[1]);
                        String dishName = dishNames.getOrDefault(dishId, "未知菜品");
                        
                        System.out.println("  - 菜品: " + dishName + "(ID:" + dishId + "), 数量: " + quantity);
                        
                        // 创建结果对象
                        Map<String, Object> salesData = new HashMap<>();
                        salesData.put("date", saleDate);
                        salesData.put("dishName", dishName);
                        salesData.put("quantity", quantity);
                        salesData.put("dishId", dishId);
                        salesData.put("orderStatus", orderStatus);
                        
                        resultList.add(salesData);
                    }
                }
                System.out.println("\n最终数据条数: " + resultList.size());
                return resultList;
            }
        }, callback);
    }

    // 获取所有商家
    public void getAllMerchants(DatabaseCallback<List<MerchantBean>> callback) {
        executeDbOperation(connection -> {
            String sql = "SELECT * FROM merchants ORDER BY merchant_id";
            List<MerchantBean> merchants = new ArrayList<>();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    MerchantBean merchant = new MerchantBean();
                    merchant.setMerchantId(rs.getInt("merchant_id"));
                    merchant.setName(rs.getString("name"));
                    merchant.setWindowLocation(rs.getString("window_location"));
                    merchant.setBusinessHours(rs.getString("business_hours"));
                    merchant.setCategory(rs.getString("category"));
                    merchants.add(merchant);
                }
                return merchants;
            }
        }, callback);
    }

    // 更新商家密码
    public void updateMerchantPassword(int merchantId, String newPassword, DatabaseCallback<Boolean> callback) {
        executeDbOperation(connection -> {
            String sql = "UPDATE merchants SET password = ? WHERE merchant_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, newPassword);
                pstmt.setInt(2, merchantId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        }, callback);
    }
}
