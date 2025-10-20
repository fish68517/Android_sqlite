package com.example.application.model;// =================================================================================
// 文件路径: app/src/main/java/com/example/stylehub/data/local/ProductDao.java
// 任务: 离线模式 (Offline Mode with Room) - 级别 3
// 描述: 数据访问对象(DAO)。定义了所有与products表交互的数据库操作。
// =================================================================================

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAllProducts();

    @Query("DELETE FROM products")
    void deleteAll();
}