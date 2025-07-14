package com.example.booktracker.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.booktracker.entity.Book;
import com.example.booktracker.entity.Order;
import com.example.booktracker.utils.DateUtils;
import com.example.booktracker.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("Range")
public class OrderDB {

    // 表名常量，避免重复使用字符串
    private static final String TABLE_NAME = "t_order";

    /**
     * 添加订单
     */
    public static BusinessResult<Order> addOrder(Order order) {
        BusinessResult<Order> result = new BusinessResult<>();
        if (order == null) {
            result.setSuccess(false);
            result.setMessage("订单信息不能为空");
            return result;
        }
        if (order.getUserId() == null) {
            result.setSuccess(false);
            result.setMessage("用户ID不能为空");
            return result;
        }
        if (order.getBookId() == null) {
            result.setSuccess(false);
            result.setMessage("图书ID不能为空");
            return result;
        }
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            result.setSuccess(false);
            result.setMessage("购买数量必须大于0");
            return result;
        }

        // 查询图书是否存在且库存充足
        BusinessResult<Book> bookResult = BookDB.getBookById(order.getBookId());
        if (!bookResult.isSuccess()) {
            result.setSuccess(false);
            result.setMessage(bookResult.getMessage());
            return result;
        }
        Book book = bookResult.getData();
        if (book.getRemain() < order.getQuantity()) {
            result.setSuccess(false);
            result.setMessage("图书库存不足");
            return result;
        }

        // 更新图书库存
        book.setRemain(book.getRemain() - order.getQuantity());
        BusinessResult<Book> updateResult = BookDB.updateBook(book);
        if (!updateResult.isSuccess()) {
            result.setSuccess(false);
            result.setMessage("创建订单失败：" + updateResult.getMessage());
            return result;
        }

        // 设置订单创建时间和状态
        order.setCreateTime(DateUtils.getNowDate());
        order.setStatus(0); // 0-待发货

        try {
            // 插入订单
            SQLiteDatabase db = SqliteUtils.getInstance().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", order.getUserId());
            values.put("book_id", order.getBookId());
            values.put("book_name", order.getBookName());
            values.put("book_url", order.getBookUrl());
            values.put("quantity", order.getQuantity());
            values.put("create_time", order.getCreateTime());
            values.put("status", order.getStatus());

            // 使用新的表名
            long id = db.insert(TABLE_NAME, null, values);
            if (id > 0) {
                order.setId((int) id);
                result.setSuccess(true);
                result.setMessage("订单创建成功");
                result.setData(order);
            } else {
                // 如果订单创建失败，需要恢复图书库存
                book.setRemain(book.getRemain() + order.getQuantity());
                BookDB.updateBook(book);
                
                result.setSuccess(false);
                result.setMessage("订单创建失败");
            }
        } catch (Exception e) {
            // 发生异常，恢复图书库存并返回错误信息
            book.setRemain(book.getRemain() + order.getQuantity());
            BookDB.updateBook(book);
            
            result.setSuccess(false);
            result.setMessage("订单创建失败: " + e.getMessage());
            e.printStackTrace(); // 打印详细错误信息以便调试
        }

        return result;
    }

    /**
     * 查询用户订单列表
     */
    public static BusinessResult<List<Order>> getOrdersByUserId(int userId, Integer status) {
        BusinessResult<List<Order>> result = new BusinessResult<>();
        SQLiteDatabase db = SqliteUtils.getInstance().getReadableDatabase();
        
        List<Order> orderList = new ArrayList<>();
        String selection = "user_id=?";
        String[] selectionArgs;
        
        if (status != null) {
            selection += " AND status=?";
            selectionArgs = new String[]{String.valueOf(userId), String.valueOf(status)};
        } else {
            selectionArgs = new String[]{String.valueOf(userId)};
        }
        
        try {
            // 使用新的表名
            Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, "_id DESC");
            while (cursor.moveToNext()) {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                order.setBookId(cursor.getInt(cursor.getColumnIndex("book_id")));
                order.setBookName(cursor.getString(cursor.getColumnIndex("book_name")));
                order.setBookUrl(cursor.getString(cursor.getColumnIndex("book_url")));
                order.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                order.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                order.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                orderList.add(order);
            }
            cursor.close();
            
            result.setSuccess(true);
            result.setMessage("查询成功");
            result.setData(orderList);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("查询失败: " + e.getMessage());
            result.setData(new ArrayList<>());
            e.printStackTrace(); // 打印详细错误信息以便调试
        }
        
        return result;
    }

    /**
     * 更新订单状态
     */
    public static BusinessResult<Void> updateOrderStatus(int orderId, int status) {
        BusinessResult<Void> result = new BusinessResult<>();
        
        if (status < 0 || status > 2) {
            result.setSuccess(false);
            result.setMessage("无效的订单状态");
            return result;
        }
        
        // 查询订单是否存在
        Order order = getOrderById(orderId);
        if (order == null) {
            result.setSuccess(false);
            result.setMessage("订单不存在");
            return result;
        }
        
        try {
            // 更新订单状态
            SQLiteDatabase db = SqliteUtils.getInstance().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("status", status);
            
            // 使用新的表名
            int count = db.update(TABLE_NAME, values, "_id=?", new String[]{String.valueOf(orderId)});
            if (count > 0) {
                result.setSuccess(true);
                result.setMessage("订单状态更新成功");
            } else {
                result.setSuccess(false);
                result.setMessage("订单状态更新失败");
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("更新失败: " + e.getMessage());
            e.printStackTrace(); // 打印详细错误信息以便调试
        }
        
        return result;
    }

    /**
     * 根据ID获取订单
     */
    private static Order getOrderById(int orderId) {
        SQLiteDatabase db = SqliteUtils.getInstance().getReadableDatabase();
        Order order = null;
        
        try {
            // 使用新的表名
            Cursor cursor = db.query(TABLE_NAME, null, "_id=?", new String[]{String.valueOf(orderId)}, null, null, null);
            if (cursor.moveToNext()) {
                order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                order.setBookId(cursor.getInt(cursor.getColumnIndex("book_id")));
                order.setBookName(cursor.getString(cursor.getColumnIndex("book_name")));
                order.setBookUrl(cursor.getString(cursor.getColumnIndex("book_url")));
                order.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                order.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
                order.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            order = null;
        }
        
        return order;
    }
} 