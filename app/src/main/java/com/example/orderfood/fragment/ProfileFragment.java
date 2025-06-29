package com.example.orderfood.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.activity.LoginActivity;
import com.example.orderfood.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvNickname, tvUsername;
    private Button btnEditNickname, btnLogout;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = -1;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DataBaseOpenHelper(getContext());
        // 获取当前用户ID
        SharedPreferences sessionPrefs = getActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
            // 如果没有获取到用户ID，直接登出
            logout();
            return;
        }

        btnEditNickname.setOnClickListener(v -> showEditNicknameDialog());
        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到这个Fragment时都加载/刷新用户信息
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (currentUserId != -1) {
            currentUser = dbHelper.getUser(currentUserId);
            if (currentUser != null) {
                tvNickname.setText(currentUser.getNickname());
                tvUsername.setText("用户名: " + currentUser.getUsername());
            } else {
                // 如果数据库中找不到该用户，也执行登出
                Toast.makeText(getContext(), "用户数据异常，请重新登录", Toast.LENGTH_SHORT).show();
                logout();
            }
        }
    }

    private void showEditNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改昵称");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentUser.getNickname()); // 显示当前昵称
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String newNickname = input.getText().toString().trim();
            if (!newNickname.isEmpty()) {
                int rowsAffected = dbHelper.updateUserNickname(currentUserId, newNickname);
                if (rowsAffected > 0) {
                    Toast.makeText(getContext(), "昵称修改成功", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // 重新加载以更新UI
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
        // 清除Session
        SharedPreferences sessionPrefs = getActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionPrefs.edit();
        editor.clear();
        editor.apply();

        // 可选：如果希望退出登录也清除"记住我"
        // SharedPreferences loginPrefs = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        // loginPrefs.edit().clear().apply();

        // 跳转到登录页并清空任务栈
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
