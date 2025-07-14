package com.example.booktracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.db.BookDB;
import com.example.booktracker.db.BusinessResult;
import com.example.booktracker.entity.Book;
import com.example.booktracker.utils.DataUtils;
import com.example.booktracker.utils.ImagePreStorageUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnUserEnter, btnAdminEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定控件
        bindView();
        //初始化控件
        initView();

        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_MEDIA_IMAGES)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        //初始化数据
                        DataUtils.init();
                    }
                });
    }

    private void bindView() {
        btnUserEnter = findViewById(R.id.btn_user_enter);
        btnAdminEnter = findViewById(R.id.btn_admin_enter);
    }

    private void initView() {
        //注册事件
        btnUserEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建Intent对象
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //启动Activity
                startActivity(intent);
            }
        });
        btnAdminEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建Intent对象
                Intent intent = new Intent(MainActivity.this, BookManageActivity.class);
                //启动Activity
                startActivity(intent);
            }
        });
    }
}
