package com.example.application.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.adapter.ProductAdapter;
import com.example.application.databinding.FragmentHomeBinding;
import com.example.application.viewmodel.ProductViewModel;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductViewModel productViewModel;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 任务: MVVM 架构 - 级别 3
        // 描述: 在View(Fragment)中获取ViewModel实例。
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        setupRecyclerView();

        // 任务: MVVM 架构 - 级别 3
        // 描述: 观察ViewModel中的LiveData。当数据变化时，自动更新UI(Adapter)。
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            // 更新RecyclerView的数据
            adapter.submitList(products);
        });
    }

    private void setupRecyclerView() {
        // 任务: 带图片的列表 (RecyclerView) - 级别 2
        // 描述: 初始化并设置RecyclerView和其Adapter。
        adapter = new ProductAdapter();
        binding.recyclerViewProducts.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}