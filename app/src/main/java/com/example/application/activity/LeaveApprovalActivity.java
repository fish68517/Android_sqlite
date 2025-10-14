package com.example.application.activity;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.application.DatabaseHelper;
import com.example.application.LeaveRequestAdapter;
import com.example.application.R;

import com.example.application.model.LeaveRequest;
import java.util.List;

public class LeaveApprovalActivity extends AppCompatActivity {

    private ListView lvLeaveRequests;
    private DatabaseHelper dbHelper;
    private LeaveRequestAdapter adapter;
    private List<LeaveRequest> leaveRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_approval);

        dbHelper = new DatabaseHelper(this);
        lvLeaveRequests = findViewById(R.id.lv_leave_requests);

        loadLeaveRequests();
    }

    public void loadLeaveRequests() {
        leaveRequestList = dbHelper.getAllLeaveRequests();
        adapter = new LeaveRequestAdapter(this, leaveRequestList, dbHelper);
        lvLeaveRequests.setAdapter(adapter);
    }
}