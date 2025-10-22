package com.example.application.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar if using Material Toolbar


import com.example.application.R;
import com.example.application.adapter.ImageSliderAdapter;
import com.example.application.databinding.ActivityProductDetailBinding;
import com.example.application.model.Product;

import java.util.ArrayList; // Import ArrayList
import java.util.List;     // Import List
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    public static final String EXTRA_PRODUCT = "PRODUCT_EXTRA"; // Use constant

    // --- 新增代码 ---
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    // --- 新增代码结束 ---

    // --- 新增代码 ---
    @Override
    protected void onResume() {
        super.onResume();
        // 当页面可见时，开始自动轮播
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当页面不可见时，停止自动轮播，以防内存泄漏
        sliderHandler.removeCallbacks(sliderRunnable);
    }
    // --- 新增代码结束 ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar (Assuming you add a Toolbar in the layout or use the default ActionBar)
        Toolbar toolbar = findViewById(R.id.detail_toolbar); // Add a Toolbar with this ID to your layout
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        } else if (getSupportActionBar() != null) { // Fallback for default ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // 任务: 详细信息屏幕 - 级别 2
        // 描述: 从启动此Activity的Intent中获取传递过来的Product对象。
        Product product = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);

        if (product != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(product.getName()); // Set Toolbar title
            }

            // 任务: 详细信息屏幕 - 级别 2
            // 描述: 将Product对象的数据填充到UI控件中。
            binding.detailProductName.setText(product.getName());
            binding.detailProductPrice.setText(String.format(Locale.getDefault(), "¥ %.2f", product.getPrice()));
            binding.detailProductDescription.setText(product.getDescription());

            // **** SETUP ViewPager2 ****
            // 任务: 商品详情图片轮播 - 级别 (Custom Requirement)
            List<String> images = new ArrayList<>();
            images.add(product.getImageUrl()); // Add the single image URL to a list
            images.add(product.getImageUrl()); // Add the single image URL to a list
            // If your Product model had multiple images, you'd add them all here.
            ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(images);
            binding.detailProductImageSlider.setAdapter(sliderAdapter);
            // **** END ViewPager2 SETUP ****

            // --- 新增代码 ---
            // 初始化 Runnable 任务
            sliderRunnable = new Runnable() {
                @Override
                public void run() {
                    int currentItem = binding.detailProductImageSlider.getCurrentItem();
                    int nextItem = currentItem + 1;
                    // 如果已经是最后一页，则返回第一页
                    if (nextItem >= sliderAdapter.getItemCount()) {
                        nextItem = 0;
                    }
                    binding.detailProductImageSlider.setCurrentItem(nextItem, true); // true 表示平滑滚动

                    // 再次调度自己，实现循环
                    sliderHandler.postDelayed(this, 3000); // 3000毫秒 = 3秒
                }
            };
            // --- 新增代码结束 ---

        } else {
            // Handle case where product data is missing
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("商品详情");
            }
            binding.detailProductName.setText("商品未找到");
            // Maybe show an error message
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle the Up button press
        return true;
    }
}