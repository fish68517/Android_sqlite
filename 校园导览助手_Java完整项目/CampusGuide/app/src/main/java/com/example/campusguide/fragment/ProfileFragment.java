package com.example.campusguide.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.campusguide.AboutActivity;
import com.example.campusguide.LoginActivity;
import com.example.campusguide.R;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        String username = requireContext().getSharedPreferences(LoginActivity.PREFS, 0)
                .getString(LoginActivity.KEY_USERNAME, "校园访客");
        ((TextView) view.findViewById(R.id.text_profile_name)).setText(username);

        view.findViewById(R.id.card_about).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AboutActivity.class)));
        view.findViewById(R.id.card_logout).setOnClickListener(v -> confirmLogout());
        return view;
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？收藏记录仍会保留。")
                .setNegativeButton("取消", null)
                .setPositiveButton("退出", (dialog, which) -> {
                    requireContext().getSharedPreferences(LoginActivity.PREFS, 0).edit()
                            .putBoolean(LoginActivity.KEY_LOGGED_IN, false)
                            .remove(LoginActivity.KEY_USERNAME)
                            .apply();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }
}
