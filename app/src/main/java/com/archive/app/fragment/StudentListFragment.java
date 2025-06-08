package com.archive.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.adapter.StudentAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Student;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList = new ArrayList<>();
    private OpenHelperDataBase dbHelper;
    private TextView emptyView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        dbHelper = new OpenHelperDataBase(getContext());

        recyclerView = view.findViewById(R.id.recycler_view_students);
        emptyView = view.findViewById(R.id.text_view_empty);
        fab = view.findViewById(R.id.fab_add_student);

        setupRecyclerView();
        loadStudents();

        fab.setOnClickListener(v -> {
            // Navigate to DetailFragment for adding a new student
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StudentDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentAdapter = new StudentAdapter(studentList, student -> {
            // Navigate to DetailFragment for editing
            StudentDetailFragment detailFragment = StudentDetailFragment.newInstance(student.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(studentAdapter);
    }

    private void loadStudents() {
        studentList.clear();
        studentList.addAll(dbHelper.getAllStudents());
        studentAdapter.notifyDataSetChanged();

        if (studentList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload students when fragment is resumed, to reflect changes
        loadStudents();
    }
} 