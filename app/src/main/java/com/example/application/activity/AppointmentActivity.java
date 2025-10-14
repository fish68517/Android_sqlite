package com.example.application.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.Appointment;

public class AppointmentActivity extends AppCompatActivity {

    private Spinner spinnerDepartment;
    private EditText etAppointmentTime, etDescription;
    private Button btnSubmitAppointment;
    private DatabaseHelper dbHelper;
    private int loggedInUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        loggedInUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        spinnerDepartment = findViewById(R.id.spinner_department);
        etAppointmentTime = findViewById(R.id.et_appointment_time);
        etDescription = findViewById(R.id.et_description);
        btnSubmitAppointment = findViewById(R.id.btn_submit_appointment);

        // 设置科室下拉框
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.department_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(adapter);

        btnSubmitAppointment.setOnClickListener(v -> submitAppointment());
    }

    private void submitAppointment() {
        String department = spinnerDepartment.getSelectedItem().toString();
        String appointmentTime = etAppointmentTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (appointmentTime.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setUserId(loggedInUserId);
        appointment.setDepartment(department);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setDescription(description);

        long result = dbHelper.addAppointment(appointment);

        if (result != -1) {
            Toast.makeText(this, "预约提交成功！", Toast.LENGTH_SHORT).show();
            finish(); // 成功后关闭页面
        } else {
            Toast.makeText(this, "预约失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}