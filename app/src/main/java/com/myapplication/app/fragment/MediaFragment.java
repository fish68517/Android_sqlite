package com.myapplication.app.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.myapplication.app.MusicService;
import com.myapplication.app.activity.AddMediaActivity;
import com.myapplication.app.adapter.MediaAdapter;
import com.myapplication.app.db.StudentDbHelper;
import com.myapplication.app.model.MediaModel;


import java.util.ArrayList;

public class MediaFragment extends Fragment {

    private StudentDbHelper dbHelper;
    private ListView listView;
    private ArrayList<MediaModel> mediaList; // 使用 Model 列表
    private MediaAdapter adapter;

    // 记录 Fragment 内部的播放状态
    private int currentPosition = -1;
    private boolean isPlaying = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);

        dbHelper = new StudentDbHelper(getContext());
        listView = view.findViewById(R.id.list_view_media);
        mediaList = new ArrayList<>();

        // 初始化 Adapter，并实现点击回调
        adapter = new MediaAdapter(getContext(), mediaList, position -> {
            handlePlayClick(position);
        });

        listView.setAdapter(adapter);

        // 长按删除保持不变
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            MediaModel item = mediaList.get(position);
            new AlertDialog.Builder(getContext())
                    .setTitle("删除音频")
                    .setMessage("确定要删除 " + item.name + " 吗？")
                    .setPositiveButton("删除", (dialog, which) -> deleteMedia(item.id))
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });

        // 添加按钮
        view.findViewById(R.id.fab_add_media).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddMediaActivity.class));
        });

        return view;
    }

    // 处理播放按钮点击逻辑
    private void handlePlayClick(int position) {
        MediaModel item = mediaList.get(position);

        if (currentPosition == position) {
            // 1. 点击的是当前正在播放/暂停的歌曲 -> 切换状态
            if (isPlaying) {
                // 正在播放 -> 暂停
                sendServiceAction(MusicService.ACTION_PAUSE, null, null, null);
                isPlaying = false;
                Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
            } else {
                // 暂停中 -> 继续播放 (由于 Service 逻辑，发 PAUSE 也是切换)
                // 或者重新发 PLAY 也可以，这里假设 Service 的 PAUSE 是 toggle 功能
                sendServiceAction(MusicService.ACTION_PLAY, null, null, null);
                isPlaying = true;
                Toast.makeText(getContext(), "继续播放", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 2. 点击的是一首新歌 -> 播放新歌
            currentPosition = position;
            isPlaying = true;
            sendServiceAction(MusicService.ACTION_PLAY, item.path, item.name, item.author);
            Toast.makeText(getContext(), "播放: " + item.name, Toast.LENGTH_SHORT).show();
        }

        // 更新 Adapter UI
        adapter.setPlayingState(currentPosition, isPlaying);
    }

    // 封装发送 Intent 给 Service 的方法
    private void sendServiceAction(String action, String path, String title, String author) {
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.setAction(action);
        if (path != null) {
            intent.putExtra("path", path);
            intent.putExtra("title", title);
            intent.putExtra("author", author);
        }
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        mediaList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_MEDIA, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDbHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_MEDIA_NAME));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_MEDIA_PATH));
            String author = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_MEDIA_AUTHOR));

            // 存入 Model
            mediaList.add(new MediaModel(id, name, path, author));
        }
        cursor.close();

        // 数据加载完，如果之前有记录播放状态，需要恢复UI显示（这里简单处理，刷新时重置UI状态）
        // 如果想保留状态，需要判断 mediaList 是否包含 currentPosition
        adapter.notifyDataSetChanged();
    }

    private void deleteMedia(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(StudentDbHelper.TABLE_MEDIA, StudentDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        // 如果删除的是当前播放的歌，重置状态
        if (currentPosition != -1) {
            // 简单处理：重置所有状态，停止播放
            sendServiceAction(MusicService.ACTION_EXIT, null, null, null);
            currentPosition = -1;
            isPlaying = false;
        }

        loadData();
    }
}