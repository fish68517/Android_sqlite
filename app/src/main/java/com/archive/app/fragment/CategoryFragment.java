package com.archive.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.activity.CategoryEditActivity;
import com.archive.app.adapter.CategoryAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Category;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryListener {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private OpenHelperDataBase dbHelper;

    private final ActivityResultLauncher<Intent> categoryEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadCategories(); // Reload data on successful edit/add
                    Toast.makeText(getContext(), "分类已更新", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new OpenHelperDataBase(getContext());
        categoryList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rv_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_category);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryEditActivity.class);
            categoryEditLauncher.launch(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(dbHelper.getAllCategories());
        adapter.setCategories(categoryList);
    }

    @Override
    public void onEditClick(Category category) {
        Intent intent = new Intent(getActivity(), CategoryEditActivity.class);
        intent.putExtra(CategoryEditActivity.EXTRA_CATEGORY_ID, category.getId());
        categoryEditLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除分类")
                .setMessage("确定要删除分类 " + category.getName() + " 吗？\n注意：这不会删除该分类下的书籍。")
                .setPositiveButton("删除", (dialog, which) -> {
                    int result = dbHelper.deleteCategory(category.getId());
                    if (result > 0) {
                        Toast.makeText(getContext(), "分类已删除", Toast.LENGTH_SHORT).show();
                        loadCategories();
                    } else {
                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 