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
import androidx.fragment.app.Fragment;

import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.Appointment;
import com.example.application.model.User;

import java.util.ArrayList;
import java.util.List;

public class DoctorHomeFragmentNew extends Fragment {

    private TextView tvWelcome;
    private Button btnViewAppointments, btnPrescribe, btnReportEpidemic;
    private ListView lvAppointments;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> appointmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("LOGGED_IN_USERNAME", "医生");

        tvWelcome = view.findViewById(R.id.tv_doctor_welcome);
        btnViewAppointments = view.findViewById(R.id.btn_view_appointments);
        btnPrescribe = view.findViewById(R.id.btn_prescribe);
        btnReportEpidemic = view.findViewById(R.id.btn_report_epidemic);
        lvAppointments = view.findViewById(R.id.lv_appointments);

        tvWelcome.setText("欢迎你, " + username);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, appointmentList);
        lvAppointments.setAdapter(adapter);

        btnViewAppointments.setOnClickListener(v -> loadAppointments());
        btnPrescribe.setOnClickListener(v -> Toast.makeText(getContext(), "该功能正在开发中", Toast.LENGTH_SHORT).show());
        btnReportEpidemic.setOnClickListener(v -> Toast.makeText(getContext(), "该功能正在开发中", Toast.LENGTH_SHORT).show());

        // Automatically load appointments on fragment creation
        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        appointmentList.clear();
        List<Appointment> allAppointments = dbHelper.getAllAppointments();

        if (allAppointments.isEmpty()) {
            appointmentList.add("当前没有预约记录");
        } else {
            for (Appointment appointment : allAppointments) {
                User student = dbHelper.getUserById(appointment.getUserId());
                String studentName = (student != null) ? student.getUsername() : "未知学生";

                String appointmentInfo = "学生: " + studentName +
                        "\n科室: " + appointment.getDepartment() +
                        "\n时间: " + appointment.getAppointmentTime() +
                        "\n病情描述: " + appointment.getDescription();
                appointmentList.add(appointmentInfo);
            }
        }
        adapter.notifyDataSetChanged();
    }
}