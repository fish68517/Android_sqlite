package com.example.booktracker.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.R;
import com.example.booktracker.adapter.OrderAdapter;
import com.example.booktracker.db.BusinessResult;
import com.example.booktracker.db.OrderDB;
import com.example.booktracker.entity.Order;
import com.example.booktracker.entity.User;
import com.example.booktracker.utils.CurrentUserUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private User currentUser;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        
        bindView();
        initView();
    }
    
    private void bindView() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        rvOrders = findViewById(R.id.rv_orders);
    }
    
    private void initView() {
        // 设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        
        // 获取当前用户
        currentUser = CurrentUserUtils.getCurrentUser();
        
        // 设置订单适配器
        orderAdapter = new OrderAdapter();
        orderAdapter.setOnItemClickListener((position, order) -> {
            confirmReceipt(position, order);
        });
        
        rvOrders.setAdapter(orderAdapter);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        
        // 设置Tab切换监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadOrders(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // 初始加载全部订单
        loadOrders(0);
    }
    
    /**
     * 加载订单
     * @param tabPosition 0-全部，1-待发货，2-已发货，3-已收货
     */
    private void loadOrders(int tabPosition) {
        Integer status = null;
        if (tabPosition > 0) {
            status = tabPosition - 1;
        }
        
        BusinessResult<List<Order>> result = OrderDB.getOrdersByUserId(currentUser.getId(), status);
        if (result.isSuccess()) {
            orderAdapter.setList(result.getData());
            if (result.getData().isEmpty()) {
                Toast.makeText(this, "暂无订单", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 确认收货
     */
    private void confirmReceipt(int position, Order order) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("确认收货")
                .setMessage("确认已收到商品？")
                .setPositiveButton("确认", (dialog, which) -> {
                    BusinessResult<Void> result = OrderDB.updateOrderStatus(order.getId(), 2);
                    if (result.isSuccess()) {
                        order.setStatus(2);
                        orderAdapter.notifyItemChanged(position);
                        Toast.makeText(OrderListActivity.this, "确认收货成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderListActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 