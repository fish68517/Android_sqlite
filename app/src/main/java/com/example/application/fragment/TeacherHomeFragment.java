package com.example.application.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeacherHomeFragment extends Fragment {

    private TextView tvWelcome;
    private Button btnViewCheckins, btnApproveLeave, btnSendNotification;
    private ListView lvStudentCheckinStatus;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> studentStatusList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("LOGGED_IN_USERNAME", "老师");

        tvWelcome = view.findViewById(R.id.tv_teacher_welcome);
        btnViewCheckins = view.findViewById(R.id.btn_view_checkins);
        btnApproveLeave = view.findViewById(R.id.btn_approve_leave);
        btnSendNotification = view.findViewById(R.id.btn_send_notification);
        lvStudentCheckinStatus = view.findViewById(R.id.lv_student_checkin_status);

        tvWelcome.setText("欢迎你, " + username);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, studentStatusList);
        lvStudentCheckinStatus.setAdapter(adapter);

        btnViewCheckins.setOnClickListener(v -> loadStudentCheckinStatus());
        btnApproveLeave.setOnClickListener(v -> Toast.makeText(getContext(), "该功能正在开发中", Toast.LENGTH_SHORT).show());
        btnSendNotification.setOnClickListener(v -> Toast.makeText(getContext(), "该功能正在开发中", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void loadStudentCheckinStatus() {
        studentStatusList.clear();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        List<User> allStudents = dbHelper.getAllStudents();
        List<Integer> checkedInUserIds = dbHelper.getTodayCheckInUserIds(today);

        for (User student : allStudents) {
            if (checkedInUserIds.contains(student.getId())) {
                studentStatusList.add("学生：" + student.getUsername() + " - 今日已打卡");
            } else {
                studentStatusList.add("学生：" + student.getUsername() + " - 今日未打卡");
            }
        }

        if(allStudents.isEmpty()){
            studentStatusList.add("暂无学生信息");
        }

        adapter.notifyDataSetChanged();
    }
}