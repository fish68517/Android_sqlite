package com.myapplication.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myapplication.app.activity.AddCourseActivity;
import com.myapplication.app.db.StudentDbHelper;

import java.util.ArrayList;

public class CourseFragment extends Fragment {

    private StudentDbHelper dbHelper;
    private ListView listView;

    // courseList 用于显示在界面上的文字
    private ArrayList<String> courseList;
    // idList 用于存储对应数据的数据库主键ID，与 courseList 一一对应
    private ArrayList<Integer> idList;

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        dbHelper = new StudentDbHelper(getContext());
        listView = view.findViewById(R.id.list_view_course);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_course);

        courseList = new ArrayList<>();
        idList = new ArrayList<>(); // 初始化ID列表

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, courseList);
        listView.setAdapter(adapter);

        // 1. 点击悬浮按钮：跳转到添加页面 (ID 为 -1 表示添加)
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCourseActivity.class);
            intent.putExtra("course_id", -1);
            startActivity(intent);
        });

        // 2. 点击列表项：跳转到编辑页面
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            int courseId = idList.get(position); // 获取点击项的数据库ID
            Intent intent = new Intent(getActivity(), AddCourseActivity.class);
            intent.putExtra("course_id", courseId); // 传递ID过去
            startActivity(intent);
        });

        // 3. 长按列表项：删除功能
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            int courseId = idList.get(position); // 获取要删除的ID
            showDeleteDialog(courseId);
            return true; // 返回true表示事件已处理，不会触发点击事件
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 每次页面回到前台时刷新数据
    }

    // 从数据库加载数据
    private void loadData() {
        courseList.clear();
        idList.clear(); // 清空ID列表

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 查询所有数据
        Cursor cursor = db.query(StudentDbHelper.TABLE_COURSE, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            // 获取 ID
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDbHelper.COLUMN_ID));
            // 获取其他信息
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_COURSE_NAME));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_COURSE_TIME));
            String place = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_COURSE_PLACE));

            // 分别存入两个列表
            idList.add(id);
            courseList.add(name + "\n" + time + " | " + place);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // 显示删除确认弹窗
    private void showDeleteDialog(int courseId) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除课程")
                .setMessage("确定要删除这就课程信息吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteCourse(courseId))
                .setNegativeButton("取消", null)
                .show();
    }

    // 执行删除操作
    private void deleteCourse(int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 根据主键ID删除
        int rows = db.delete(StudentDbHelper.TABLE_COURSE,
                StudentDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(courseId)});

        if (rows > 0) {
            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            loadData(); // 刷新列表
        } else {
            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}