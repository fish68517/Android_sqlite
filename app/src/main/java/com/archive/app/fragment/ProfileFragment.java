package com.archive.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.activity.MyBookingsActivity;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.User;
import com.example.myapplication.R;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private Button logoutButton, myBookingsButton;
    private OpenHelperDataBase dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new OpenHelperDataBase(getContext());
        usernameTextView = view.findViewById(R.id.profile_username);
        logoutButton = view.findViewById(R.id.logout_button);
        myBookingsButton = view.findViewById(R.id.my_bookings_button);

        loadUserProfile();

        logoutButton.setOnClickListener(v -> {
            // 在实际应用中，您可能需要清除用户会话/首选项
            Toast.makeText(getContext(), "您已退出登录", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        myBookingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyBookingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        // FIXME: 暂时硬编码加载ID为1的用户。在实际应用中，应从登录会话中获取用户ID。
        User user = dbHelper.loginUser("1", "1"); // 使用loginUser模拟获取用户
        if (user != null) {
            usernameTextView.setText("用户：" + user.getUsername());
        } else {
            usernameTextView.setText("未找到用户");
        }
    }
} 