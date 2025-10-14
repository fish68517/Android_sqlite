package com.example.application.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // 导入 AppCompatActivity

import com.example.application.AppointmentAdapter;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.Appointment; // 确保导入路径正确
import java.util.List;

// 1. 类继承自 AppCompatActivity 而不是 Fragment
public class DoctorHomeActivity extends AppCompatActivity {

    // 成员变量保持不变
    private TextView tvWelcome;
    private ListView lvAppointments;
    private DatabaseHelper dbHelper;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    // 2. 重写 onCreate 方法，而不是 onCreateView
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 3. 使用 setContentView 来设置布局文件
        // 建议将 fragment_doctor_home.xml 重命名为 activity_doctor_home.xml 以符合规范，但也可以直接使用
        setContentView(R.layout.activity_doctor_home);

        // 4. 在 Activity 中，可以直接使用 'this' 作为 Context
        dbHelper = new DatabaseHelper(this);

        // 5. getSharedPreferences 可以直接调用，无需 getActivity()
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("LOGGED_IN_USERNAME", "医生");

        // 6. findViewById 直接在 Activity 中调用，无需通过 view 对象
        tvWelcome = findViewById(R.id.tv_doctor_welcome);
        lvAppointments = findViewById(R.id.lv_appointments);

        tvWelcome.setText("欢迎你, " + username);

        // 调用加载数据的方法
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentList = dbHelper.getAllAppointments();
        // 7. 同样，这里使用 'this' 作为上下文
        adapter = new AppointmentAdapter(this, appointmentList, dbHelper);
        lvAppointments.setAdapter(adapter);
    }
}