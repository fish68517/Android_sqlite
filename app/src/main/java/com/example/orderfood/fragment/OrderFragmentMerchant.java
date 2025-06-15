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
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.adapter.OrderAdapter;
import com.example.orderfood.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class OrderFragmentMerchant extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Orders> ordersList = new ArrayList<>();
    private DBMysqlHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        initViews(view);
        loadOrders();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(getContext(), ordersList);
        adapter.setOnPayClickListener(order -> handlePayment(order));
        recyclerView.setAdapter(adapter);


    }

    private void loadOrders() {
        dbHelper.getOrdersByMerchantId(MyApplication.curMerchant.getMerchantId(), new DBMysqlHelper.DatabaseCallback<List<Orders>>() {
            @Override
            public void onSuccess(List<Orders> result) {
                ordersList.clear();
                ordersList.addAll(result);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "加载订单失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void handlePayment(Orders order) {
        // 处理支付逻辑
        order.setOrderStatus("paid");
        dbHelper.updateOrderStatus(order.getOrderId(), "paid", new DBMysqlHelper.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                loadOrders(); // 刷新订单列表
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "支付失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}
