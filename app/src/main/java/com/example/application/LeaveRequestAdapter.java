package com.example.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.activity.LeaveApprovalActivity;
import com.example.application.model.LeaveRequest;
import com.example.application.model.User;
import java.util.List;

public class LeaveRequestAdapter extends ArrayAdapter<LeaveRequest> {

    private DatabaseHelper dbHelper;
    private Context mContext;

    public LeaveRequestAdapter(Context context, List<LeaveRequest> leaveRequests, DatabaseHelper dbHelper) {
        super(context, 0, leaveRequests);
        this.mContext = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_leave_request, parent, false);
        }

        LeaveRequest leaveRequest = getItem(position);

        TextView tvStudentName = convertView.findViewById(R.id.tv_leave_student_name);
        TextView tvLeaveTime = convertView.findViewById(R.id.tv_leave_time);
        TextView tvLeaveReason = convertView.findViewById(R.id.tv_leave_reason);
        TextView tvStatus = convertView.findViewById(R.id.tv_leave_status);
        Button btnApprove = convertView.findViewById(R.id.btn_approve);
        Button btnReject = convertView.findViewById(R.id.btn_reject);

        if (leaveRequest != null) {
            User student = dbHelper.getUserById(leaveRequest.getUserId());
            tvStudentName.setText(student != null ? student.getUsername() : "未知学生");
            tvLeaveTime.setText("时间: " + leaveRequest.getLeaveTime());
            tvLeaveReason.setText("原因: " + leaveRequest.getReason());
            tvStatus.setText("状态: " + leaveRequest.getStatus());

            // 根据状态决定是否显示按钮
            if (LeaveRequest.STATUS_PENDING.equals(leaveRequest.getStatus())) {
                btnApprove.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
            } else {
                btnApprove.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
            }

            btnApprove.setOnClickListener(v -> {
                leaveRequest.setStatus(LeaveRequest.STATUS_APPROVED);
                dbHelper.updateLeaveRequestStatus(leaveRequest);
                Toast.makeText(mContext, "已批准", Toast.LENGTH_SHORT).show();
                ((LeaveApprovalActivity) mContext).loadLeaveRequests(); // 刷新列表
            });

            btnReject.setOnClickListener(v -> {
                leaveRequest.setStatus(LeaveRequest.STATUS_REJECTED);
                dbHelper.updateLeaveRequestStatus(leaveRequest);
                Toast.makeText(mContext, "已拒绝", Toast.LENGTH_SHORT).show();
                ((LeaveApprovalActivity) mContext).loadLeaveRequests(); // 刷新列表
            });
        }
        return convertView;
    }
}