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
import com.example.studentmanager.adapter.ClassListAdapter;
import com.example.studentmanager.db.StudentDBHelper;
import com.example.studentmanager.model.Class;
import com.example.studentmanager.model.Student;

import java.util.ArrayList;
import java.util.List;

public class ClassListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClassListAdapter adapter;
    private List<Class> classList;
    private StudentDBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new StudentDBHelper(getContext());
        return inflater.inflate(R.layout.fragment_class_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadClassData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.class_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classList = new ArrayList<>();
        adapter = new ClassListAdapter(getContext(), classList);
        adapter.setOnStudentClickListener(student -> {
            Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
            intent.putExtra("student_id", student.getStudentId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private void loadClassData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("classes", null, null, null, null, null, "class_id ASC");
        if (cursor != null) {
            classList.clear();
            while (cursor.moveToNext()) {
                Class aClass = new Class();
                aClass.setClassId(cursor.getInt(cursor.getColumnIndex("class_id")));
                aClass.setClassName(cursor.getString(cursor.getColumnIndex("class_name")));
                aClass.setClassType(cursor.getString(cursor.getColumnIndex("class_type")));
                classList.add(aClass);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClassData();
    }
} 