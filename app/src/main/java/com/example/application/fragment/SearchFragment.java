package com.example.application.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.databinding.FragmentSearchBinding;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    // 模拟一些搜索建议
    private static final String[] SUGGESTIONS = new String[]{
            "跑鞋", "夹克", "T恤", "帆布包", "智能手表", "牛仔裤"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 任务: 使用建议进行搜索 - 级别 2
        // 描述: 创建一个ArrayAdapter并将它设置给AutoCompleteTextView，以提供搜索建议。
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);
        binding.searchView.setAdapter(adapter);

        binding.searchView.setOnItemClickListener((parent, view1, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            binding.searchResultText.setText("你选择了: " + selection);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}