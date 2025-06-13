package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanager.adapter.StudentAdapter;
import com.example.studentmanager.db.StudentDBHelper;
import com.example.studentmanager.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment implements StudentAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private StudentDBHelper dbHelper;

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
        loadData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(getContext(), studentList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private void loadData() {
        // 直接通过 StudentDBHelper 查询所有学生
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "students",
                null,
                null,
                null,
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
        loadData(); // 刷新数据
    }
} 