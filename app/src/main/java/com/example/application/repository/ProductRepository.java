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
        products.add(new Product(1, "复古跑鞋", "舒适透气的复古设计跑鞋", 499.0, "https://placehold.co/600x400/E2E2E2/000?text=跑鞋"));
        products.add(new Product(2, "简约帆布包", "大容量，适合日常通勤", 189.0, "https://placehold.co/600x400/E2E2E2/000?text=帆布包"));
        products.add(new Product(3, "飞行员夹克", "经典款式，防风保暖", 799.0, "https://placehold.co/600x400/E2E2E2/000?text=夹克"));
        products.add(new Product(4, "纯棉T恤", "柔软亲肤，百搭基础款", 99.0, "https://placehold.co/600x400/E2E2E2/000?text=T恤"));
        products.add(new Product(5, "智能手表", "健康监测，时尚外观", 1299.0, "https://placehold.co/600x400/E2E2E2/000?text=手表"));
        products.add(new Product(6, "牛仔裤", "修身剪裁，弹力舒适", 399.0, "https://placehold.co/600x400/E2E2E2/000?text=牛仔裤"));
        products.add(new Product(7, "太阳镜", "UV400防护，潮流必备", 259.0, "https://placehold.co/600x400/E2E2E2/000?text=太阳镜"));
        products.add(new Product(8, "针织帽", "保暖有型，秋冬搭配利器", 79.0, "https://placehold.co/600x400/E2E2E2/000?text=针织帽"));
        return products;
    }
}