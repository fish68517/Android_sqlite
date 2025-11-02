package com.archive.app.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.archive.app.activity.AddEditBookActivity;
import com.archive.app.activity.BookDetailActivity;
import com.archive.app.adapter.BookAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Book;
import com.example.myapplication.R; // R文件路径根据项目结构调整
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass to display and manage books.
 */
public class BookFragment extends Fragment implements BookAdapter.OnBookItemClickListener {

    private static final String TAG = "BookFragment";

    private RecyclerView rvBooks;

    private SearchView searchView; // <-- 1. 添加 SearchView 成员变量
    private BookAdapter bookAdapter;
    private OpenHelperDataBase dbHelper;
    private FloatingActionButton fabAddBook;
    private TextView tvEmptyBooks;
    private ProgressBar pbLoadingBooks;
    private SwipeRefreshLayout swipeRefreshLayout;

    // ActivityResultLauncher 用于处理从 AddEditBookActivity 返回的结果
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public BookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new OpenHelperDataBase(getContext());

        // 注册 ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 如果AddEditBookActivity成功返回 (书籍被添加或编辑)
                        Log.i(TAG, "从AddEditBookActivity成功返回，刷新书籍列表。");
                        loadBooksAsync(null); // 刷新列表
                    } else {
                        Log.d(TAG, "从AddEditBookActivity返回，但没有RESULT_OK。");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        // 初始化视图组件
        rvBooks = view.findViewById(R.id.rv_books);
        fabAddBook = view.findViewById(R.id.fab_add_book);
        tvEmptyBooks = view.findViewById(R.id.tv_empty_books);
        pbLoadingBooks = view.findViewById(R.id.pb_loading_books);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_books);

        setupRecyclerView();

        searchView = view.findViewById(R.id.search_view_books); // <-- 2. 初始化 SearchView

        setupRecyclerView();
        setupSearchView(); // <-- 3. 设置 SearchView 监听

        // FAB 点击事件：启动 AddEditBookActivity 添加新书
        fabAddBook.setOnClickListener(v -> {
            Log.d(TAG, "FAB 点击，启动 AddEditBookActivity (添加模式)");
            Intent intent = new Intent(getActivity(), AddEditBookActivity.class);
            activityResultLauncher.launch(intent);
        });

        // 下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "触发下拉刷新");
            searchView.setQuery("", false); // 清空搜索框
            searchView.clearFocus(); // 移除焦点
            loadBooksAsync(null); // 加载所有书籍
        });

        return view;
    }

    /**
     * 4. 设置 SearchView 的监听器
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户按下回车或搜索按钮时
                loadBooksAsync(query);
                searchView.clearFocus(); // 隐藏键盘
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当用户输入时实时搜索
                loadBooksAsync(newText);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated，开始加载书籍");
        loadBooksAsync(null); // 初始加载书籍数据
    }

    private void setupRecyclerView() {
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        // 初始化Adapter时使用空列表，数据稍后加载
        bookAdapter = new BookAdapter(getContext(), new ArrayList<>(), this);
        rvBooks.setAdapter(bookAdapter);
    }

    /**
     * 异步加载书籍列表
     */
    /**
     * 5. 修改 loadBooksAsync 方法以接受搜索查询
     * @param query 搜索关键字，如果为 null 或空，则加载所有书籍
     */
    private void loadBooksAsync(@Nullable String query) {
        Log.i(TAG, "开始异步加载书籍列表... 查询: " + (query == null ? "无" : query));
        pbLoadingBooks.setVisibility(View.VISIBLE);
        tvEmptyBooks.setVisibility(View.GONE);
        rvBooks.setVisibility(View.GONE);
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new Thread(() -> {
            final List<Book> loadedBooks;
            // 根据 query 是否为空来决定调用哪个数据库方法
            if (TextUtils.isEmpty(query)) {
                loadedBooks = dbHelper.getAllBooks();
            } else {
                loadedBooks = dbHelper.searchBooks(query);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    pbLoadingBooks.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (loadedBooks == null || loadedBooks.isEmpty()) {
                        tvEmptyBooks.setVisibility(View.VISIBLE);
                        // 根据是否有搜索词显示不同的提示
                        if (TextUtils.isEmpty(query)) {
                            tvEmptyBooks.setText("书库为空，请添加书籍");
                        } else {
                            tvEmptyBooks.setText("未找到匹配的书籍");
                        }
                        rvBooks.setVisibility(View.GONE);
                        bookAdapter.setBooks(new ArrayList<>());
                    } else {
                        tvEmptyBooks.setVisibility(View.GONE);
                        rvBooks.setVisibility(View.VISIBLE);
                        bookAdapter.setBooks(loadedBooks);
                    }
                });
            }
        }).start();
    }

    // --- BookAdapter.OnBookItemClickListener 实现 --- Interfaces

    @Override
    public void onViewClick(Book book) {
        Log.d(TAG, "查看书籍详情: " + book.getTitle());
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        // Book 类已实现 Serializable，可以直接传递
        intent.putExtra(BookDetailActivity.EXTRA_BOOK_OBJECT, book);
        startActivity(intent);
    }

    @Override
    public void onEditClick(Book book) {
        Log.d(TAG, "编辑书籍: " + book.getTitle());
        Intent intent = new Intent(getActivity(), AddEditBookActivity.class);
        intent.putExtra(AddEditBookActivity.EXTRA_BOOK, book);
        activityResultLauncher.launch(intent); // 使用同一个 launcher，因为它处理的是通用的 RESULT_OK
    }

    @Override
    public void onDeleteClick(final Book book) {
        Log.d(TAG, "请求删除书籍: " + book.getTitle());
        new AlertDialog.Builder(requireContext()) // 使用 requireContext() 更安全
                .setTitle("确认删除")
                .setMessage("您确定要删除书籍《" + book.getTitle() + "》吗？此操作无法撤销。")
                .setPositiveButton("删除", (dialog, which) -> {
                    Log.d(TAG, "确认删除书籍: " + book.getTitle());
                    performDeleteBookAsync(book);
                })
                .setNegativeButton("取消", (dialog, which) -> Log.d(TAG, "取消删除书籍: " + book.getTitle()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * 异步执行删除书籍的操作
     */
    private void performDeleteBookAsync(final Book book) {
        Log.i(TAG, "开始异步删除书籍: " + book.getTitle());
        // 模拟后台操作
        new Thread(() -> {
            final int rowsAffected = dbHelper.deleteBook(book.getId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(getContext(), "《" + book.getTitle() + "》已成功删除", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "书籍删除成功，刷新列表");
                        loadBooksAsync(null); // 重新加载列表
                    } else {
                        Toast.makeText(getContext(), "删除失败，请重试", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "书籍删除失败，数据库操作未影响行: " + book.getTitle());
                    }
                });
            }
        }).start();
    }

    // 当Fragment重新可见时，可以考虑是否需要刷新数据。
    // 但由于我们使用了ActivityResultLauncher和下拉刷新，这里的onResume刷新可能不是必需的，
    // 除非有其他方式修改了数据而没有通知此Fragment。
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "BookFragment onResume");
        // 如果需要每次返回都刷新，可以在这里调用 loadBooksAsync()，但要注意用户体验和性能。
        // 当前设计依赖于 AddEditBookActivity 的 RESULT_OK 和下拉刷新。
    }
} 