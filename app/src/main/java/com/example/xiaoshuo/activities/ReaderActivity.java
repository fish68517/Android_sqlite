package com.example.xiaoshuo.activities;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.ChapterAdapter;
import com.example.xiaoshuo.models.Chapter;
import com.example.xiaoshuo.utils.ChapterDataManager;
import com.example.xiaoshuo.utils.ReadHistoryManager;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ScrollView scrollView;
    private TextView tvChapterTitle;
    private TextView tvContent;
    private LinearLayout bottomControls;
    private Button btnPrevChapter;
    private Button btnChapterList;
    private Button btnNextChapter;
    private RecyclerView rvChapters;
    
    private GestureDetectorCompat gestureDetector;
    private boolean isControlsVisible = false;
    
    private String bookTitle;
    private String bookId;
    private String bookAuthor;
    private String bookCover;
    private int currentChapterIndex = 0;
    private List<Chapter> chapterList;
    private ReadHistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        
        // 获取传入的书籍信息
        bookTitle = getIntent().getStringExtra("book_title");
        bookId = getIntent().getStringExtra("BOOK_ID");
        bookAuthor = getIntent().getStringExtra("BOOK_AUTHOR");
        bookCover = getIntent().getStringExtra("BOOK_COVER");
        currentChapterIndex = getIntent().getIntExtra("chapter_index", 0);
        
        // 初始化阅读历史管理器
        historyManager = ReadHistoryManager.getInstance(this);
        
        initViews();
        setupGestureDetector();
        setupListeners();
        loadChapters();
        loadChapterContent();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scroll_view);
        tvChapterTitle = findViewById(R.id.tv_chapter_title);
        tvContent = findViewById(R.id.tv_content);
        bottomControls = findViewById(R.id.bottom_controls);
        btnPrevChapter = findViewById(R.id.btn_prev_chapter);
        btnChapterList = findViewById(R.id.btn_chapter_list);
        btnNextChapter = findViewById(R.id.btn_next_chapter);
        rvChapters = findViewById(R.id.rv_chapters);
        
        // 设置章节列表
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetectorCompat(this, this);
    }

    private void setupListeners() {
        // 点击屏幕中间区域显示/隐藏控制栏
        scrollView.setOnClickListener(v -> toggleControls());
        
        // 上一章
        btnPrevChapter.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                loadChapterContent();
                scrollView.scrollTo(0, 0);
            }
        });
        
        // 下一章
        btnNextChapter.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) {
                currentChapterIndex++;
                loadChapterContent();
                scrollView.scrollTo(0, 0);
            }
        });
        
        // 显示章节列表
        btnChapterList.setOnClickListener(v -> {
            drawerLayout.openDrawer(findViewById(R.id.drawer_chapter_list));
        });
    }

    private void loadChapters() {
        // 从ChapterDataManager加载章节列表
        chapterList = ChapterDataManager.getChaptersByBookTitle(bookTitle);
        
        if (chapterList.isEmpty()) {
            // 如果没有找到章节，显示提示信息
            tvContent.setText("抱歉，暂无此书的章节内容");
            return;
        }
        
        ChapterAdapter adapter = new ChapterAdapter(this, chapterList);
        adapter.setCurrentChapterIndex(currentChapterIndex);
        adapter.setOnItemClickListener((chapter, position) -> {
            currentChapterIndex = position;
            loadChapterContent();
            drawerLayout.closeDrawers();
            scrollView.scrollTo(0, 0);
        });
        rvChapters.setAdapter(adapter);
    }

    private void loadChapterContent() {
        if (chapterList.isEmpty() || currentChapterIndex < 0 || currentChapterIndex >= chapterList.size()) {
            return;
        }
        
        Chapter chapter = chapterList.get(currentChapterIndex);
        tvChapterTitle.setText(chapter.getTitle());
        
        // 设置章节内容
        String content = chapter.getContent();
        if (content != null && !content.isEmpty()) {
            // 格式化内容，添加段落缩进
            content = "    " + content.replace("\n", "\n    ");
            tvContent.setText(content);
        } else {
            tvContent.setText("本章内容正在更新中...");
        }
        
        // 更新上一章/下一章按钮状态
        btnPrevChapter.setEnabled(currentChapterIndex > 0);
        btnNextChapter.setEnabled(currentChapterIndex < chapterList.size() - 1);
        
        // 更新工具栏标题
        toolbar.setTitle(bookTitle);
        
        // 记录阅读历史
        saveReadHistory(chapter);
    }
    
    private void saveReadHistory(Chapter chapter) {
        if (bookId != null && bookTitle != null) {
            historyManager.addHistory(
                bookId,
                bookTitle,
                bookCover,
                bookAuthor,
                currentChapterIndex,
                chapter.getTitle()
            );
        }
    }

    private void toggleControls() {
        isControlsVisible = !isControlsVisible;
        toolbar.setVisibility(isControlsVisible ? View.VISIBLE : View.GONE);
        bottomControls.setVisibility(isControlsVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        // 左右滑动翻页
        float deltaX = e2.getX() - e1.getX();
        if (Math.abs(deltaX) > 100 && Math.abs(velocityX) > 100) {
            if (deltaX > 0) {
                // 右滑，上一章
                if (currentChapterIndex > 0) {
                    currentChapterIndex--;
                    loadChapterContent();
                    scrollView.scrollTo(0, 0);
                }
            } else {
                // 左滑，下一章
                if (currentChapterIndex < chapterList.size() - 1) {
                    currentChapterIndex++;
                    loadChapterContent();
                    scrollView.scrollTo(0, 0);
                }
            }
            return true;
        }
        return false;
    }
} 