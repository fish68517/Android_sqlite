package com.example.sensecheck.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensecheck.R;
import com.example.sensecheck.data.AppPreferences;

public final class ProfileActivity extends Activity {
    private AppPreferences preferences;
    private EditText studentNoView;
    private EditText nameView;
    private EditText classNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferences = new AppPreferences(this);

        studentNoView = findViewById(R.id.etStudentNo);
        nameView = findViewById(R.id.etName);
        classNameView = findViewById(R.id.etClassName);
        TextView deviceIdView = findViewById(R.id.tvDeviceId);

        studentNoView.setText(preferences.getStudentNo());
        nameView.setText(preferences.getName());
        classNameView.setText(preferences.getClassName());
        deviceIdView.setText(preferences.getDeviceId());

        findViewById(R.id.btnSaveProfile).setOnClickListener(view -> saveProfile());
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
    }

    private void saveProfile() {
        String studentNo = studentNoView.getText().toString().trim();
        String name = nameView.getText().toString().trim();
        String className = classNameView.getText().toString().trim();
        if (TextUtils.isEmpty(studentNo) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "学号和姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        preferences.saveProfile(studentNo, name, className);
        Toast.makeText(this, "个人信息已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}

