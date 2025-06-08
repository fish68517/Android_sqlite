package com.archive.app.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.adapter.CategoryAdapter;
import com.archive.app.model.Category;
import com.archive.app.mvp.CategoryContract;
import com.archive.app.mvp.CategoryPresenter;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryContract.View, CategoryAdapter.OnCategoryClickListener {

    private CategoryContract.Presenter presenter;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private TextView emptyView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new CategoryPresenter(getContext());
        presenter.attachView(this);

        recyclerView = view.findViewById(R.id.categories_recycler_view);
        emptyView = view.findViewById(R.id.empty_category_view);
        fab = view.findViewById(R.id.fab_add_category);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> showAddCategoryDialog());

        presenter.loadCategories(getUserId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showCategories(List<Category> categories) {
        adapter.setCategories(categories);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public long getUserId() {
        return MyApplication.curUser.getId();
    }

    @Override
    public void onCategoryAdded() {
        Toast.makeText(getContext(), "分类已添加", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategoryClick(Category category) {
        // Can be used to filter notes in the future
        Toast.makeText(getContext(), "Clicked on " + category.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除分类")
                .setMessage("确定要删除 '" + category.getName() + "' 吗？该分类下的笔记不会被删除。")
                .setPositiveButton("删除", (dialog, which) -> presenter.deleteCategory(category.getId()))
                .setNegativeButton("取消", null)
                .show();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("添加新分类");
        final EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("添加", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                presenter.addCategory(categoryName, getUserId());
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }
} 