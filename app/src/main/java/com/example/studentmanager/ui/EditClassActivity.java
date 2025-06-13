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

public class EditClassActivity extends AppCompatActivity {
    private EditText etClassName;
    private Button btnUpdateClass;
    private StudentDBHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        dbHelper = new StudentDBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etClassName = findViewById(R.id.et_class_name);
        btnUpdateClass = findViewById(R.id.btn_update_class);

        classId = getIntent().getIntExtra("class_id", -1);
        String className = getIntent().getStringExtra("class_name");

        if (classId == -1) {
            Toast.makeText(this, "无效的班级ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etClassName.setText(className);

        btnUpdateClass.setOnClickListener(v -> {
            String newClassName = etClassName.getText().toString().trim();
            if (newClassName.isEmpty()) {
                Toast.makeText(this, "班级名称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            int rowsAffected = dbHelper.updateClass(classId, newClassName);
            if (rowsAffected > 0) {
                Toast.makeText(this, "班级更新成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 