package com.example.booktracker.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.db.BusinessResult;
import com.example.booktracker.db.OrderDB;
import com.example.booktracker.entity.Book;
import com.example.booktracker.entity.Order;
import com.example.booktracker.entity.User;
import com.example.booktracker.utils.CurrentUserUtils;

public class OrderCreateActivity extends AppCompatActivity {

    private static final String TAG = "OrderCreateActivity";
    private ImageView ivBack;
    private ImageView ivBookCover;
    private TextView tvBookName;
    private TextView tvBookAuthor;
    private TextView tvBookDesc;
    private TextView tvBookStock;
    private EditText etQuantity;
    private Button btnSubmit;

    private Book book;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_create);

        // 获取传入的图书信息
        book = (Book) getIntent().getSerializableExtra("book");
        if (book == null) {
            Toast.makeText(this, "图书信息获取失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 获取当前用户
        currentUser = CurrentUserUtils.getCurrentUser();

        bindView();
        initView();
    }

    private void bindView() {
        ivBack = findViewById(R.id.iv_back);
        ivBookCover = findViewById(R.id.iv_book_cover);
        tvBookName = findViewById(R.id.tv_book_name);
        tvBookAuthor = findViewById(R.id.tv_book_author);
        tvBookDesc = findViewById(R.id.tv_book_desc);
        tvBookStock = findViewById(R.id.tv_book_stock);
        etQuantity = findViewById(R.id.et_quantity);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void initView() {
        // 设置返回按钮
        ivBack.setOnClickListener(v -> finish());

        // 设置图书信息
        Glide.with(this).load(book.getUrl()).into(ivBookCover);
        tvBookName.setText(book.getName());
        tvBookAuthor.setText("作者: " + book.getAuthor());
        tvBookDesc.setText(book.getDesc());
        tvBookStock.setText(String.format("库存数量: %d", book.getRemain()));

        // 默认购买数量为1
        etQuantity.setText("1");

        // 提交订单
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubmit.setEnabled(false); // 防止重复点击
                createOrder();
            }
        });
    }

    private void createOrder() {
        try {
            // 获取并验证购买数量
            String quantityStr = etQuantity.getText().toString().trim();
            if (TextUtils.isEmpty(quantityStr)) {
                Toast.makeText(this, "请输入购买数量", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的购买数量", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                return;
            }

            if (quantity <= 0) {
                Toast.makeText(this, "购买数量必须大于0", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                return;
            }

            if (quantity > book.getRemain()) {
                Toast.makeText(this, "购买数量不能超过库存数量", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                return;
            }

            // 创建订单对象
            Order order = new Order();
            order.setUserId(currentUser.getId());
            order.setBookId(book.getId());
            order.setBookName(book.getName());
            order.setBookUrl(book.getUrl());
            order.setQuantity(quantity);

            // 保存订单
            BusinessResult<Order> result = OrderDB.addOrder(order);
            if (result.isSuccess()) {
                Toast.makeText(this, "订单创建成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e(TAG, "订单创建失败: " + result.getMessage());
                Toast.makeText(this, "订单创建失败: " + result.getMessage(), Toast.LENGTH_LONG).show();
                btnSubmit.setEnabled(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "订单创建异常", e);
            Toast.makeText(this, "订单创建发生异常: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnSubmit.setEnabled(true);
        }
    }
} 