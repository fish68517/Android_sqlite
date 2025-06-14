package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.adapter.CheckoutItemAdapter;
import com.example.orderfood.model.Orders;
import com.example.orderfood.R;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.UserAddress;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private TextView addressText;
    private TextView contactText;
    private TextView merchantName;
    private RecyclerView itemsRecyclerView;
    private RadioGroup deliveryGroup;
    private EditText remarkEdit;
    private TextView totalPriceText;
    private MaterialButton submitButton;
    
    private CheckoutItemAdapter itemsAdapter;
    private List<CartItem> cartItems;
    private double totalPrice;
    private DBMysqlHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        dbHelper = DBMysqlHelper.getInstance();
        initViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addressText = findViewById(R.id.addressText);
        contactText = findViewById(R.id.contactText);
        merchantName = findViewById(R.id.merchantName);
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        deliveryGroup = findViewById(R.id.deliveryGroup);
        remarkEdit = findViewById(R.id.remarkEdit);
        totalPriceText = findViewById(R.id.totalPriceText);
        submitButton = findViewById(R.id.submitButton);

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new CheckoutItemAdapter(this);
        itemsRecyclerView.setAdapter(itemsAdapter);
    }

    private void loadData() {
        // 获取传递过来的购物车商品
        cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");
        itemsAdapter.setItems(cartItems);
        
        // 计算总价
        totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getSubtotal();
        }
        totalPriceText.setText(String.format("¥%.2f", totalPrice));

        // 设置商家名称
        if (!cartItems.isEmpty()) {
            merchantName.setText(cartItems.get(0).getMerchantName());
        }

        // 加载用户地址信息
        loadUserAddress();
    }

    private void loadUserAddress() {
        int userId = MyApplication.getUserId();
        dbHelper.getUserAddress(userId, new DBMysqlHelper.DatabaseCallback<UserAddress>() {
            @Override
            public void onSuccess(UserAddress address) {
                if (address != null) {
                    addressText.setText(address.getAddress());
                    contactText.setText(String.format("%s %s", address.getName(), address.getPhone()));
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CheckoutActivity.this, "加载地址失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // 地址卡片点击事件
        findViewById(R.id.addressCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivityForResult(intent, REQUEST_ADDRESS);
        });

        // 提交订单
        submitButton.setOnClickListener(v -> {
            submitOrder();
        });
    }

    private void submitOrder() {
        Orders order = new Orders();
        order.setStudentId(MyApplication.getUserId());
        order.setMerchantId(cartItems.get(0).getMerchantId());
        order.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        order.setTotalPrice(totalPrice);
        order.setOrderStatus("pending");
        order.setDiningOption(deliveryGroup.getCheckedRadioButtonId() == R.id.deliveryRadio ? "delivery" : "pickup");
        order.setRemark(remarkEdit.getText().toString());

        // 将购物车商品转换为订单商品列表
        StringBuilder dishList = new StringBuilder();
        for (CartItem item : cartItems) {
            if (dishList.length() > 0) {
                dishList.append(",");
            }
            order.setDishId(item.getDishId());
            dishList.append(item.getDishId()).append(":").append(item.getQuantity());
        }
        order.setDishList(dishList.toString());


        // 提交订单
        dbHelper.placeOrder(order, new DBMysqlHelper.DatabaseCallback<Orders>() {
            @Override
            public void onSuccess(Orders result) {
                // 清空已结算的购物车商品
                clearCartItems();
                
                Toast.makeText(CheckoutActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Exception e) {
                System.out.println("下单失败：" + e.getMessage());
                Toast.makeText(CheckoutActivity.this, "下单失败: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCartItems() {
        List<Integer> recordIds = new ArrayList<>();
        for (CartItem item : cartItems) {
            recordIds.add(item.getRecordId());
        }
        dbHelper.removeCartItems(recordIds, new DBMysqlHelper.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // 购物车商品已清除
            }

            @Override
            public void onError(Exception e) {
                // 清除失败，可以忽略
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
            loadUserAddress();
        }
    }

    private static final int REQUEST_ADDRESS = 1;
} 