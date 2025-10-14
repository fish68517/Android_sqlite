package com.example.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.activity.AppointmentActivity;
import com.example.application.activity.DoctorHomeActivity;
import com.example.application.activity.ManageNotificationsActivity;
import com.example.application.activity.StudentCheckinStatusActivity;
import com.example.application.model.CheckIn;
import com.example.application.model.Notification;
import com.example.application.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvStepCount;
    private Button btnDailyCheckin, btnAppointment, btnHealthPass;
    private ListView lvNotifications;
    private DatabaseHelper dbHelper;

    private ArrayList<String> studentStatusList = new ArrayList<>();


    private int loggedInUserId = -1;
    private String userRole = ""; // 新增变量
    private ArrayList<Notification> notifications;
    private ImageView btnManageNotifications;
    private Button btnViewCheckins;

    @SuppressLint("MissingInflatedId")
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
        userRole = prefs.getString("LOGGED_IN_USER_ROLE", "学生");

        // 绑定控件
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvStepCount = view.findViewById(R.id.tv_step_count);
        btnDailyCheckin = view.findViewById(R.id.btn_daily_checkin);
        btnAppointment = view.findViewById(R.id.btn_appointment);
        btnHealthPass = view.findViewById(R.id.btn_health_pass);
        lvNotifications = view.findViewById(R.id.lv_notifications);
        btnManageNotifications = view.findViewById(R.id.btn_manage_notifications);
        btnViewCheckins = view.findViewById(R.id.btn_view_checkins);
        if (userRole.equals("医生") || userRole.equals("老师") || userRole.equals("管理员")) {
            btnViewCheckins.setVisibility(View.VISIBLE);
            if (userRole.equals("老师")) {
                btnViewCheckins.setText("查看学生打卡");
            } else if (userRole.equals("管理员")) {
                btnViewCheckins.setText("用户管理");
            }
            else {
                btnViewCheckins.setText("查看预约就诊");
            }
        } else {
            btnViewCheckins.setVisibility(View.GONE);
        }

        btnViewCheckins.setOnClickListener(v -> loadStudentCheckinStatus());

        // 设置欢迎语
        tvWelcome.setText("欢迎你, " + username);

        // 设置步数
        int randomSteps = new Random().nextInt(15000) + 500; // 500-15500的随机步数
        tvStepCount.setText("今日步数: " + randomSteps);

        // 设置按钮点击事件
        setupButtonClickListeners();

        checkUserRole(); // 新增方法调用

        // 加载的通知列表
        loadNotifications();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotificationsFromDb();
    }

    private void checkUserRole() {
        if ("老师".equals(userRole) || "医生".equals(userRole)) {
            btnManageNotifications.setVisibility(View.VISIBLE);
            btnManageNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ManageNotificationsActivity.class);
                startActivity(intent);
            });
        } else {
            btnManageNotifications.setVisibility(View.GONE);
        }
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
            // 跳转到新的预约Activity
            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
            startActivity(intent);
        });

        btnHealthPass.setOnClickListener(v -> {
            try {
                // 1. 随机决定是绿码还是红码
                // boolean isGreenCode = new Random().nextBoolean();
                boolean isGreenCode = true; // 这里暂时固定为绿码，方便测试

                // 准备对话框的自定义视图
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.dialog_qr_code, null);
                ImageView ivQrCode = dialogView.findViewById(R.id.ivQrCode);
                TextView tvPassMessage = dialogView.findViewById(R.id.tvPassMessage);

                // 2. 设置二维码和消息内容
                String title;
                String message;
                int qrCodeColor;

                if (isGreenCode) {
                    title = "通行码 (绿码)";
                    message = "绿码通行！";
                    qrCodeColor = Color.GREEN;
                    tvPassMessage.setTextColor(Color.GREEN);
                } else {
                    title = "通行码 (红码)";
                    message = "禁止通行！";
                    qrCodeColor = Color.RED;
                    tvPassMessage.setTextColor(Color.RED);
                }

                String currentTime = "当前时间: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                tvPassMessage.setText(String.format("%s\n%s", message, currentTime));


                // 3. 生成二维码
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                // 二维码内容可以自定义，这里我们使用消息和时间
                Bitmap bitmap = barcodeEncoder.encodeBitmap(
                        message + "\n" + currentTime,
                        BarcodeFormat.QR_CODE,
                        400,
                        400
                );

                // 创建一个新的Bitmap来应用颜色
                Bitmap coloredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                for (int x = 0; x < bitmap.getWidth(); x++) {
                    for (int y = 0; y < bitmap.getHeight(); y++) {
                        int pixel = bitmap.getPixel(x, y);
                        if (pixel == Color.BLACK) {
                            coloredBitmap.setPixel(x, y, qrCodeColor);
                        } else {
                            coloredBitmap.setPixel(x, y, Color.WHITE);
                        }
                    }
                }

                ivQrCode.setImageBitmap(coloredBitmap);

                // 4. 创建并显示 AlertDialog
                new AlertDialog.Builder(getContext())
                        .setTitle(title)
                        .setView(dialogView) // 设置自定义视图
                        .setPositiveButton("关闭", null)
                        .show();

            } catch (WriterException e) {
                e.printStackTrace();
                // 如果生成失败，显示一个错误消息
                new AlertDialog.Builder(getContext())
                        .setTitle("错误")
                        .setMessage("无法生成二维码: " + e.getMessage())
                        .setPositiveButton("关闭", null)
                        .show();
            }
        });
    }

    private void loadNotifications() {
        notifications = new ArrayList<>();
/*        notifications.add("通知: 本周五下午将进行校园卫生大扫除。");
        notifications.add("讲座: ‘如何健康饮食’讲座将于明晚7点在A栋101举行。");
        notifications.add("提醒: 近期天气转凉，请注意添加衣物。");*/

   /*     ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, notifications);
        lvNotifications.setAdapter(adapter);*/
    }

    // 修改：从数据库加载通知
    private void loadNotificationsFromDb() {
        notifications = dbHelper.getAllNotifications();
        // 为了在 ArrayAdapter 中显示，我们需要一个字符串列表,需要显示标题和内容
        List<String> notificationTitles = notifications.stream()
                .map(n -> n.getTitle() + "\n" + n.getContent())
                .collect(Collectors.toList());
      /*  List<String> notificationTitles = notifications.stream()
                .map(Notification::getTitle)
                .collect(Collectors.toList());*/


        notificationTitles.add("通知: 本周五下午将进行校园卫生大扫除。");
        notificationTitles.add("讲座: ‘如何健康饮食’讲座将于明晚7点在A栋101举行。");
        notificationTitles.add("提醒: 近期天气转凉，请注意添加衣物。");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, notificationTitles);
        lvNotifications.setAdapter(adapter);

        // 为通知列表添加点击事件，显示详细内容
        lvNotifications.setOnItemClickListener((parent, view, position, id) -> {
            Notification selectedNotification = notifications.get(position);
            new AlertDialog.Builder(getContext())
                    .setTitle(selectedNotification.getTitle())
                    .setMessage(selectedNotification.getContent())
                    .setPositiveButton("好的", null)
                    .show();
        });
    }

    private void loadStudentCheckinStatus() {
        if (userRole.equals("医生")) {
            // 预约就诊页面
            Intent intent = new Intent(getActivity(), DoctorHomeActivity.class);
            startActivity(intent);
        } else if (userRole.equals("管理员")) {
            Intent intent = new Intent(getActivity(), UserManageActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getActivity(), StudentCheckinStatusActivity.class);
            startActivity(intent);

        }

        /*studentStatusList.clear();
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
        adapter.notifyDataSetChanged();*/
    }
}