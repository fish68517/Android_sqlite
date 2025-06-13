package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentmanagement.R;
import com.example.studentmanager.db.StudentDBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditStudentActivity extends AppCompatActivity {
    private TextInputEditText etName;
    private AutoCompleteTextView actGender;
    private AutoCompleteTextView actClass;
    private TextInputEditText etAdmissionDate;
    private TextInputEditText etGraduationDate;
    private AutoCompleteTextView actStatus;
    private MaterialButton btnSave;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private int studentId;
    private int selectedClassId = -1;
    private StudentDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        studentId = getIntent().getIntExtra("student_id", -1);
        if (studentId == -1) {
            finish();
            return;
        }

        dbHelper = new StudentDBHelper(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        initViews();
        setupGenderDropdown();
        setupClassDropdown();
        setupStatusDropdown();
        setupDatePickers();
        loadStudentData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.et_name);
        actGender = findViewById(R.id.act_gender);
        actClass = findViewById(R.id.act_class);
        etAdmissionDate = findViewById(R.id.et_admission_date);
        etGraduationDate = findViewById(R.id.et_graduation_date);
        actStatus = findViewById(R.id.act_status);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> saveStudent());
    }

    private void setupGenderDropdown() {
        String[] genders = new String[]{"男", "女"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        actGender.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private void setupClassDropdown() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("classes", new String[]{"class_id", "class_name"}, null, null, null, null, null);
        List<String> classNames = new ArrayList<>();
        final List<Integer> classIds = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                classNames.add(cursor.getString(cursor.getColumnIndex("class_name")));
                classIds.add(cursor.getInt(cursor.getColumnIndex("class_id")));
            }
            cursor.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        actClass.setAdapter(adapter);
        actClass.setOnItemClickListener((parent, view, position, id) -> {
            selectedClassId = classIds.get(position);
        });
    }

    private void setupStatusDropdown() {
        String[] statuses = new String[]{"在校", "毕业", "休学", "退学"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        actStatus.setAdapter(adapter);
    }

    private void setupDatePickers() {
        etAdmissionDate.setOnClickListener(v -> showDatePicker(etAdmissionDate));
        etGraduationDate.setOnClickListener(v -> showDatePicker(etGraduationDate));
    }

    private void showDatePicker(TextInputEditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
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
            etName.setText(cursor.getString(cursor.getColumnIndex("name")));
            actGender.setText(cursor.getString(cursor.getColumnIndex("gender")), false);
            int classId = cursor.getInt(cursor.getColumnIndex("class_id"));
            selectedClassId = classId;
            // 获取班级名称
            Cursor classCursor = db.query(
                    "classes",
                    new String[]{"class_name"},
                    "class_id = ?",
                    new String[]{String.valueOf(classId)},
                    null, null, null
            );
            if (classCursor != null && classCursor.moveToFirst()) {
                actClass.setText(classCursor.getString(0), false);
                classCursor.close();
            }
            etAdmissionDate.setText(cursor.getString(cursor.getColumnIndex("admission_date")));
            String graduationDate = cursor.getString(cursor.getColumnIndex("graduation_date"));
            if (graduationDate != null) {
                etGraduationDate.setText(graduationDate);
            }
            actStatus.setText(cursor.getString(cursor.getColumnIndex("status")), false);
            cursor.close();
        }
    }

    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String gender = actGender.getText().toString().trim();
        String admissionDate = etAdmissionDate.getText().toString().trim();
        String graduationDate = etGraduationDate.getText().toString().trim();
        String status = actStatus.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(admissionDate) || TextUtils.isEmpty(status) ||
                selectedClassId == -1) {
            Toast.makeText(this, "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("gender", gender);
        values.put("class_id", selectedClassId);
        values.put("admission_date", admissionDate);
        values.put("graduation_date", graduationDate);
        values.put("status", status);

        // 直接通过 StudentDBHelper 更新学生信息
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update("students", values, "student_id = ?", new String[]{String.valueOf(studentId)});
        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
