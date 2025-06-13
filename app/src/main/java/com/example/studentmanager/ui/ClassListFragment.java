package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanager.adapter.ClassListAdapter;
import com.example.studentmanager.db.StudentDBHelper;
import com.example.studentmanager.model.Student;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ClassListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClassListAdapter adapter;
    private List<com.example.studentmanager.model.Class> classList;
    private StudentDBHelper dbHelper;
    private SearchView searchView;
    private ImageView ivAddClass;

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
        loadClassData(null);
    }

    private void initViews(View view) {
        ivAddClass = view.findViewById(R.id.iv_add_class);
        searchView = view.findViewById(R.id.search_view_class);
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

        adapter.setOnItemClickListener(aClass -> {
            Intent intent = new Intent(getActivity(), EditClassActivity.class);
            intent.putExtra("class_id", aClass.getClassId());
            intent.putExtra("class_name", aClass.getClassName());
            startActivity(intent);
        });

        adapter.setOnDeleteClickListener(aClass -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("确认删除")
                    .setMessage("您确定要删除班级 " + aClass.getClassName() + " 吗？这将同时删除该班级下的所有学生。")
                    .setPositiveButton("删除", (dialog, which) -> {
                        dbHelper.deleteClass(aClass.getClassId());
                        Toast.makeText(getContext(), "班级已删除", Toast.LENGTH_SHORT).show();
                        loadClassData(searchView.getQuery().toString());
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        ivAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClassActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadClassData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadClassData(newText);
                return false;
            }
        });
    }

    @SuppressLint("Range")
    private void loadClassData(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = null;
        String[] selectionArgs = null;

        if (query != null && !query.isEmpty()) {
            selection = "class_name LIKE ?";
            selectionArgs = new String[]{"%" + query + "%"};
        }

        Cursor cursor = db.query("classes", null, selection, selectionArgs, null, null, "class_id ASC");
        if (cursor != null) {
            classList.clear();
            while (cursor.moveToNext()) {
                com.example.studentmanager.model.Class aClass = new com.example.studentmanager.model.Class();
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
        loadClassData(null);
    }
} 