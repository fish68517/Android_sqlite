package com.example.orderfood.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.activity.LoginActivity;
import com.example.orderfood.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvNickname;
    private TextView tvUsername;
    private Button btnEditNickname;
    private Button btnLogout;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = -1;
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DataBaseOpenHelper(requireContext());
        SharedPreferences sessionPrefs = requireActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvUsername = view.findViewById(R.id.tv_username);
        btnEditNickname = view.findViewById(R.id.btn_edit_nickname);
        btnLogout = view.findViewById(R.id.btn_logout);

        if (currentUserId == -1) {
            logout();
            return;
        }

        btnEditNickname.setOnClickListener(v -> showEditNicknameDialog());
        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (currentUserId != -1) {
            currentUser = dbHelper.getUser(currentUserId);
            if (currentUser != null) {
                tvNickname.setText(currentUser.getNickname());
                tvUsername.setText("用户名: " + currentUser.getUsername());
            } else {
                Toast.makeText(getContext(), "用户数据异常，请重新登录", Toast.LENGTH_SHORT).show();
                logout();
            }
        }
    }

    private void showEditNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("修改昵称");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (currentUser != null) {
            input.setText(currentUser.getNickname());
        }
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String newNickname = input.getText().toString().trim();
            if (!newNickname.isEmpty()) {
                long rowsAffected = dbHelper.updateUserNickname(currentUserId, newNickname);
                if (rowsAffected > 0) {
                    Toast.makeText(getContext(), "昵称修改成功", Toast.LENGTH_SHORT).show();
                    loadUserProfile();
                } else {
                    Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void logout() {
        SharedPreferences sessionPrefs = requireActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        sessionPrefs.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
} 