package com.archive.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Book;
import com.archive.app.model.Category;
import com.example.myapplication.R; // 根据其他文件，R文件在此包下
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 添加或编辑书籍信息的 Activity
 */
public class AddEditBookActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "extra_book_to_edit"; // Intent中传递Book对象的键
    private static final String TAG = "AddEditBookActivity";

    private TextInputEditText etTitle, etAuthor, etIsbn, etPublishDate, etDescription;
    private Spinner spinnerCategory;
    private ImageView ivCoverPreview;
    private Button btnSave, btnTakePhoto, btnChooseGallery;
    private Toolbar toolbar;

    private OpenHelperDataBase dbHelper;
    private Book currentBook; // 当前正在编辑的书籍对象，如果为null则为添加模式
    private List<Category> categoryList; // 分类列表
    private final Calendar calendar = Calendar.getInstance(); // 用于日期选择器

    private Uri currentImageUri = null; // 用于存储当前封面图片的URI

    // 新的 ActivityResultLauncher
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

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

        initializeLaunchers(); // 初始化 Launchers

        // 初始化视图组件
        etTitle = findViewById(R.id.et_book_title);
        etAuthor = findViewById(R.id.et_book_author);
        etIsbn = findViewById(R.id.et_book_isbn);
        spinnerCategory = findViewById(R.id.spinner_book_category);
        etPublishDate = findViewById(R.id.et_book_publish_date);

        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnChooseGallery = findViewById(R.id.btn_choose_gallery);
        ivCoverPreview = findViewById(R.id.iv_cover_preview);

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
        setupClickListeners();
    }

    /**
     * 设置点击事件监听
     */
    private void setupClickListeners() {
        etPublishDate.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveBookData());
        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        btnChooseGallery.setOnClickListener(v -> {
            // 启动系统的照片选择器
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    /**
     * 检查相机权限并启动相机
     */
    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 已有权限，直接启动相机
            launchCamera();
        } else {
            // 请求权限
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * 启动相机应用
     */
    private void launchCamera() {
        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException ex) {
            Log.e(TAG, "创建图片文件失败", ex);
            Toast.makeText(this, "创建图片文件失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageFile != null) {
            // 从文件创建 content URI
            Uri photoURI = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    imageFile);
            currentImageUri = photoURI;
            // 启动相机
            takePictureLauncher.launch(currentImageUri);
        }
    }

    /**
     * 创建用于存储相机照片的临时文件
     * @return 创建的文件
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // 创建一个唯一的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /**
     * 初始化所有 ActivityResultLauncher
     */
    private void initializeLaunchers() {
        // 1. 注册权限请求回调
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // 权限被授予，启动相机
                        launchCamera();
                    } else {
                        // 权限被拒绝，提示用户
                        Toast.makeText(this, "相机权限被拒绝，无法拍照", Toast.LENGTH_SHORT).show();
                    }
                });

        // 2. 注册拍照结果回调
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        // 拍照成功，currentImageUri 已指向图片
                        ivCoverPreview.setImageURI(currentImageUri);
                        Log.d(TAG, "拍照成功，URI: " + currentImageUri);
                    } else {
                        // 用户取消了拍照或拍照失败
                        Log.d(TAG, "拍照操作被取消或失败");
                        currentImageUri = null; // 重置URI
                    }
                });

        // 3. 注册相册选择结果回调
        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        // 图片选择成功
                        Log.d(TAG, "从相册选择的URI: " + uri);
                        // 申请对URI的持久访问权限
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        currentImageUri = uri;
                        ivCoverPreview.setImageURI(currentImageUri);
                    } else {
                        // 用户没有选择任何图片
                        Log.d(TAG, "没有从相册选择图片");
                    }
                });
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
            // etCoverImage.setText(currentBook.getCoverImage());
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
        // String coverImageName = etCoverImage.getText().toString().trim();
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
        // book.setCoverImage(coverImageName.toLowerCase().replace(".png", "").replace(".jpg", "")); // 简单处理图片名
        book.setDescription(description);
        book.setCategoryId(categoryId);
        book.setCategoryName(categoryName); // 虽然数据库不直接存，但Book对象中可以有

        // ** 保存封面图片路径 **
        if (currentImageUri != null) {
            book.setCoverImage(currentImageUri.toString());
        } else if (currentBook != null && !TextUtils.isEmpty(currentBook.getCoverImage())) {
            // 编辑模式下，如果用户没有选择新图片，则保留旧图片
            book.setCoverImage(currentBook.getCoverImage());
        } else {
            // 添加模式下没有选择图片，或编辑模式下删除了图片
            book.setCoverImage(""); // 存空字符串
        }

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