package com.example.application.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentCheckinStatusActivity extends AppCompatActivity {

    private TextView tvSummary;
    private ListView lvCheckedIn, lvNotCheckedIn;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> checkedInAdapter;
    private ArrayAdapter<String> notCheckedInAdapter;
    private ArrayList<String> checkedInList = new ArrayList<>();
    private ArrayList<String> notCheckedInList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_checkin_status);

        dbHelper = new DatabaseHelper(this);
        tvSummary = findViewById(R.id.tv_checkin_summary);
        lvCheckedIn = findViewById(R.id.lv_checked_in);
        lvNotCheckedIn = findViewById(R.id.lv_not_checked_in);

        checkedInAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, checkedInList);
        notCheckedInAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notCheckedInList);

        lvCheckedIn.setAdapter(checkedInAdapter);
        lvNotCheckedIn.setAdapter(notCheckedInAdapter);

        loadStudentStatus();
    }

    private void loadStudentStatus() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        List<User> allStudents = dbHelper.getAllStudents(); // 调用接口1
        List<Integer> checkedInUserIds = dbHelper.getTodayCheckInUserIds(today); // 调用接口2

        checkedInList.clear();
        notCheckedInList.clear();

        for (User student : allStudents) {
            if (checkedInUserIds.contains(student.getId())) {
                checkedInList.add("学生：" + student.getUsername() + " - 已打卡");
            } else {
                notCheckedInList.add("学生：" + student.getUsername() + " - 未打卡");
            }
        }

        // 更新概要信息
        String summary = "已打卡: " + checkedInUserIds.size() + " / " + allStudents.size();
        tvSummary.setText(summary);

        // 刷新列表
        checkedInAdapter.notifyDataSetChanged();
        notCheckedInAdapter.notifyDataSetChanged();
    }
}