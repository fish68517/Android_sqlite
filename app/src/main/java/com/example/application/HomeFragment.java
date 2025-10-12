package com.example.application;

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

import com.example.application.R;
import com.example.application.model.CheckIn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvStepCount;
    private Button btnDailyCheckin, btnAppointment, btnHealthPass;
    private ListView lvNotifications;
    private DatabaseHelper dbHelper;
    private int loggedInUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化数据库助手
        dbHelper = new DatabaseHelper(getContext());

        // 获取登录用户ID
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        loggedInUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);
        String username = prefs.getString("LOGGED_IN_USERNAME", "用户");

        // 绑定控件
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvStepCount = view.findViewById(R.id.tv_step_count);
        btnDailyCheckin = view.findViewById(R.id.btn_daily_checkin);
        btnAppointment = view.findViewById(R.id.btn_appointment);
        btnHealthPass = view.findViewById(R.id.btn_health_pass);
        lvNotifications = view.findViewById(R.id.lv_notifications);

        // 设置欢迎语
        tvWelcome.setText("欢迎你, " + username);

        // 设置模拟步数
        int randomSteps = new Random().nextInt(15000) + 500; // 500-15500的随机步数
        tvStepCount.setText("今日步数: " + randomSteps);

        // 设置按钮点击事件
        setupButtonClickListeners();

        // 加载模拟的通知列表
        loadNotifications();

        return view;
    }

    private void setupButtonClickListeners() {
        btnDailyCheckin.setOnClickListener(v -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            CheckIn checkIn = new CheckIn();
            checkIn.setUserId(loggedInUserId);
            checkIn.setCheckinDate(currentDate);
            dbHelper.addCheckIn(checkIn);
            Toast.makeText(getContext(), "今日打卡成功！", Toast.LENGTH_SHORT).show();
        });

        btnAppointment.setOnClickListener(v -> {
            // 这里用一个简单的对话框来模拟预约，你也可以创建一个新的Fragment或Activity
            new AlertDialog.Builder(getContext())
                    .setTitle("预约就诊")
                    .setMessage("此功能为演示，点击确定将模拟一次预约。")
                    .setPositiveButton("确定", (dialog, which) -> {
                        Toast.makeText(getContext(), "预约信息已提交", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        btnHealthPass.setOnClickListener(v -> {
            // 显示一个静态的通行码图片
            new AlertDialog.Builder(getContext())
                    .setTitle("通行码 (模拟)")
                    .setMessage("绿码通行！\n当前时间：" + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()))
                    .setPositiveButton("关闭", null)
                    .show();
        });
    }

    private void loadNotifications() {
        ArrayList<String> notifications = new ArrayList<>();
        notifications.add("通知: 本周五下午将进行校园卫生大扫除。");
        notifications.add("讲座: ‘如何健康饮食’讲座将于明晚7点在A栋101举行。");
        notifications.add("提醒: 近期天气转凉，请注意添加衣物。");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, notifications);
        lvNotifications.setAdapter(adapter);
    }
}