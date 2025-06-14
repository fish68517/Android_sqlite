package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.CartCountListener;
import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.adapter.DishAdapter;
import com.example.orderfood.model.Dish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DishListActivity extends AppCompatActivity {
    private RecyclerView rightRecyclerView;

    private DishAdapter dishAdapter;
    private List<Dish> dishList = new ArrayList<Dish>();
    private int merchantId;
    private TextView cartItemCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);
        rightRecyclerView = findViewById(R.id.rightRecyclerView);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        buttonCart.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View v) {
                  // 加载Fragment
                  Intent intent = new Intent(DishListActivity.this, CartActivity.class);
                  startActivity(intent);
              }
        });
        cartItemCount = findViewById(R.id.cartItemCount);

        // 获取 Bundle 中的 merchantId
        Bundle bundle = getIntent().getExtras();
        merchantId = bundle.getInt("merchantId");


        dishAdapter = new DishAdapter(this, new ArrayList<>(), new CartCountListener() {
            @Override
            public int getCount(int count) {
                cartItemCount.setText(count + "");
                return count;
            }
        });
        // 使用菜品布局
        dishAdapter.setGridLayout(false);
        rightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rightRecyclerView.setAdapter(dishAdapter);
        fetchDishes();
    }

    private void fetchDishes() {
        DBMysqlHelper.getInstance().getAllDishes(new DBMysqlHelper.DatabaseCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> result) {
                dishList.clear();
                List<Dish> list = result;
                System.out.println("餐馆ID: " + merchantId);
                System.out.println("菜品数量222: " + list);
                Iterator<Dish> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Dish dish = iterator.next();
                    if (dish.getMerchantId() != merchantId) {
                        iterator.remove();
                    }
                }
                dishList.addAll(list);
                MyApplication.setDishList(dishList);
                System.out.println("菜品数量ff: " + dishList);
                dishAdapter.updateDishList(dishList);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }
}
