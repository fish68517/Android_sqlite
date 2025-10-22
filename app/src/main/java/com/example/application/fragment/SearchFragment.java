package com.example.application.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.application.R;
import com.example.application.databinding.FragmentSearchBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    // --- 新增: 日志 TAG ---
    private static final String TAG = "SearchFragmentDebug";

    // 搜索建议
    private static final String[] SUGGESTIONS = new String[]{
            "Running Shoes", "Jacket", "T-Shirt", "Canvas Bag", "Smart Watch", "Jeans", "Sunglasses"
    };

    // 新增：创建一个Map来存储每个建议项对应的特定图片URL
    private final Map<String, String> specificImageUrls = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化图片URL映射
        initializeImageMap();

        // 任务: 使用建议进行搜索 - 级别 2
        // 描述: 创建一个ArrayAdapter并将它设置给 searchView，以提供搜索建议。
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);
        binding.searchView.setAdapter(adapter);

        binding.searchView.setOnItemClickListener((parent, view1, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            performSearch(selection);
        });

        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchView.getText().toString();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });
    }

    /**
     * 新增方法：初始化建议词与特定图片URL的映射
     */
    private void initializeImageMap() {
        specificImageUrls.put("Running Shoes", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80");
        specificImageUrls.put("Jacket", "https://images.unsplash.com/photo-1592878912950-7241ae6522c7?w=800&q=80");
        specificImageUrls.put("T-Shirt", "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800&q=80");
        specificImageUrls.put("Canvas Bag", "https://images.unsplash.com/photo-1591561939836-54dd3e2715b7?w=800&q=80");
        specificImageUrls.put("Smart Watch", "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=800&q=80");
        specificImageUrls.put("Jeans", "https://images.unsplash.com/photo-1602293589914-9FF0554c679c?w=800&q=80");
        specificImageUrls.put("Sunglasses", "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600&q=80");
    }

    private void performSearch(String query) {
        hideKeyboard();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchResultLayout.setVisibility(View.GONE);

        // --- 这是主要修改部分 ---
        String imageUrl;
        // 检查Map中是否存在该关键词的特定URL
        if (specificImageUrls.containsKey(query)) {
            // 如果存在，使用我们预设的、高质量的固定URL
            imageUrl = specificImageUrls.get(query);
        } else {
            // 如果不存在（用户输入了自定义内容），则回退到使用随机图片URL
            // 这里我们对自定义搜索词进行URL编码，以处理空格等特殊字符
            String encodedQuery = query.replace(" ", "+");
            imageUrl = "https://source.unsplash.com/800x600/?" + encodedQuery;
        }
        // --- 修改结束 ---

        // --- 日志 2: 确认最终的图片 URL ---
        Log.d(TAG, "Glide is attempting to load URL: " + imageUrl);
        String productName = capitalizeWords(query);
        double price = 29.99 + (new Random().nextDouble() * 200); // 生成更真实的随机价格
        String description = "A high-quality, stylish " + productName + " that perfectly fits your lifestyle.";
        String productInfo = String.format(Locale.US, "Product: %s\nPrice: $%.2f\nDescription: %s",
                productName, price, description);


        if (true) {
            binding.progressBar.setVisibility(View.GONE);
            binding.searchResultLayout.setVisibility(View.VISIBLE);
            binding.productImage.setVisibility(View.GONE);
            binding.searchResultText.setText(productInfo);
            return;
        }
        Log.d(TAG, "Starting Glide request...");
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // --- 日志 4: 关键！捕获加载失败的异常信息 ---
                        Log.e(TAG, "Glide onLoadFailed. Error: ", e); // 使用 Log.e 并传入异常对象 e

                        // 增加一个检查，确保Fragment仍然附加到Activity
                        if (isAdded()) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.searchResultLayout.setVisibility(View.VISIBLE);
                            binding.productImage.setVisibility(View.GONE);
                            binding.searchResultText.setText("Failed to load image for: " + query);
                        }
                        return false; // 返回 false 很重要，让 Glide 继续处理
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // --- 日志 5: 确认图片加载成功 ---
                        Log.d(TAG, "Glide onResourceReady. Image loaded successfully!");

                        // 增加一个检查
                        if (isAdded()) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.searchResultLayout.setVisibility(View.VISIBLE);
                            binding.productImage.setVisibility(View.VISIBLE);
                            binding.searchResultText.setText(productInfo);
                        }
                        return false;
                    }
                })
                .into(binding.productImage);
    }

    private void hideKeyboard() {
        View view = getActivity() != null ? getActivity().getCurrentFocus() : null;
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        // 处理多个单词的情况
        String[] words = str.split(" ");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalizedWords.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalizedWords.toString().trim();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}