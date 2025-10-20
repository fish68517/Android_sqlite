package com.example.application.activity;// =================================================================================
// 文件路径: app/src/main/java/com/example/geeknotes/ui/MainActivity.java
// =================================================================================


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.adapter.NoteAdapter;
import com.example.application.databinding.ActivityMainBinding;
import com.example.application.model.Note;
import com.example.application.service.ExportService;
import com.example.application.viewmodel.NoteViewModel;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 任务: 欢迎屏幕 (Splash Screen) - 级别 1
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        setupRecyclerView();
        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        binding.fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
            startActivity(intent);
        });

        // 任务: 下拉刷新 - 级别 2
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // 模拟网络刷新
            binding.progressBar.setVisibility(View.VISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
            }, 1500);
        });

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == com.example.application.R.id.action_export) {
                // 任务: 前台服务 - 级别 3
                // 描述: 点击菜单项，启动用于导出笔记的前台服务。
                Intent serviceIntent = new Intent(this, ExportService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                }
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        adapter = new NoteAdapter((note, cardView) -> {
            // 任务: 复杂动画 (MotionLayout) - 级别 3
            // 描述: 点击笔记时，使用共享元素动画启动详情页。
            Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
            intent.putExtra("note", note);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    this, cardView, "note_transition");
            startActivity(intent, options.toBundle());
        });
        binding.recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNotes.setAdapter(adapter);

        // 任务: 使用手势 - 级别 2
        // 描述: 实现RecyclerView的左右滑动删除功能。
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note noteToDelete = adapter.getNoteAt(position);
                noteViewModel.delete(noteToDelete);

                Snackbar.make(binding.getRoot(), "笔记已删除", Snackbar.LENGTH_LONG)
                        .setAction("撤销", v -> noteViewModel.insert(noteToDelete))
                        .show();
            }
        }).attachToRecyclerView(binding.recyclerViewNotes);
    }

    private void observeViewModel() {
        noteViewModel.getAllNotes().observe(this, notes -> {
            adapter.submitList(notes);
            // 模拟添加第一条笔记
            if(notes.isEmpty()){
                noteViewModel.insert(new Note("欢迎使用", "这是一个示例笔记。向右滑动可以删除它。"));
            }
        });
    }
}
