package com.example.application.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.AppointmentAdapter;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.Appointment;
import java.util.List;

public class DoctorHomeFragment extends Fragment {

    private TextView tvWelcome;
    private ListView lvAppointments;
    private DatabaseHelper dbHelper;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("LOGGED_IN_USERNAME", "医生");

        tvWelcome = view.findViewById(R.id.tv_doctor_welcome);
        lvAppointments = view.findViewById(R.id.lv_appointments);
        tvWelcome.setText("欢迎你, " + username);

        loadAppointments();
        return view;
    }

    private void loadAppointments() {
        appointmentList = dbHelper.getAllAppointments();
        adapter = new AppointmentAdapter(getContext(), appointmentList, dbHelper);
        lvAppointments.setAdapter(adapter);
    }
}