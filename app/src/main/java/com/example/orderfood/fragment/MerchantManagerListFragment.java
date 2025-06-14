package com.example.orderfood.fragment;

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
import com.example.orderfood.R;
import com.example.orderfood.adapter.MerchantListAdapter;
import com.example.orderfood.model.MerchantBean;

import java.util.ArrayList;
import java.util.List;

public class MerchantManagerListFragment extends Fragment {
    private RecyclerView recyclerView;

    private MerchantListAdapter adapter;
    private DBMysqlHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_list_manager, container, false);
        initViews(view);
        loadMerchants();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MerchantListAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);



        // 设置适配器的编辑回调
        adapter.setOnEditClickListener((merchant, newPassword) -> {
            dbHelper.updateMerchantPassword(merchant.getMerchantId(), newPassword, new DBMysqlHelper.DatabaseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        Toast.makeText(getContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "密码修改失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), "修改失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadMerchants() {
        dbHelper.getAllMerchants(new DBMysqlHelper.DatabaseCallback<List<MerchantBean>>() {
            @Override
            public void onSuccess(List<MerchantBean> merchants) {
                adapter.updateData(merchants);

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}