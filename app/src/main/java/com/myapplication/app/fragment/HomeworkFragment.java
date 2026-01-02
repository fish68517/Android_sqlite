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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myapplication.app.activity.AddHomeworkActivity;
import com.myapplication.app.db.StudentDbHelper;

import java.util.ArrayList;

public class HomeworkFragment extends Fragment {

    private StudentDbHelper dbHelper;
    private ListView listView;

    // 存储界面显示的文本
    private ArrayList<String> homeworkList;
    // 存储对应的数据库主键 ID (核心新增)
    private ArrayList<Integer> idList;

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework, container, false);

        dbHelper = new StudentDbHelper(getContext());
        listView = view.findViewById(R.id.list_view_homework);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_homework);

        homeworkList = new ArrayList<>();
        idList = new ArrayList<>(); // 初始化ID列表

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, homeworkList);
        listView.setAdapter(adapter);

        // 1. 点击按钮：跳转添加 (ID = -1)
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddHomeworkActivity.class);
            intent.putExtra("hw_id", -1);
            startActivity(intent);
        });

        // 2. 点击列表项：跳转修改 (传递 ID)
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            int hwId = idList.get(position);
            Intent intent = new Intent(getActivity(), AddHomeworkActivity.class);
            intent.putExtra("hw_id", hwId);
            startActivity(intent);
        });

        // 3. 长按列表项：删除
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            int hwId = idList.get(position);
            showDeleteDialog(hwId);
            return true;
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        homeworkList.clear();
        idList.clear(); // 清空ID列表

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_HOMEWORK, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            // 获取 ID
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDbHelper.COLUMN_ID));
            // 获取内容
            String content = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_HW_CONTENT));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_HW_DEADLINE));

            idList.add(id);
            homeworkList.add("作业: " + content + "\n截止: " + deadline);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // 显示删除确认框
    private void showDeleteDialog(int hwId) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除作业")
                .setMessage("确定要删除这条作业记录吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteHomework(hwId))
                .setNegativeButton("取消", null)
                .show();
    }

    // 执行数据库删除
    private void deleteHomework(int hwId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(StudentDbHelper.TABLE_HOMEWORK,
                StudentDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(hwId)});
        if (rows > 0) {
            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            loadData(); // 刷新页面
        } else {
            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}