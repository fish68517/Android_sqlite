package com.archive.app.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.model.Student;
import com.example.myapplication.R;

import java.io.File;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public StudentAdapter(List<Student> studentList, OnItemClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student currentStudent = studentList.get(position);
        holder.nameTextView.setText(currentStudent.getName());
        holder.idTextView.setText(currentStudent.getStudentId());

        if (currentStudent.getAvatarPath() != null && !currentStudent.getAvatarPath().isEmpty()) {
            holder.avatarImageView.setImageURI(Uri.fromFile(new File(currentStudent.getAvatarPath())));
        } else {
            holder.avatarImageView.setImageResource(R.drawable.ic_avatar_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentStudent));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        TextView idTextView;

        StudentViewHolder(View view) {
            super(view);
            avatarImageView = view.findViewById(R.id.image_view_avatar);
            nameTextView = view.findViewById(R.id.text_view_name);
            idTextView = view.findViewById(R.id.text_view_student_id);
        }
    }
} 