package com.example.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.Appointment;
import com.example.application.model.User;
import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private DatabaseHelper dbHelper;

    public AppointmentAdapter(Context context, List<Appointment> appointments, DatabaseHelper dbHelper) {
        super(context, 0, appointments);
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_appointment, parent, false);
        }

        Appointment appointment = getItem(position);

        TextView tvPatientName = convertView.findViewById(R.id.tv_patient_name);
        TextView tvPatientRole = convertView.findViewById(R.id.tv_patient_role);
        TextView tvDepartment = convertView.findViewById(R.id.tv_department);
        TextView tvAppointmentTime = convertView.findViewById(R.id.tv_appointment_time);
        TextView tvDescription = convertView.findViewById(R.id.tv_description);

        if (appointment != null) {
            User user = dbHelper.getUserById(appointment.getUserId());
            if (user != null) {
                tvPatientName.setText(user.getUsername());
                tvPatientRole.setText(user.getRole());
            } else {
                tvPatientName.setText("未知用户");
                tvPatientRole.setText("");
            }

            tvDepartment.setText(appointment.getDepartment());
            tvAppointmentTime.setText(appointment.getAppointmentTime());
            tvDescription.setText(appointment.getDescription());
        }

        return convertView;
    }
}