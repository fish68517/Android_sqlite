package com.example.application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.activity.LoginActivity;
import com.example.application.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvRole;
    private Button btnLogout;
    private DatabaseHelper dbHelper;
    private int loggedInUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        loggedInUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        tvUsername = view.findViewById(R.id.tv_profile_username);
        tvRole = view.findViewById(R.id.tv_profile_role);
        btnLogout = view.findViewById(R.id.btn_logout);

        loadUserProfile();

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserProfile() {
        if (loggedInUserId != -1) {
            User user = dbHelper.getUserById(loggedInUserId);
            if (user != null) {
                tvUsername.setText("用户名: " + user.getUsername());
                tvRole.setText("角色: " + user.getRole());
            }
        }
    }

    private void logout() {
        // 清除 SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // 跳转到登录页面
        // 假设你的登录Activity叫 LoginActivity.class
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}