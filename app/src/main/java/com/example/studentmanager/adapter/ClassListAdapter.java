package com.example.studentmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanager.db.StudentDBHelper;
import com.example.studentmanager.model.Class;
import com.example.studentmanager.model.Student;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder> {

    private final Context context;
    private final List<Class> classList;
    private final StudentDBHelper dbHelper;
    private final Map<Integer, Boolean> expandedState = new HashMap<>();
    private OnStudentClickListener onStudentClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnItemClickListener onItemClickListener;

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Class aClass);
    }

    public interface OnItemClickListener {
        void onItemClick(Class aClass);
    }

    public void setOnStudentClickListener(OnStudentClickListener listener) {
        this.onStudentClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ClassListAdapter(Context context, List<Class> classList) {
        this.context = context;
        this.classList = classList;
        this.dbHelper = new StudentDBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class_expandable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Class currentClass = classList.get(position);
        holder.tvClassName.setText(currentClass.getClassName());

        boolean isExpanded = expandedState.getOrDefault(currentClass.getClassId(), false);
        holder.studentsRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivExpandArrow.setRotation(isExpanded ? 180 : 0);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(currentClass);
            }
        });

        holder.ivExpandArrow.setOnClickListener(v -> {
            expandedState.put(currentClass.getClassId(), !isExpanded);
            notifyItemChanged(position);
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(currentClass);
            }
        });

        if (isExpanded) {
            List<Student> studentList = getStudentsForClass(currentClass.getClassId());
            StudentAdapter studentAdapter = new StudentAdapter(context, studentList);
            studentAdapter.setOnItemClickListener(student -> {
                if (onStudentClickListener != null) {
                    onStudentClickListener.onStudentClick(student);
                }
            });
            holder.studentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.studentsRecyclerView.setAdapter(studentAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    @SuppressLint("Range")
    private List<Student> getStudentsForClass(int classId) {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("students", null, "class_id = ?", new String[]{String.valueOf(classId)}, null, null, "student_id ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndex("student_id")));
                student.setName(cursor.getString(cursor.getColumnIndex("name")));
                student.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                student.setClassId(cursor.getInt(cursor.getColumnIndex("class_id")));
                student.setAdmissionDate(cursor.getString(cursor.getColumnIndex("admission_date")));
                student.setGraduationDate(cursor.getString(cursor.getColumnIndex("graduation_date")));
                student.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                studentList.add(student);
            }
            cursor.close();
        }
        return studentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName;
        ImageView ivExpandArrow;
        RecyclerView studentsRecyclerView;
        TextView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            ivExpandArrow = itemView.findViewById(R.id.iv_expand_arrow);
            studentsRecyclerView = itemView.findViewById(R.id.students_recycler_view);
            ivDelete = itemView.findViewById(R.id.iv_delete_class);
        }
    }
} 