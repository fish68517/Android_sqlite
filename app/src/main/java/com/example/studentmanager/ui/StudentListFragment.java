package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanager.adapter.StudentAdapter;
import com.example.studentmanager.db.StudentDBHelper;
import com.example.studentmanager.model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment implements StudentAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private StudentDBHelper dbHelper;
    private SearchView searchView;
    private FloatingActionButton fabAddStudent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new StudentDBHelper(getActivity());
        return inflater.inflate(R.layout.fragment_student_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadData(null);
    }

    private void initViews(View view) {
        fabAddStudent = view.findViewById(R.id.fab_add_student);
        searchView = view.findViewById(R.id.search_view_student);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(getContext(), studentList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(student -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("确认删除")
                    .setMessage("您确定要删除学生 " + student.getName() + " 吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        dbHelper.deleteStudent(student.getStudentId());
                        Toast.makeText(getContext(), "学生已删除", Toast.LENGTH_SHORT).show();
                        loadData(searchView.getQuery().toString());
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        fabAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddStudentActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadData(newText);
                return false;
            }
        });
    }

    @SuppressLint("Range")
    private void loadData(String query) {
        // 直接通过 StudentDBHelper 查询所有学生
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = null;
        String[] selectionArgs = null;
        if (query != null && !query.isEmpty()) {
            selection = "name LIKE ?";
            selectionArgs = new String[]{"%" + query + "%"};
        }

        Cursor cursor = db.query(
                "students",
                null,
                selection,
                selectionArgs,
                null,
                null,
                "student_id DESC"
        );

        if (cursor != null) {
            studentList.clear();
            while (cursor.moveToNext()) {
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndex("student_id")));
                student.setName(cursor.getString(cursor.getColumnIndex("name")));
                student.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                student.setClassId(cursor.getInt(cursor.getColumnIndex("class_id")));
                student.setAdmissionDate(cursor.getString(cursor.getColumnIndex("admission_date")));
                student.setGraduationDate(cursor.getString(cursor.getColumnIndex("graduation_date")));
                student.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                studentList.add(student);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(Student student) {
        // 跳转到学生详情页面
        Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
        intent.putExtra("student_id", student.getStudentId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(null); // 刷新数据
    }
} 