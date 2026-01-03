package com.myapplication.app.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.myapplication.app.MusicService;
import com.myapplication.app.activity.AddMediaActivity;
import com.myapplication.app.db.StudentDbHelper;

import java.util.ArrayList;

public class MediaFragment extends Fragment {

    private StudentDbHelper dbHelper;
    private ListView listView;
    private ArrayList<String> displayList;
    private ArrayList<Integer> idList;
    private ArrayList<String> pathList; // 存路径，用于播放
    private ArrayList<String> titleList; // 存标题
    private ArrayList<String> authorList; // 存作者
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);

        dbHelper = new StudentDbHelper(getContext());
        listView = view.findViewById(R.id.list_view_media);

        displayList = new ArrayList<>();
        idList = new ArrayList<>();
        pathList = new ArrayList<>();
        titleList = new ArrayList<>();
        authorList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        // 点击：播放音乐
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String path = pathList.get(position);
            String title = titleList.get(position);
            String author = authorList.get(position);

            Intent intent = new Intent(getActivity(), MusicService.class);
            intent.setAction(MusicService.ACTION_PLAY);
            intent.putExtra("path", path);
            intent.putExtra("title", title);
            intent.putExtra("author", author);
            getActivity().startService(intent);

            Toast.makeText(getContext(), "开始播放: " + title, Toast.LENGTH_SHORT).show();
        });

        // 长按：删除音乐
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            int mediaId = idList.get(position);
            new AlertDialog.Builder(getContext())
                    .setTitle("删除音频")
                    .setMessage("确定从列表移除该音频吗？")
                    .setPositiveButton("删除", (dialog, which) -> deleteMedia(mediaId))
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

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        displayList.clear(); idList.clear(); pathList.clear(); titleList.clear(); authorList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_MEDIA, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDbHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_MEDIA_NAME));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_MEDIA_PATH));
            String author = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_MEDIA_AUTHOR));

            idList.add(id);
            pathList.add(path);
            titleList.add(name);
            authorList.add(author);
            displayList.add(name + " - " + author);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void deleteMedia(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(StudentDbHelper.TABLE_MEDIA, StudentDbHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        loadData();
    }
}