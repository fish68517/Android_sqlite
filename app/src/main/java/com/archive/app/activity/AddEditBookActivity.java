package com.archive.app.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Book;
import com.archive.app.model.Category;
import com.example.myapplication.R; // 根据其他文件，R文件在此包下
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 添加或编辑书籍信息的 Activity
 */
public class AddEditBookActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "extra_book_to_edit"; // Intent中传递Book对象的键
    private static final String TAG = "AddEditBookActivity";

    private TextInputEditText etTitle, etAuthor, etIsbn, etPublishDate, etCoverImage, etDescription;
    private Spinner spinnerCategory;
    private Button btnSave;
    private Toolbar toolbar;

    private OpenHelperDataBase dbHelper;
    private Book currentBook; // 当前正在编辑的书籍对象，如果为null则为添加模式
    private List<Category> categoryList; // 分类列表
    private final Calendar calendar = Calendar.getInstance(); // 用于日期选择器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        dbHelper = new OpenHelperDataBase(this);

        // 初始化Toolbar
        toolbar = findViewById(R.id.toolbar_add_edit_book);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 初始化视图组件
        etTitle = findViewById(R.id.et_book_title);
        etAuthor = findViewById(R.id.et_book_author);
        etIsbn = findViewById(R.id.et_book_isbn);
        spinnerCategory = findViewById(R.id.spinner_book_category);
        etPublishDate = findViewById(R.id.et_book_publish_date);
        etCoverImage = findViewById(R.id.et_book_cover_image);
        etDescription = findViewById(R.id.et_book_description);
        btnSave = findViewById(R.id.btn_save_book);

        // 加载分类到Spinner
        loadCategories();

        // 检查是否为编辑模式
        if (getIntent().hasExtra(EXTRA_BOOK)) {
            currentBook = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("编辑书籍");
            }
            Log.d(TAG, "进入编辑模式，书籍: " + (currentBook != null ? currentBook.getTitle() : "null"));
            populateFieldsForEdit();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("添加新书籍");
            }
            Log.d(TAG, "进入添加模式");
        }

        // 出版日期 EditText 点击事件，弹出日期选择器
        etPublishDate.setOnClickListener(v -> showDatePickerDialog());

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveBookData());
    }

    /**
     * 从数据库加载所有分类并填充到Spinner中
     */
    private void loadCategories() {
        categoryList = dbHelper.getAllCategories();
        if (categoryList == null || categoryList.isEmpty()) {
            Log.w(TAG, "数据库中没有分类信息");
            // 可以考虑添加一个默认的"未分类"或提示用户先添加分类
            Toast.makeText(this, "暂无分类信息，请先添加分类", Toast.LENGTH_LONG).show();
        }
        // Category 类需要重写 toString() 方法以在Spinner中正确显示名称
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    /**
     * 如果是编辑模式，用当前书籍数据填充表单字段
     */
    private void populateFieldsForEdit() {
        if (currentBook != null) {
            etTitle.setText(currentBook.getTitle());
            etAuthor.setText(currentBook.getAuthor());
            etIsbn.setText(currentBook.getIsbn());
            etPublishDate.setText(currentBook.getPublishDate());
            etCoverImage.setText(currentBook.getCoverImage());
            etDescription.setText(currentBook.getDescription());

            // 设置Spinner选中项
            if (categoryList != null) {
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getId() == currentBook.getCategoryId()) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
            // 如果日期不为空，尝试解析并设置calendar
            if (!TextUtils.isEmpty(currentBook.getPublishDate())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                try {
                    calendar.setTime(sdf.parse(currentBook.getPublishDate()));
                } catch (ParseException e) {
                    Log.e(TAG, "解析日期失败: " + currentBook.getPublishDate(), e);
                }
            }
        }
    }

    /**
     * 显示日期选择对话框
     */
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updatePublishDateEditText();
        };

        new DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 更新出版日期 EditText 的显示内容
     */
    private void updatePublishDateEditText() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        etPublishDate.setText(sdf.format(calendar.getTime()));
    }

    /**
     * 保存书籍数据（添加或更新）
     */
    private void saveBookData() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String isbn = etIsbn.getText().toString().trim();
        String publishDateStr = etPublishDate.getText().toString().trim();
        String coverImageName = etCoverImage.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // 基本校验
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(isbn)) {
            Toast.makeText(this, "书籍名称、作者和ISBN为必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        long categoryId = 0; // 默认为0，表示可能未分类或分类选择无效
        String categoryName = "未分类";

        if (selectedCategory != null) {
            categoryId = selectedCategory.getId();
            categoryName = selectedCategory.getName();
        } else if (categoryList != null && !categoryList.isEmpty()){
            // 如果有分类列表但没有选中项（例如Spinner为空或未加载成功后用户直接保存）
            Toast.makeText(this, "请选择一个有效的书籍分类", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // 没有分类可选，允许保存为"未分类"
            Log.w(TAG, "没有选择分类或无可用分类，书籍将保存为未分类");
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublishDate(publishDateStr);
        book.setCoverImage(coverImageName.toLowerCase().replace(".png", "").replace(".jpg", "")); // 简单处理图片名
        book.setDescription(description);
        book.setCategoryId(categoryId);
        book.setCategoryName(categoryName); // 虽然数据库不直接存，但Book对象中可以有

        boolean successOperation;
        String successMessage;
        String failureMessage;

        if (currentBook != null) { // 编辑模式
            book.setId(currentBook.getId());
            int rowsAffected = dbHelper.updateBook(book);
            successOperation = rowsAffected > 0;
            successMessage = "书籍信息更新成功";
            failureMessage = "书籍信息更新失败，请重试";
            Log.d(TAG, "尝试更新书籍: " + title + ", 结果: " + successOperation);
        } else { // 添加模式
            long newBookId = dbHelper.addBook(book);
            successOperation = newBookId != -1;
            successMessage = "新书籍添加成功";
            failureMessage = "新书籍添加失败，请重试";
            Log.d(TAG, "尝试添加书籍: " + title + ", ID: " + newBookId + ", 结果: " + successOperation);
        }

        if (successOperation) {
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent); // 通知前一个Activity数据已更改
            finish(); // 关闭当前Activity
        } else {
            Toast.makeText(this, failureMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 处理Toolbar返回按钮
        if (item.getItemId() == android.R.id.home) {
            finish(); // 关闭当前Activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 