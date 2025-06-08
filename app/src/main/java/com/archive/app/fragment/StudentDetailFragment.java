package com.archive.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Student;
import com.example.myapplication.R;

import java.io.File;

public class StudentDetailFragment extends Fragment {

    private static final String ARG_STUDENT_ID = "student_id";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameEditText, studentIdEditText, phoneEditText, bioEditText;
    private ImageView avatarImageView;
    private Button saveButton, deleteButton;

    private OpenHelperDataBase dbHelper;
    private Student currentStudent;
    private long studentId = -1;
    private String avatarPath;

    public static StudentDetailFragment newInstance(long studentId) {
        StudentDetailFragment fragment = new StudentDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new OpenHelperDataBase(getContext());
        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        nameEditText = view.findViewById(R.id.edit_text_name);
        studentIdEditText = view.findViewById(R.id.edit_text_student_id);
        phoneEditText = view.findViewById(R.id.edit_text_phone);
        bioEditText = view.findViewById(R.id.edit_text_bio);
        avatarImageView = view.findViewById(R.id.image_view_avatar_detail);
        saveButton = view.findViewById(R.id.button_save);
        deleteButton = view.findViewById(R.id.button_delete);

        if (studentId != -1) {
            loadStudentData();
            deleteButton.setVisibility(View.VISIBLE);
        }

        avatarImageView.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> saveStudent());
        deleteButton.setOnClickListener(v -> deleteStudent());

        return view;
    }

    private void loadStudentData() {
        currentStudent = dbHelper.getStudent(studentId);
        if (currentStudent != null) {
            nameEditText.setText(currentStudent.getName());
            studentIdEditText.setText(currentStudent.getStudentId());
            phoneEditText.setText(currentStudent.getPhone());
            bioEditText.setText(currentStudent.getBio());
            avatarPath = currentStudent.getAvatarPath();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                avatarImageView.setImageURI(Uri.fromFile(new File(avatarPath)));
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            avatarImageView.setImageURI(imageUri);
            avatarPath = getPathFromURI(imageUri);
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return contentUri.getPath();
    }


    private void saveStudent() {
        String name = nameEditText.getText().toString().trim();
        String studentIdStr = studentIdEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(studentIdStr)) {
            Toast.makeText(getContext(), "姓名和学号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = phoneEditText.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();

        if (currentStudent == null) {
            // Add new student
            currentStudent = new Student();
        }

        currentStudent.setName(name);
        currentStudent.setStudentId(studentIdStr);
        currentStudent.setPhone(phone);
        currentStudent.setBio(bio);
        currentStudent.setAvatarPath(avatarPath);

        boolean success;
        if (currentStudent.getId() == 0) {
            success = dbHelper.addStudent(currentStudent) != -1;
        } else {
            success = dbHelper.updateStudent(currentStudent) > 0;
        }

        if (success) {
            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "保存失败，学号可能已存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent() {
        if (currentStudent != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("删除学生")
                    .setMessage("确定要删除该学生的信息吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteStudent(currentStudent.getId());
                        if (deleted) {
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }
} 