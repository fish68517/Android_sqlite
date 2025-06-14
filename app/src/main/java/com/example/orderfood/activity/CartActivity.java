package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.model.CartItem;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CheckBox selectAllCheckbox;
    private TextView totalPriceText;
    private MaterialButton checkoutButton;
    private CartAdapter cartAdapter;
    private DBMysqlHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);
        initViews();
        loadCartData();
    }

    private void initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        selectAllCheckbox = findViewById(R.id.selectAllCheckbox);
        totalPriceText = findViewById(R.id.totalPriceText);
        checkoutButton = findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this);
        cartRecyclerView.setAdapter(cartAdapter);

        // 设置全选监听
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartAdapter.setAllItemsSelected(isChecked);
            updateTotalPrice();
        });

        // 设置结算按钮监听
        checkoutButton.setOnClickListener(v -> {
            List<CartItem> selectedItems = cartAdapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "请选择要结算的商品", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: 跳转到结算页面
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("cartItems", (Serializable) selectedItems);
            startActivity(intent);
        });

        dbHelper = DBMysqlHelper.getInstance();
    }

    private void loadCartData() {
        dbHelper.getCartItems(MyApplication.getUserId(), new DBMysqlHelper.DatabaseCallback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> cartItems) {
                cartAdapter.setCartItems(cartItems);
                updateTotalPrice();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CartActivity.this, "加载购物车失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTotalPrice() {
        double total = cartAdapter.getSelectedItemsTotal();
        totalPriceText.setText(String.format("¥%.2f", total));
        checkoutButton.setEnabled(total > 0);
    }
}