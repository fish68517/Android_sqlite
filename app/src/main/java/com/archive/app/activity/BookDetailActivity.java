package com.archive.app.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Book;
import com.example.myapplication.R; // R文件包

/**
 * 显示书籍详细信息的 Activity
 */
public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "extra_book_id"; // 通过Intent传递Book ID的键
    public static final String EXTRA_BOOK_OBJECT = "extra_book_object"; // 直接传递Book对象的键
    private static final String TAG = "BookDetailActivity";

    private ImageView ivCover;
    private TextView tvTitle, tvAuthor, tvIsbn, tvCategory, tvPublishDate, tvCreateTime, tvDescription;
    private Toolbar toolbar;
    private OpenHelperDataBase dbHelper;
    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        dbHelper = new OpenHelperDataBase(this);

        toolbar = findViewById(R.id.toolbar_book_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 初始化视图
        ivCover = findViewById(R.id.iv_detail_book_cover);
        tvTitle = findViewById(R.id.tv_detail_book_title);
        tvAuthor = findViewById(R.id.tv_detail_book_author);
        tvIsbn = findViewById(R.id.tv_detail_book_isbn);
        tvCategory = findViewById(R.id.tv_detail_book_category);
        tvPublishDate = findViewById(R.id.tv_detail_book_publish_date);
        tvCreateTime = findViewById(R.id.tv_detail_book_create_time);
        tvDescription = findViewById(R.id.tv_detail_book_description);

        // 获取传递过来的Book对象或Book ID
        if (getIntent().hasExtra(EXTRA_BOOK_OBJECT)) {
            currentBook = (Book) getIntent().getSerializableExtra(EXTRA_BOOK_OBJECT);
            Log.d(TAG, "通过Book对象加载详情: " + (currentBook != null ? currentBook.getTitle() : "null"));
            displayBookDetails();
        } else if (getIntent().hasExtra(EXTRA_BOOK_ID)) {
            long bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, -1);
            Log.d(TAG, "通过Book ID加载详情: " + bookId);
            if (bookId != -1) {
                // 注意：数据库操作应在后台线程执行，这里为简化直接调用
                currentBook = dbHelper.getBookById(bookId);
                displayBookDetails();
            } else {
                Log.e(TAG, "无效的书籍ID传递过来");
                Toast.makeText(this, "无法加载书籍详情：无效的ID", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Log.e(TAG, "没有书籍信息传递过来");
            Toast.makeText(this, "无法加载书籍详情：缺少信息", Toast.LENGTH_LONG).show();
            finish(); // 关闭Activity，因为没有数据显示
        }
    }

    /**
     * 在界面上显示书籍的详细信息
     */
    private void displayBookDetails() {
        if (currentBook == null) {
            Log.e(TAG, "要显示的书籍对象为null");
            Toast.makeText(this, "无法显示书籍详情，数据为空", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentBook.getTitle()); // 设置Toolbar标题为书名
        }

        tvTitle.setText(currentBook.getTitle());
        tvAuthor.setText(currentBook.getAuthor());
        tvIsbn.setText(currentBook.getIsbn());
        tvCategory.setText(currentBook.getCategoryName() != null ? currentBook.getCategoryName() : "未分类");
        tvPublishDate.setText(currentBook.getPublishDate());
        tvCreateTime.setText(currentBook.getCreateTime() != null ? currentBook.getCreateTime() : "未知");
        tvDescription.setText(currentBook.getDescription() != null && !currentBook.getDescription().isEmpty() ? currentBook.getDescription() : "暂无简介");

        // 加载封面图片
        String coverImageName = currentBook.getCoverImage();
        if (!TextUtils.isEmpty(coverImageName)) {
            String drawableName = coverImageName.contains(".") ? coverImageName.substring(0, coverImageName.lastIndexOf('.')) : coverImageName;
            int imageResId = getResources().getIdentifier(drawableName.toLowerCase(), "drawable", getPackageName());
            if (imageResId != 0) {
                ivCover.setImageResource(imageResId);
            } else {
                ivCover.setImageResource(R.drawable.default_book_icon); // 默认图片
                Log.w(TAG, "封面图片资源未找到: " + drawableName);
            }
        } else {
            ivCover.setImageResource(R.drawable.default_book_icon); // 默认图片
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 处理返回按钮事件
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 