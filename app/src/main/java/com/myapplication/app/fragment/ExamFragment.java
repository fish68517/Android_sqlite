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
import com.myapplication.app.activity.AddExamActivity;
import com.myapplication.app.db.StudentDbHelper;

import java.util.ArrayList;

public class ExamFragment extends Fragment {

    private StudentDbHelper dbHelper;
    private ListView listView;

    // 存储显示的文本
    private ArrayList<String> examList;
    // 存储对应的数据库主键 ID
    private ArrayList<Integer> idList;

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam, container, false);

        dbHelper = new StudentDbHelper(getContext());
        listView = view.findViewById(R.id.list_view_exam);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_exam);

        examList = new ArrayList<>();
        idList = new ArrayList<>(); // 初始化 ID 列表

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, examList);
        listView.setAdapter(adapter);

        // 1. 点击 FAB：添加模式 (ID 为 -1)
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExamActivity.class);
            intent.putExtra("exam_id", -1);
            startActivity(intent);
        });

        // 2. 点击列表项：编辑模式 (传递选中的 ID)
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            int examId = idList.get(position);
            Intent intent = new Intent(getActivity(), AddExamActivity.class);
            intent.putExtra("exam_id", examId);
            startActivity(intent);
        });

        // 3. 长按列表项：删除确认
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            int examId = idList.get(position);
            showDeleteDialog(examId);
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
        examList.clear();
        idList.clear(); // 清空 ID 列表

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_EXAM, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            // 获取 ID
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDbHelper.COLUMN_ID));
            // 获取内容
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_EXAM_NAME));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_EXAM_TIME));

            idList.add(id);
            examList.add("科目: " + name + "\n时间: " + time);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(int examId) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除考试")
                .setMessage("确定要删除这条考试信息吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteExam(examId))
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteExam(int examId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(StudentDbHelper.TABLE_EXAM,
                StudentDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(examId)});
        if (rows > 0) {
            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            loadData();
        } else {
            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}