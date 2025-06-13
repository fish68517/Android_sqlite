package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanager.db.StudentDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StudentDetailActivity extends AppCompatActivity {
    private TextView tvStudentId;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvClass;
    private TextView tvAdmissionDate;
    private TextView tvGraduationDate;
    private TextView tvStatus;
    private FloatingActionButton fabEdit;
    private int studentId;
    private StudentDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        studentId = getIntent().getIntExtra("student_id", -1);
        if (studentId == -1) {
            finish();
            return;
        }

        dbHelper = new StudentDBHelper(this);
        initViews();
        loadStudentData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvStudentId = findViewById(R.id.tv_student_id);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvClass = findViewById(R.id.tv_class);
        tvAdmissionDate = findViewById(R.id.tv_admission_date);
        tvGraduationDate = findViewById(R.id.tv_graduation_date);
        tvStatus = findViewById(R.id.tv_status);
        fabEdit = findViewById(R.id.fab_edit);

        fabEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditStudentActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });
    }

    @SuppressLint("Range")
    private void loadStudentData() {
        // 直接通过 StudentDBHelper 查询学生信息
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "students",
                null,
                "student_id = ?",
                new String[]{String.valueOf(studentId)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            tvStudentId.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("student_id"))));
            tvName.setText(cursor.getString(cursor.getColumnIndex("name")));
            tvGender.setText(cursor.getString(cursor.getColumnIndex("gender")));

            // 获取班级信息
            int classId = cursor.getInt(cursor.getColumnIndex("class_id"));
            Cursor classCursor = db.query(
                    "classes",
                    new String[]{"class_name"},
                    "class_id = ?",
                    new String[]{String.valueOf(classId)},
                    null, null, null
            );
            if (classCursor != null && classCursor.moveToFirst()) {
                tvClass.setText(classCursor.getString(0));
                classCursor.close();
            }

            tvAdmissionDate.setText(cursor.getString(cursor.getColumnIndex("admission_date")));
            String graduationDate = cursor.getString(cursor.getColumnIndex("graduation_date"));
            tvGraduationDate.setText(graduationDate != null ? graduationDate : "未毕业");
            tvStatus.setText(cursor.getString(cursor.getColumnIndex("status")));

            cursor.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentData(); // 刷新数据
    }
}