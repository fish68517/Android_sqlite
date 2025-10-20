package com.example.application.viewmodel;// =================================================================================
// 文件路径: app/src/main/java/com/example/stylehub/viewmodel/ProductViewModel.java
// 任务: MVVM 架构 - 级别 3
// 描述: ViewModel层。它持有UI所需的数据(LiveData)，并在配置更改时存活。
// 它通过Repository获取数据，与UI完全解耦。
// =================================================================================


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.application.model.Product;
import com.example.application.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository mRepository;
    private final LiveData<List<Product>> mAllProducts;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ProductRepository(application);
        mAllProducts = mRepository.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return mAllProducts;
    }
}