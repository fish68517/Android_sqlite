package com.example.booktracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.entity.Book;

public class BookDetailActivity extends AppCompatActivity {

    private Book book;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        book = (Book) getIntent().getSerializableExtra("book");

        ImageView ivBookImg = findViewById(R.id.iv_book_img);
        TextView tvBookName = findViewById(R.id.tv_book_name);
        TextView tvBookAuthor = findViewById(R.id.tv_book_author);
        TextView tvBookDesc = findViewById(R.id.tv_book_desc);
        TextView tvBookNum = findViewById(R.id.tv_book_num);
        Button btnPurchase = findViewById(R.id.btn_purchase);

        Glide.with(this).load(book.getUrl()).into(ivBookImg);
        tvBookName.setText(book.getName());
        tvBookAuthor.setText(book.getAuthor());
        tvBookDesc.setText(book.getDesc());
        tvBookNum.setText(String.format("剩余:%d      总数:%d", book.getRemain(), book.getTotal()));

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 添加购买按钮点击事件
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book.getRemain() <= 0) {
                    Toast.makeText(BookDetailActivity.this, "图书库存不足", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 跳转到下单页面
                Intent intent = new Intent(BookDetailActivity.this, OrderCreateActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });
    }
}
