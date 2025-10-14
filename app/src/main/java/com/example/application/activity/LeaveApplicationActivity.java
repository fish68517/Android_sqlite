package com.example.application.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.LeaveRequest;

public class LeaveApplicationActivity extends AppCompatActivity {

    private EditText etLeaveTime, etLeaveReason;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;
    private int loggedInUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        loggedInUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        etLeaveTime = findViewById(R.id.et_leave_time);
        etLeaveReason = findViewById(R.id.et_leave_reason);
        btnSubmit = findViewById(R.id.btn_submit_leave_request);

        btnSubmit.setOnClickListener(v -> submitLeaveRequest());
    }

    private void submitLeaveRequest() {
        String leaveTime = etLeaveTime.getText().toString().trim();
        String reason = etLeaveReason.getText().toString().trim();

        if (leaveTime.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            return;
        }

        LeaveRequest lr = new LeaveRequest();
        lr.setUserId(loggedInUserId);
        lr.setLeaveTime(leaveTime);
        lr.setReason(reason);
        lr.setStatus(LeaveRequest.STATUS_PENDING); // 初始状态为“待审批”

        long result = dbHelper.addLeaveRequest(lr);

        if (result != -1) {
            Toast.makeText(this, "请假申请已提交", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}