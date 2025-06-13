package com.example.studentmanager.ui;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class AddStudentActivity extends AppCompatActivity {
    private TextInputEditText etName;
    private AutoCompleteTextView actGender;
    private AutoCompleteTextView actClass;
    private TextInputEditText etAdmissionDate;
    private MaterialButton btnSave;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private int selectedClassId = -1;
    private StudentDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        dbHelper = new StudentDBHelper(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        initViews();
        setupGenderDropdown();
        setupClassDropdown();
        setupDatePicker();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.et_name);
        actGender = findViewById(R.id.act_gender);
        actClass = findViewById(R.id.act_class);
        etAdmissionDate = findViewById(R.id.et_admission_date);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> saveStudent());
    }

    private void setupGenderDropdown() {
        String[] genders = new String[]{"男", "女"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        actGender.setAdapter(adapter);
    }

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

    private void setupDatePicker() {
        etAdmissionDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        etAdmissionDate.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String gender = actGender.getText().toString().trim();
        String admissionDate = etAdmissionDate.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gender) || 
            TextUtils.isEmpty(admissionDate) || selectedClassId == -1) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("gender", gender);
        values.put("class_id", selectedClassId);
        values.put("admission_date", admissionDate);
        values.put("status", "在校");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("students", null, values);
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
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