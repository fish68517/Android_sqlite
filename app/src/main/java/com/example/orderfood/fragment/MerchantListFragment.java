package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.activity.MerchantDetailActivity;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.adapter.MerchantAdapter;
import com.example.orderfood.model.MerchantBean;

import java.util.ArrayList;
import java.util.List;

public class MerchantListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_IS_PURCHASED = "is_purchased";

    private String category;
    private boolean isPurchased;
    private RecyclerView merchantRecyclerView;
    private MerchantAdapter merchantAdapter;
    private DBMysqlHelper dbHelper;

    public static MerchantListFragment newInstance(String category, boolean isPurchased) {
        MerchantListFragment fragment = new MerchantListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        args.putBoolean(ARG_IS_PURCHASED, isPurchased);
        fragment.setArguments(args);
        return fragment;
    }

    public MerchantListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance(getActivity());
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            isPurchased = getArguments().getBoolean(ARG_IS_PURCHASED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_list, container, false);
        initViews(view);
        loadMerchants();
        return view;
    }

    private void initViews(View view) {
        merchantRecyclerView = view.findViewById(R.id.merchantRecyclerView);
        merchantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        merchantAdapter = new MerchantAdapter(getContext(), new ArrayList<>());
        merchantAdapter.setOnItemClickListener(merchant -> {
            Intent intent = new Intent(getContext(), MerchantDetailActivity.class);
            intent.putExtra("merchantId", merchant.getMerchantId());
            startActivity(intent);
        });
        merchantRecyclerView.setAdapter(merchantAdapter);
    }

    private void loadMerchants() {
        if (isPurchased) {
            // 加载买过的店铺
            loadPurchasedMerchants();
        } else {
            // 加载该分类下的所有商家
            loadAllMerchants();
        }
    }

    private void loadAllMerchants() {
        dbHelper.getMerchantsByCategory(category, new DBMysqlHelper.DatabaseCallback<List<MerchantBean>>() {
            @Override
            public void onSuccess(List<MerchantBean> merchants) {
                merchantAdapter.setMerchants(merchants);
                merchantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "加载商家列表失败: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPurchasedMerchants() {
        // 从SharedPreferences或其他地方获取当前登录的ID
        int studentId = getCurrentStudentId();
        
        dbHelper.getPurchasedMerchantsByCompletedOrders(studentId, new DBMysqlHelper.DatabaseCallback<List<MerchantBean>>() {
            @Override
            public void onSuccess(List<MerchantBean> merchants) {
                // 如果需要，可以在这里根据category进行过滤
              /*  List<MerchantBean> filteredMerchants = merchants.stream()
                    .filter(merchant -> merchant.getCategory().equals(category))
                    .collect(Collectors.toList());*/

              /*  List<Integer> filteredMerchants = new ArrayList<>();
                for (MerchantBean merchant : merchants) {
                    int id = merchant.getMerchantId();
                    filteredMerchants.add(id);
                }
                dbHelper.getMerchantListByMerchantIds(filteredMerchants, new DBMysqlHelper.DatabaseCallback<List<MerchantBean>>() {
                    @Override
                    public void onSuccess(List<MerchantBean> result) {
                        merchantAdapter.setMerchants(merchants);
                        merchantAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });*/

                merchantAdapter.setMerchants(merchants);
                merchantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "加载购买记录失败: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getCurrentStudentId() {
        // TODO: 实现获取当前登录ID的逻辑
        // 这里需要你实现从SharedPreferences或其他地方获取当前登录的ID
        return MyApplication.getUserId(); // 临时返回默认值
    }
} 