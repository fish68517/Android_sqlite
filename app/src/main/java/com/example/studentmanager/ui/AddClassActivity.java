package com.example.studentmanager.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanager.db.StudentDBHelper;

public class AddClassActivity extends AppCompatActivity {
    private EditText etClassName;
    private Button btnSaveClass;
    private StudentDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        dbHelper = new StudentDBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etClassName = findViewById(R.id.et_class_name);
        btnSaveClass = findViewById(R.id.btn_save_class);

        btnSaveClass.setOnClickListener(v -> {
            String className = etClassName.getText().toString().trim();

            if (className.isEmpty()) {
                Toast.makeText(this, "班级名称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = dbHelper.addClass(className);
            if (result != -1) {
                Toast.makeText(this, "班级添加成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 返回上一个活动
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 