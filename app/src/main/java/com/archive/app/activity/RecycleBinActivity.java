package com.archive.app.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.adapter.RecycleBinAdapter;
import com.archive.app.model.Note;
import com.archive.app.mvp.RecycleBinContract;
import com.archive.app.mvp.RecycleBinPresenter;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class RecycleBinActivity extends AppCompatActivity implements RecycleBinContract.View, RecycleBinAdapter.OnDeletedNoteActionsListener {

    private RecycleBinContract.Presenter presenter;
    private RecyclerView recyclerView;
    private RecycleBinAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new RecycleBinPresenter(this);
        presenter.attachView(this);

        recyclerView = findViewById(R.id.recycle_bin_recycler_view);
        emptyView = findViewById(R.id.empty_recycle_bin_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecycleBinAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        presenter.loadDeletedNotes(getUserId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showDeletedNotes(List<Note> notes) {
        adapter.setDeletedNotes(notes);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoteRestored() {
        Toast.makeText(this, "笔记已恢复", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotePermanentlyDeleted() {
        Toast.makeText(this, "笔记已永久删除", Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getUserId() {
        return MyApplication.curUser.getId();
    }

    @Override
    public void onRestore(Note note) {
        presenter.restoreNote(note.getId());
    }

    @Override
    public void onPermanentDelete(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("永久删除笔记")
                .setMessage("您确定要永久删除这篇笔记吗？此操作无法撤销。")
                .setPositiveButton("删除", (dialog, which) -> presenter.permanentlyDeleteNote(note.getId()))
                .setNegativeButton("取消", null)
                .show();
    }
} 