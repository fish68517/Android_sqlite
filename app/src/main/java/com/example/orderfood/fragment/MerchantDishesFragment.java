package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;

import com.example.orderfood.activity.EditDishActivity;
import com.example.orderfood.adapter.MerchantDishAdapter;
import com.example.orderfood.model.Dish;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MerchantDishesFragment extends Fragment {
    private RecyclerView recyclerView;

    private FloatingActionButton fabAdd;
    private MerchantDishAdapter adapter;
    private List<Dish> dishes = new ArrayList<>();
    private DBMysqlHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_dishes, container, false);
        initViews(view);
        loadDishes();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);

        fabAdd = view.findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MerchantDishAdapter(getContext(), dishes);
        adapter.setOnItemClickListener(new MerchantDishAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Dish dish) {
                Intent intent = new Intent(getContext(), EditDishActivity.class);
                intent.putExtra("dish", dish);
                startActivityForResult(intent, REQUEST_EDIT_DISH);
            }

            @Override
            public void onDeleteClick(Dish dish) {
                deleteDish(dish);
            }
        });
        recyclerView.setAdapter(adapter);


        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditDishActivity.class);
            startActivityForResult(intent, REQUEST_ADD_DISH);
        });
    }

    private void loadDishes() {
        dbHelper.getMerchantDishes(MyApplication.curMerchant.getMerchantId(), new DBMysqlHelper.DatabaseCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> result) {
                if (result == null || result.size() == 0) {return;}
                dishes.clear();
                dishes.addAll(result);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "加载菜品失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteDish(Dish dish) {
        dbHelper.deleteDish(dish.getDishId(), MyApplication.curMerchant.getMerchantId(), new DBMysqlHelper.DatabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    loadDishes();
                } else {
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && 
            (requestCode == REQUEST_ADD_DISH || requestCode == REQUEST_EDIT_DISH)) {
            loadDishes();
        }
    }

    private static final int REQUEST_ADD_DISH = 1;
    private static final int REQUEST_EDIT_DISH = 2;
} 