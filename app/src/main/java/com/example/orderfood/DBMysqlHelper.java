package com.example.orderfood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;

import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.MerchantBean;
import com.example.orderfood.model.Orders;
import com.example.orderfood.model.Post;
import com.example.orderfood.model.Student;
import com.example.orderfood.model.UserAddress;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DBMysqlHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "foodorder.db";
    private static final int DATABASE_VERSION = 1;
    private static DBMysqlHelper instance = null;
    private final Context context;

    private final ExecutorService executorService;
    private static final int THREAD_POOL_SIZE = 5; // 可以根据需求调整线程池大小
    private final Handler mainHandler;

    private DBMysqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
        // 初始化线程池
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        // 初始化主线程Handler
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized DBMysqlHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBMysqlHelper(context.getApplicationContext());
        }
        return instance;
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // 从 res/raw 读取并执行 SQL 文件
            InputStream inputStream = context.getResources().openRawResource(R.raw.sqlite_open_helper);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // 忽略注释
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                sql.append(line);
                if (line.trim().endsWith(";")) {
                    try {
                        db.execSQL(sql.toString());
                    } catch (Exception e) {
                        System.err.println("Failed to execute SQL: " + sql.toString());
                        e.printStackTrace();
                    }
                    sql.setLength(0); // 清空
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read SQL file for database creation", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单处理：删除所有表并重新创建
        // 在实际应用中，你可能需要更复杂的迁移策略
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                tables.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();

        for (String table : tables) {
            if (!table.equalsIgnoreCase("android_metadata") && !table.equalsIgnoreCase("sqlite_sequence")) {
                db.execSQL("DROP TABLE IF EXISTS " + table);
            }
        }
        onCreate(db);
    }

    // 关闭线程池和数据库连接的方法
    public void shutdown() {
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

    public List<String> getImageUrisByPostId(int postId) {
        return null;
    }

    public void updatePostFavorite(int postId, int i) {

    }

    public List<Post> getAllPosts() {

        return null;
    }

    public long addPost(Post post) {
            return 0;
    }

    public void addImageUri(int postId, String string) {

    }


    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // 定义数据库操作接口
    private interface DatabaseOperation<T> {
        T execute(SQLiteDatabase connection);
    }

    // 使用线程池执行数据库操作的通用方法
    private <T> void executeDbOperation(DatabaseOperation<T> operation, DatabaseCallback<T> callback) {
        executorService.submit(() -> {
            try (SQLiteDatabase db = getWritableDatabase()) {
                T result = operation.execute(db);
                // 在主线程中执行成功回调
                if (callback != null) {
                    mainHandler.post(() -> callback.onSuccess(result));
                }
            } catch (Exception e) {
                // 在主线程中执行错误回调
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(e));
                }
            }
        });
    }


    // 重构后的注册方法
    public void registerStudent(Student student, DatabaseCallback<Student> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            values.put("name", student.getName());
            values.put("password", student.getPassword());
            values.put("contact_info", student.getContactInfo());

            long id = db.insert("students", null, values);
            if (id != -1) {
                student.setStudentId((int) id);
                return student;
            } else {
                throw new android.database.SQLException("Failed to insert student");
            }
        }, callback);
    }

    // 重构后的登录方法
    public void loginStudent(String name, String password, DatabaseCallback<Map<String, Object>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM students WHERE name = ? AND password = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{name, password});

            Map<String, Object> result = new HashMap<>();
            if (cursor.moveToFirst()) {
                String token = UUID.randomUUID().toString();
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow("student_id")));
                student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));

                result.put("token", token);
                result.put("student", new Gson().toJson(student));
            }
            cursor.close();
            return result;
        }, callback);
    }

    // 商家相关方法
    public void registerMerchant(MerchantBean merchant, DatabaseCallback<MerchantBean> callback) {
        executeDbOperation(db -> {

            // get last merchant id
            Cursor c = db.rawQuery("SELECT MAX(merchant_id) FROM merchants", null);
            int lastId = 0;
            if(c.moveToFirst()){
                lastId = c.getInt(0);
            }
            c.close();

            ContentValues values = new ContentValues();
            values.put("merchant_id", lastId + 1);
            values.put("name", merchant.getName());
            values.put("password", merchant.getPassword());
            values.put("window_location", merchant.getWindowLocation());
            values.put("business_hours", merchant.getBusinessHours());
            values.put("category", merchant.getCategory());
            values.put("content", "新店开业，欢迎光临！");
            values.put("browse_count", 0);
            values.put("sales", 0);

            long id = db.insert("merchants", null, values);
            if(id != -1){
                merchant.setMerchantId((int) id);
                return merchant;
            } else {
                throw new android.database.SQLException("Failed to insert merchant");
            }
        }, callback);
    }

    public void loginMerchant(String name, String password, DatabaseCallback<Map<String, String>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM merchants WHERE name = ? AND password = ?";
            Cursor rs = db.rawQuery(sql, new String[]{name, password});

            Map<String, String> result = new HashMap<>();
            if (rs.moveToFirst()) {
                String token = UUID.randomUUID().toString();
                MerchantBean merchant = new MerchantBean();
                merchant.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
                merchant.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
                merchant.setWindowLocation(rs.getString(rs.getColumnIndexOrThrow("window_location")));
                merchant.setBusinessHours(rs.getString(rs.getColumnIndexOrThrow("business_hours")));
                merchant.setPassword(rs.getString(rs.getColumnIndexOrThrow("password")));

                result.put("token", token);
                Gson gson = new Gson();
                result.put("merchant", gson.toJson(merchant));
            }
            rs.close();
            return result;
        }, callback);
    }



    // 菜品相关方法
    public void getAllDishes(DatabaseCallback<List<Dish>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM dishes";
            List<Dish> dishes = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, null);

            while (rs.moveToNext()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt(rs.getColumnIndexOrThrow("dish_id")));
                dish.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
                dish.setPrice(rs.getDouble(rs.getColumnIndexOrThrow("price")));
                dish.setImageUrl(rs.getString(rs.getColumnIndexOrThrow("image_url")));
                dish.setCategory(rs.getString(rs.getColumnIndexOrThrow("category")));
                dish.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
                dish.setDescription(rs.getString(rs.getColumnIndexOrThrow("description")));
                dish.setBrowseCount(rs.getInt(rs.getColumnIndexOrThrow("browse_count")));
                dish.setSales(rs.getInt(rs.getColumnIndexOrThrow("sales")));
                dish.setStock(rs.getInt(rs.getColumnIndexOrThrow("stock")));
                dish.setSpecifications(rs.getString(rs.getColumnIndexOrThrow("specifications")));
                dishes.add(dish);
            }
            rs.close();
            return dishes;
        }, callback);
    }

    private void updateDishBrowseCount(SQLiteDatabase db, int dishId) {
        db.execSQL("UPDATE dishes SET browse_count = browse_count + 1 WHERE dish_id = ?", new Object[]{dishId});
    }


    // 获取商家的订单列表
    public void getOrdersByMerchantId(int merchantId, DatabaseCallback<List<Orders>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM orders WHERE merchant_id = ? AND (order_status = 'completed' OR order_status = 'paid') ORDER BY order_time DESC";
            List<Orders> ordersList = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(merchantId)});

            while (rs.moveToNext()) {
                Orders order = new Orders();
                order.setOrderId(rs.getInt(rs.getColumnIndexOrThrow("order_id")));
                order.setOrderTime(rs.getString(rs.getColumnIndexOrThrow("order_time")));
                order.setStudentId(rs.getInt(rs.getColumnIndexOrThrow("student_id")));
                order.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
                order.setDishList(rs.getString(rs.getColumnIndexOrThrow("dish_list")));
                order.setTotalPrice(rs.getDouble(rs.getColumnIndexOrThrow("total_price")));
                order.setOrderStatus(rs.getString(rs.getColumnIndexOrThrow("order_status")));
                order.setDishId(rs.getInt(rs.getColumnIndexOrThrow("dish_id")));
                order.setDiningOption(rs.getString(rs.getColumnIndexOrThrow("dining_option")));
                order.setOrderQuantity(rs.getInt(rs.getColumnIndexOrThrow("order_quantity")));
                ordersList.add(order);
            }
            rs.close();
            return ordersList;
        }, callback);
    }

    // 获取商家信息
    public void getMerchantInfo(int id, DatabaseCallback<MerchantBean> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM merchants WHERE merchant_id = ?";
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(id)});
            if (rs.moveToFirst()) {
                MerchantBean merchant = getMerchantFromCursor(rs);
                 rs.close();
                return merchant;
            }
            rs.close();
            return null;
        }, callback);
    }

    // 下单
    public void placeOrder(Orders order, DatabaseCallback<Orders> callback) {
        executeDbOperation(db -> {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put("order_time", order.getOrderTime());
                values.put("student_id", order.getStudentId());
                values.put("merchant_id", order.getMerchantId());
                values.put("dish_list", order.getDishList());
                values.put("total_price", order.getTotalPrice());
                values.put("order_status", order.getOrderStatus());
                values.put("dish_id", order.getDishId());
                values.put("dining_option", order.getDiningOption());
                values.put("order_quantity", order.getOrderQuantity());

                long id = db.insert("orders", null, values);
                if (id != -1) {
                    order.setOrderId((int)id);
                }

                // 更新菜品销量和库存
                updateDishSalesAndStock(db, order.getDishId(), order.getOrderQuantity());
                db.setTransactionSuccessful();
                return order;
            } finally {
                db.endTransaction();
            }
        }, callback);
    }

    // 辅助方法：更新菜品销量和库存
    private void updateDishSalesAndStock(SQLiteDatabase db, int dishId, int quantity) {
        db.execSQL("UPDATE dishes SET sales = sales + ?, stock = stock - ? WHERE dish_id = ?",
                new Object[]{quantity, quantity, dishId});
    }


    public void getPurchasedMerchantsByCompletedOrders(int studentId,
                                                       DatabaseCallback<List<MerchantBean>> callback) {
        executeDbOperation(db -> {
            // 通过订单表关联商家表，获取用户购买过的商家，且订单状态为 "completed" 或 "paid"
            String sql = "SELECT DISTINCT m.* FROM merchants m " +
                    "INNER JOIN orders o ON m.merchant_id = o.merchant_id " +
                    "WHERE o.student_id = ? AND (o.order_status = 'completed' OR o.order_status = 'paid')";

            List<MerchantBean> merchants = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(studentId)});
            while (rs.moveToNext()) {
                merchants.add(getMerchantFromCursor(rs));
            }
            rs.close();
            return merchants;
        }, callback);
    }



    // 根据分类获取商家列表
    public void getMerchantsByCategory(String category, DatabaseCallback<List<MerchantBean>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM merchants WHERE category = ? ORDER BY browse_count DESC";
            List<MerchantBean> merchants = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, new String[]{category});
            while (rs.moveToNext()) {
                merchants.add(getMerchantFromCursor(rs));
            }
            rs.close();
            return merchants;
        }, callback);
    }

    private MerchantBean getMerchantFromCursor(Cursor rs) {
        MerchantBean merchant = new MerchantBean();
        merchant.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
        merchant.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
        merchant.setImageName(rs.getString(rs.getColumnIndexOrThrow("image_name")));
        merchant.setRating(rs.getDouble(rs.getColumnIndexOrThrow("rating")));
        merchant.setSales(rs.getInt(rs.getColumnIndexOrThrow("sales")));
        merchant.setMinPrice(rs.getDouble(rs.getColumnIndexOrThrow("min_price")));
        merchant.setDiscountInfo(rs.getString(rs.getColumnIndexOrThrow("discount_info")));
        int delivery = rs.getInt(rs.getColumnIndexOrThrow("delivery"));
        merchant.setDelivery(delivery == 1);
        merchant.setRemark(rs.getString(rs.getColumnIndexOrThrow("remark")));
        merchant.setCategory(rs.getString(rs.getColumnIndexOrThrow("category")));
        merchant.setWindowLocation(rs.getString(rs.getColumnIndexOrThrow("window_location")));
        merchant.setBusinessHours(rs.getString(rs.getColumnIndexOrThrow("business_hours")));
        merchant.setContent(rs.getString(rs.getColumnIndexOrThrow("content")));
        return merchant;
    }

    // 添加商品到购物车
    public void addToCart(int studentId, int merchantId, int dishId, int quantity, DatabaseCallback<Void> callback) {
        executeDbOperation(db -> {
            String checkSql = "SELECT quantity FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
            Cursor cursor = db.rawQuery(checkSql, new String[]{String.valueOf(studentId), String.valueOf(merchantId), String.valueOf(dishId)});

            if (cursor.moveToFirst()) {
                // 已存在，更新数量
                ContentValues values = new ContentValues();
                values.put("quantity", cursor.getInt(0) + quantity);
                db.update("orderrecords", values, "student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'",
                        new String[]{String.valueOf(studentId), String.valueOf(merchantId), String.valueOf(dishId)});

            } else {
                // 不存在，插入新记录
                ContentValues values = new ContentValues();
                values.put("student_id", studentId);
                values.put("merchant_id", merchantId);
                values.put("dish_id", dishId);
                values.put("quantity", quantity);
                values.put("status", "cart");
                values.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                db.insert("orderrecords", null, values);
            }
            cursor.close();
            return null;
        }, callback);
    }

    // 从购物车移除商品
    public void removeFromCart(int studentId, int merchantId, int dishId, int quantity, DatabaseCallback<Void> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            String quantitySql = "SELECT quantity FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
            Cursor cursor = db.rawQuery(quantitySql, new String[]{String.valueOf(studentId), String.valueOf(merchantId), String.valueOf(dishId)});
            if(cursor.moveToFirst()){
                int currentQuantity = cursor.getInt(0);
                if(currentQuantity - quantity > 0){
                    values.put("quantity", currentQuantity - quantity);
                    db.update("orderrecords", values, "student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'",
                            new String[]{String.valueOf(studentId), String.valueOf(merchantId), String.valueOf(dishId)});
                }else{
                     db.delete("orderrecords", "student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'",
                            new String[]{String.valueOf(studentId), String.valueOf(merchantId), String.valueOf(dishId)});
                }
            }
            cursor.close();
            return null;
        }, callback);
    }

    // 获取购物车中商品数量
    public void getCartItemQuantity(int studentId, int merchantId, int dishId, DatabaseCallback<Integer> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT quantity FROM orderrecords WHERE student_id = ? AND merchant_id = ? AND dish_id = ? AND status = 'cart'";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(studentId), String.valueOf(merchantId), String.valueOf(dishId)});
            int quantity = 0;
            if (cursor.moveToFirst()) {
                quantity = cursor.getInt(0);
            }
            cursor.close();
            return quantity;
        }, callback);
    }

    // 获取用户购物车中的所有商品
    public void getCartItems(int studentId, DatabaseCallback<List<CartItem>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT r.*, d.name as dish_name, d.price, d.image_url, m.name as merchant_name " +
                    "FROM orderrecords r " +
                    "JOIN dishes d ON r.dish_id = d.dish_id " +
                    "JOIN merchants m ON r.merchant_id = m.merchant_id " +
                    "WHERE r.student_id = ? AND r.status = 'cart' " +
                    "ORDER BY r.merchant_id, r.create_time";
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(studentId)});
            List<CartItem> cartItems = new ArrayList<>();
            while (rs.moveToNext()) {
                CartItem item = new CartItem();
                item.setRecordId(rs.getInt(rs.getColumnIndexOrThrow("record_id")));
                item.setStudentId(rs.getInt(rs.getColumnIndexOrThrow("student_id")));
                item.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
                item.setDishId(rs.getInt(rs.getColumnIndexOrThrow("dish_id")));
                item.setQuantity(rs.getInt(rs.getColumnIndexOrThrow("quantity")));
                item.setDishName(rs.getString(rs.getColumnIndexOrThrow("dish_name")));
                item.setPrice(rs.getDouble(rs.getColumnIndexOrThrow("price")));
                item.setImageUrl(rs.getString(rs.getColumnIndexOrThrow("image_url")));
                item.setMerchantName(rs.getString(rs.getColumnIndexOrThrow("merchant_name")));
                cartItems.add(item);
            }
            rs.close();
            return cartItems;
        }, callback);
    }



    // 更新购物车商品数量
    public void updateCartItemQuantity(int recordId, int quantity, DatabaseCallback<Void> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            values.put("quantity", quantity);
            if(quantity > 0){
                db.update("orderrecords", values, "record_id = ? AND status = 'cart'", new String[]{String.valueOf(recordId)});
            } else {
                db.delete("orderrecords", "record_id = ? AND status = 'cart'", new String[]{String.valueOf(recordId)});
            }
            return null;
        }, callback);
    }



    // 获取用户地址
    public void getUserAddress(int userId, DatabaseCallback<UserAddress> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM user_address WHERE user_id = ? AND is_default = 1";
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(userId)});

            if (rs.moveToFirst()) {
                UserAddress address = new UserAddress();
                address.setId(rs.getInt(rs.getColumnIndexOrThrow("id")));
                address.setUserId(rs.getInt(rs.getColumnIndexOrThrow("user_id")));
                address.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
                address.setPhone(rs.getString(rs.getColumnIndexOrThrow("phone")));
                address.setAddress(rs.getString(rs.getColumnIndexOrThrow("address")));
                address.setDefault(rs.getInt(rs.getColumnIndexOrThrow("is_default")) == 1);
                rs.close();
                return address;
            }
            rs.close();
            return null;
        }, callback);
    }

    // 获取用户所有地址
    public void getUserAddresses(int userId, DatabaseCallback<List<UserAddress>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM user_address WHERE user_id = ? ORDER BY is_default DESC";
            List<UserAddress> addresses = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(userId)});
            while (rs.moveToNext()) {
                UserAddress address = new UserAddress();
                address.setId(rs.getInt(rs.getColumnIndexOrThrow("id")));
                address.setUserId(rs.getInt(rs.getColumnIndexOrThrow("user_id")));
                address.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
                address.setPhone(rs.getString(rs.getColumnIndexOrThrow("phone")));
                address.setAddress(rs.getString(rs.getColumnIndexOrThrow("address")));
                address.setDefault(rs.getInt(rs.getColumnIndexOrThrow("is_default")) == 1);
                addresses.add(address);
            }
            rs.close();
            return addresses;
        }, callback);
    }

    // 添加新地址
    public void addUserAddress(UserAddress address, DatabaseCallback<UserAddress> callback) {
        executeDbOperation(db -> {
            db.beginTransaction();
            try {
                // 如果新地址是默认地址，先将其他地址设为非默认
                if (address.isDefault()) {
                    ContentValues defaultValues = new ContentValues();
                    defaultValues.put("is_default", 0);
                    db.update("user_address", defaultValues, "user_id = ?", new String[]{String.valueOf(address.getUserId())});
                }

                ContentValues values = new ContentValues();
                values.put("user_id", address.getUserId());
                values.put("name", address.getName());
                values.put("phone", address.getPhone());
                values.put("address", address.getAddress());
                values.put("is_default", address.isDefault() ? 1 : 0);

                long id = db.insert("user_address", null, values);
                address.setId((int) id);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            return address;
        }, callback);
    }

    // 更新地址
    public void updateUserAddress(UserAddress address, DatabaseCallback<UserAddress> callback) {
        executeDbOperation(db -> {
            db.beginTransaction();
            try {
                // 如果更新为默认地址，先将其他地址设为非默认
                if (address.isDefault()) {
                    ContentValues defaultValues = new ContentValues();
                    defaultValues.put("is_default", 0);
                    db.update("user_address", defaultValues, "user_id = ? AND id != ?", new String[]{String.valueOf(address.getUserId()), String.valueOf(address.getId())});
                }

                ContentValues values = new ContentValues();
                values.put("name", address.getName());
                values.put("phone", address.getPhone());
                values.put("address", address.getAddress());
                values.put("is_default", address.isDefault() ? 1 : 0);
                db.update("user_address", values, "id = ?", new String[]{String.valueOf(address.getId())});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            return address;
        }, callback);
    }

    // 删除地址
    public void deleteUserAddress(int addressId, DatabaseCallback<Void> callback) {
        executeDbOperation(db -> {
            db.delete("user_address", "id = ?", new String[]{String.valueOf(addressId)});
            return null;
        }, callback);
    }

    // 批量删除购物车商品
    public void removeCartItems(List<Integer> recordIds, DatabaseCallback<Void> callback) {
        executeDbOperation(db -> {
            if (recordIds == null || recordIds.isEmpty()) {
                return null;
            }

            String[] ids = new String[recordIds.size()];
            for (int i = 0; i < recordIds.size(); i++) {
                ids[i] = String.valueOf(recordIds.get(i));
            }
            db.delete("orderrecords", "record_id IN (" + new String(new char[recordIds.size()-1]).replace("\0", "?,") + "?)", ids);
            return null;
        }, callback);
    }

    // 获取学生的所有订单
    public void getOrdersByStudent(int studentId, DatabaseCallback<List<Orders>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM orders WHERE student_id = ? ORDER BY order_time DESC";
            List<Orders> ordersList = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(studentId)});

            while (rs.moveToNext()) {
                Orders order = new Orders();
                order.setOrderId(rs.getInt(rs.getColumnIndexOrThrow("order_id")));
                order.setOrderTime(rs.getString(rs.getColumnIndexOrThrow("order_time")));
                order.setStudentId(rs.getInt(rs.getColumnIndexOrThrow("student_id")));
                order.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
                order.setDishList(rs.getString(rs.getColumnIndexOrThrow("dish_list")));
                order.setTotalPrice(rs.getDouble(rs.getColumnIndexOrThrow("total_price")));
                order.setOrderStatus(rs.getString(rs.getColumnIndexOrThrow("order_status")));
                order.setDishId(rs.getInt(rs.getColumnIndexOrThrow("dish_id")));
                order.setDiningOption(rs.getString(rs.getColumnIndexOrThrow("dining_option")));
                order.setOrderQuantity(rs.getInt(rs.getColumnIndexOrThrow("order_quantity")));
                ordersList.add(order);
            }
            rs.close();
            return ordersList;
        }, callback);
    }

    // 更新订单状态
    public void updateOrderStatus(int orderId, String status, DatabaseCallback<Void> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            values.put("order_status", status);
            db.update("orders", values, "order_id = ?", new String[]{String.valueOf(orderId)});
            return null;
        }, callback);
    }



    // 获取菜品信息
    public void getDishInfo(int dishId, DatabaseCallback<Dish> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM dishes WHERE dish_id = ?";
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(dishId)});
            if (rs.moveToFirst()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt(rs.getColumnIndexOrThrow("dish_id")));
                dish.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
                dish.setPrice(rs.getDouble(rs.getColumnIndexOrThrow("price")));
                dish.setImageUrl(rs.getString(rs.getColumnIndexOrThrow("image_url")));
                dish.setDescription(rs.getString(rs.getColumnIndexOrThrow("description")));
                dish.setStock(rs.getInt(rs.getColumnIndexOrThrow("stock")));
                dish.setSales(rs.getInt(rs.getColumnIndexOrThrow("sales")));
                dish.setMerchantName(rs.getString(rs.getColumnIndexOrThrow("merchant_name")));
                rs.close();
                return dish;
            }
            rs.close();
            return null;
        }, callback);
    }

    // 获取商家的所有菜品
    public void getMerchantDishes(int merchantId, DatabaseCallback<List<Dish>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM dishes WHERE merchant_id = ? ORDER BY sales DESC";
            List<Dish> dishes = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, new String[]{String.valueOf(merchantId)});
            while (rs.moveToNext()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt(rs.getColumnIndexOrThrow("dish_id")));
                dish.setName(rs.getString(rs.getColumnIndexOrThrow("name")));
                dish.setPrice(rs.getDouble(rs.getColumnIndexOrThrow("price")));
                dish.setImageUrl(rs.getString(rs.getColumnIndexOrThrow("image_url")));
                dish.setDescription(rs.getString(rs.getColumnIndexOrThrow("description")));
                dish.setStock(rs.getInt(rs.getColumnIndexOrThrow("stock")));
                dish.setSales(rs.getInt(rs.getColumnIndexOrThrow("sales")));
                dish.setMerchantId(rs.getInt(rs.getColumnIndexOrThrow("merchant_id")));
                dish.setCategory(rs.getString(rs.getColumnIndexOrThrow("category")));
                dish.setMerchantName(rs.getString(rs.getColumnIndexOrThrow("merchant_name")));
                dishes.add(dish);
            }
            rs.close();
            return dishes;
        }, callback);
    }

    // 添加新菜品
    public void addDish(Dish dish, DatabaseCallback<Dish> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            values.put("name", dish.getName());
            values.put("price", dish.getPrice());
            values.put("image_url", dish.getImageUrl());
            values.put("category", dish.getCategory());
            values.put("merchant_id", dish.getMerchantId());
            values.put("browse_count", dish.getBrowseCount());
            values.put("description", dish.getDescription());
            values.put("sales", dish.getSales());
            values.put("stock", dish.getStock());
            values.put("specifications", dish.getSpecifications());
            values.put("merchant_name", dish.getMerchantName());

            long id = db.insert("dishes", null, values);
            dish.setDishId((int)id);
            return dish;
        }, callback);
    }

    // 更新菜品信息
    public void updateDish(Dish dish, DatabaseCallback<Dish> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            values.put("name", dish.getName());
            values.put("price", dish.getPrice());
            values.put("image_url", dish.getImageUrl());
            values.put("description", dish.getDescription());
            values.put("stock", dish.getStock());
            values.put("category", dish.getCategory());

            int affectedRows = db.update("dishes", values, "dish_id = ? AND merchant_id = ?",
                    new String[]{String.valueOf(dish.getDishId()), String.valueOf(dish.getMerchantId())});
            return affectedRows > 0 ? dish : null;
        }, callback);
    }

    // 删除菜品
    public void deleteDish(int dishId, int merchantId, DatabaseCallback<Boolean> callback) {
        executeDbOperation(db -> {
            int affectedRows = db.delete("dishes", "dish_id = ? AND merchant_id = ?",
                    new String[]{String.valueOf(dishId), String.valueOf(merchantId)});
            return affectedRows > 0;
        }, callback);
    }

    // 生成模拟订单数据
    public void generateMockOrders(int merchantId, DatabaseCallback<Boolean> callback) {
        executeDbOperation(db -> {
            db.beginTransaction();
            try {
                // 获取商家的所有菜品
                String dishSql = "SELECT dish_id, price FROM dishes WHERE merchant_id = ?";
                Cursor dishRs = db.rawQuery(dishSql, new String[]{String.valueOf(merchantId)});

                List<Integer> dishIds = new ArrayList<>();
                Map<Integer, Double> dishPrices = new HashMap<>();
                while (dishRs.moveToNext()) {
                    int dishId = dishRs.getInt(dishRs.getColumnIndexOrThrow("dish_id"));
                    dishIds.add(dishId);
                    dishPrices.put(dishId, dishRs.getDouble(dishRs.getColumnIndexOrThrow("price")));
                }
                dishRs.close();
                if (dishIds.isEmpty()) {
                    return false;
                }

                // 生成过去7天的订单
                Random random = new Random();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -7); // 从7天前开始

                for (int day = 0; day < 7; day++) {
                    int ordersPerDay = random.nextInt(8) + 3;

                    for (int i = 0; i < ordersPerDay; i++) {
                        int dishCount = random.nextInt(3) + 1;
                        StringBuilder dishListStr = new StringBuilder();
                        double totalPrice = 0;

                        int mainDishId = dishIds.get(random.nextInt(dishIds.size()));
                        int mainDishQuantity = random.nextInt(3) + 1;

                        List<Integer> selectedDishes = new ArrayList<>();
                        selectedDishes.add(mainDishId);
                        dishListStr.append(mainDishId).append(":").append(mainDishQuantity);
                        totalPrice += dishPrices.get(mainDishId) * mainDishQuantity;

                        for (int j = 1; j < dishCount; j++) {
                            int dishId;
                            do {
                                dishId = dishIds.get(random.nextInt(dishIds.size()));
                            } while (selectedDishes.contains(dishId));
                            selectedDishes.add(dishId);
                            int quantity = random.nextInt(3) + 1;
                            dishListStr.append(",").append(dishId).append(":").append(quantity);
                            totalPrice += dishPrices.get(dishId) * quantity;
                        }

                        calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(14) + 8);
                        calendar.set(Calendar.MINUTE, random.nextInt(60));

                        ContentValues values = new ContentValues();
                        values.put("order_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                        values.put("student_id", random.nextInt(5) + 1);
                        values.put("merchant_id", merchantId);
                        values.put("dish_list", dishListStr.toString());
                        values.put("total_price", totalPrice);
                        values.put("order_status", "completed");
                        values.put("dish_id", mainDishId);
                        values.put("dining_option", random.nextBoolean() ? "delivery" : "pickup");
                        values.put("order_quantity", mainDishQuantity);
                        db.insert("orders", null, values);
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                db.setTransactionSuccessful();
                return true;
            } finally {
                db.endTransaction();
            }
        }, callback);
    }

    public void getWeekDishSales(int merchantId, DatabaseCallback<List<Map<String, Object>>> callback) {
        executeDbOperation(db -> {
            List<Map<String, Object>> resultList = new ArrayList<>();
            Map<Integer, String> dishNames = new HashMap<>();
            String dishSql = "SELECT dish_id, name FROM dishes WHERE merchant_id = ?";
            Cursor dishRs = db.rawQuery(dishSql, new String[]{String.valueOf(merchantId)});
            while (dishRs.moveToNext()) {
                dishNames.put(dishRs.getInt(0), dishRs.getString(1));
            }
            dishRs.close();

            String orderSql = "SELECT STRFTIME('%Y-%m-%d', order_time) as sale_date, dish_list, order_status " +
                    "FROM orders " +
                    "WHERE merchant_id = ? " +
                    "ORDER BY sale_date ASC";
            Cursor rs = db.rawQuery(orderSql, new String[]{String.valueOf(merchantId)});
            while (rs.moveToNext()) {
                String saleDate = rs.getString(rs.getColumnIndexOrThrow("sale_date"));
                String dishList = rs.getString(rs.getColumnIndexOrThrow("dish_list"));
                String orderStatus = rs.getString(rs.getColumnIndexOrThrow("order_status"));
                String[] items = dishList.split(",");
                for (String item : items) {
                    String[] parts = item.split(":");
                    int dishId = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);
                    String dishName = dishNames.getOrDefault(dishId, "未知菜品");

                    Map<String, Object> salesData = new HashMap<>();
                    salesData.put("date", saleDate);
                    salesData.put("dishName", dishName);
                    salesData.put("quantity", quantity);
                    salesData.put("dishId", dishId);
                    salesData.put("orderStatus", orderStatus);
                    resultList.add(salesData);
                }
            }
            rs.close();
            return resultList;
        }, callback);
    }

    // 获取所有商家
    public void getAllMerchants(DatabaseCallback<List<MerchantBean>> callback) {
        executeDbOperation(db -> {
            String sql = "SELECT * FROM merchants ORDER BY merchant_id";
            List<MerchantBean> merchants = new ArrayList<>();
            Cursor rs = db.rawQuery(sql, null);
            while (rs.moveToNext()) {
                merchants.add(getMerchantFromCursor(rs));
            }
            rs.close();
            return merchants;
        }, callback);
    }

    // 更新商家密码
    public void updateMerchantPassword(int merchantId, String newPassword, DatabaseCallback<Boolean> callback) {
        executeDbOperation(db -> {
            ContentValues values = new ContentValues();
            values.put("password", newPassword);
            int affectedRows = db.update("merchants", values, "merchant_id = ?", new String[]{String.valueOf(merchantId)});
            return affectedRows > 0;
        }, callback);
    }
} 