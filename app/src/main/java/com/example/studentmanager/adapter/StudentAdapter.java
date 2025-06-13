package com.example.studentmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanager.model.Student;

import java.util.List;

/**
 * 学生列表适配器
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList;
    private Context context;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteClickListener;

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Student student);
    }

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        
        // 设置学生头像（这里使用默认头像）
        holder.ivAvatar.setImageResource(R.drawable.ic_avator);
        
        // 设置学生信息
        holder.tvName.setText(student.getName());
        holder.tvGender.setText(student.getGender());
        holder.tvStatus.setText(student.getStatus());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(student);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // 更新数据
    public void updateData(List<Student> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }

    // ViewHolder类
    static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvGender;
        TextView tvStatus;
        ImageView ivDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvGender = itemView.findViewById(R.id.tv_gender);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivDelete = itemView.findViewById(R.id.iv_delete_student);
        }
    }
} 