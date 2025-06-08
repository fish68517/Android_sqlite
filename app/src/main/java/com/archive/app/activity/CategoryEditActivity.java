package com.archive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Category;
import com.example.myapplication.R;

public class CategoryEditActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    private static final int INVALID_CATEGORY_ID = -1;

    private EditText etCategoryName;
    private Button btnSave;
    private OpenHelperDataBase dbHelper;

    private int categoryId = INVALID_CATEGORY_ID;
    private Category currentCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        etCategoryName = findViewById(R.id.et_category_name);
        btnSave = findViewById(R.id.btn_save_category);
        dbHelper = new OpenHelperDataBase(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_CATEGORY_ID)) {
            categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, INVALID_CATEGORY_ID);
        }

        if (categoryId != INVALID_CATEGORY_ID) {
            setTitle("编辑分类");
            loadCategoryData();
        } else {
            setTitle("添加新分类");
        }

        btnSave.setOnClickListener(v -> saveCategory());
    }

    private void loadCategoryData() {
        currentCategory = dbHelper.getCategoryById(categoryId);
        if (currentCategory != null) {
            etCategoryName.setText(currentCategory.getName());
        } else {
            Toast.makeText(this, "无法加载分类信息", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveCategory() {
        String name = etCategoryName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etCategoryName.setError("分类名称不能为空");
            etCategoryName.requestFocus();
            return;
        }

        if (categoryId != INVALID_CATEGORY_ID) {
            // 更新模式
            if (currentCategory != null) {
                currentCategory.setName(name);
                int result = dbHelper.updateCategory(currentCategory);
                if (result > 0) {
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // 添加模式
            Category newCategory = new Category();
            newCategory.setName(name);
            long result = dbHelper.addCategory(newCategory);
            if (result != -1) {
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 