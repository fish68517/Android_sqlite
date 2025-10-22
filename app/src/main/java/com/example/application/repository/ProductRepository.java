package com.example.application.repository;// =================================================================================
// 文件路径: app/src/main/java/com/example/stylehub/data/repository/ProductRepository.java
// 任务: MVVM 架构 - 级别 3
// 描述: Repository层，作为数据来源的唯一入口。它负责决定是从网络(模拟)还是从本地数据库获取数据。
// =================================================================================


import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.application.model.AppDatabase;
import com.example.application.model.Product;
import com.example.application.model.ProductDao;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final ProductDao mProductDao;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mProductDao = db.productDao();
    }

    public LiveData<List<Product>> getAllProducts() {
        // 模拟从网络获取数据并存入数据库
        refreshProducts();
        return mProductDao.getAllProducts();
    }

    private void refreshProducts() {
        // 实际应用中这里会是网络请求。我们在这里用模拟数据代替。
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 先清空旧数据
            mProductDao.deleteAll();
            // 插入新的模拟数据
            mProductDao.insertAll(getMockProducts());
        });
    }

    // 模拟网络数据
    private List<Product> getMockProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Retro Running Shoes", "Comfortable and breathable retro design running shoes.", 89.99, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80"));
        //products.add(new Product(2, "Simple Canvas Bag", "Large capacity, suitable for daily commuting.", 29.99, "https://images.unsplash.com/photo-1591561939836-54dd3e2715b7?w=600&q=80"));
        //products.add(new Product(3, "Aviator Jacket", "Classic style, windproof and warm.", 129.99, "https://images.unsplash.com/photo-1576435728678-684f21142092?w=600&q=80"));
        products.add(new Product(4, "Cotton T-Shirt", "Soft and skin-friendly, a versatile basic item.", 19.99, "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&q=80"));
        products.add(new Product(5, "Smart Watch", "Health monitoring with a stylish look.", 249.00, "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&q=80"));
        //products.add(new Product(6, "Slim-fit Jeans", "Slim tailoring, stretchy and comfortable.", 59.99, "https://images.unsplash.com/photo-1602293589914-9FF0554c679c?w=600&q=80"));
        products.add(new Product(7, "Sunglasses", "UV400 protection, a trendy must-have.", 45.50, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600&q=80"));
        //products.add(new Product(8, "Knit Hat", "Warm and stylish, a great accessory for autumn and winter.", 15.00, "https://images.unsplash.com/photo-1575428652377-a3d80e281498?w=600&q=80"));
        return products;
    }
}