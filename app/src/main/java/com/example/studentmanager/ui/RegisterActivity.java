package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
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

import com.example.studentmanagement.R;
import com.example.studentmanager.db.StudentDBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etName;
    private AutoCompleteTextView actGender;
    private AutoCompleteTextView actClass;
    private TextInputEditText etAdmissionDate;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;
    private StudentDBHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private int selectedClassId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new StudentDBHelper(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        initViews();
        setupListeners();
        setupGenderDropdown();
        setupClassDropdown();
        setupDatePicker();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        actGender = findViewById(R.id.act_gender);
        actClass = findViewById(R.id.act_class);
        etAdmissionDate = findViewById(R.id.et_admission_date);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
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
        actClass.setOnItemClickListener((parent, view, position, id) -> selectedClassId = classIds.get(position));
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

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> saveStudent());
        findViewById(R.id.tv_login).setOnClickListener(v -> finish());
    }

    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String gender = actGender.getText().toString().trim();
        String admissionDate = etAdmissionDate.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gender) ||
            TextUtils.isEmpty(admissionDate) || selectedClassId == -1 ||
            TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
            TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUsernameExists(username)) {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("gender", gender);
        values.put("class_id", selectedClassId);
        values.put("admission_date", admissionDate);
        values.put("status", "在校");
        values.put("username", username);
        values.put("password", password);
        values.put("is_admin", 0);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("students", null, values);

        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "students",
                new String[]{"username"},
                "username = ?",
                new String[]{username},
                null, null, null
        );
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
} 