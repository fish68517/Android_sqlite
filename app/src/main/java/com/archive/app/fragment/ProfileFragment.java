package com.archive.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.archive.app.MyApplication;
import com.archive.app.activity.LoginActivity;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.User;
import com.example.myapplication.R; // 确保R文件路径正确
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

/**
 * 用户资料、统计信息显示和操作的 Fragment
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView tvTotalBooksCount, tvCurrentUsername;
    private LinearLayout llCategoryCountsContainer;
    private TextInputEditText etProfileUsername, etProfilePassword, etProfileConfirmPassword;
    private Button btnUpdateProfile, btnLogout;

    private OpenHelperDataBase dbHelper;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing ProfileFragment");
        dbHelper = new OpenHelperDataBase(getContext());
        // 获取当前登录用户。此操作应快速，不涉及IO
        currentUser = MyApplication.curUser;
        if (currentUser == null) {
            Log.e(TAG, "onCreate: Current user is null. Redirecting to LoginActivity.");
            // 如果在这里发现用户未登录，应尽早处理，甚至阻止Fragment加载
            // 但通常Activity或Fragment的容器会处理访问权限
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(TAG, "onCreateView: Inflating layout for ProfileFragment");

        // 初始化视图组件
        tvTotalBooksCount = view.findViewById(R.id.tv_total_books_count);
        llCategoryCountsContainer = view.findViewById(R.id.ll_category_counts_container);
        tvCurrentUsername = view.findViewById(R.id.tv_profile_current_username);
        etProfileUsername = view.findViewById(R.id.et_profile_username);
        etProfilePassword = view.findViewById(R.id.et_profile_password);
        etProfileConfirmPassword = view.findViewById(R.id.et_profile_confirm_password);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        // 设置按钮点击监听
        btnUpdateProfile.setOnClickListener(v -> attemptUpdateProfile());
        btnLogout.setOnClickListener(v -> confirmLogout());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Loading profile data.");
        // Fragment视图创建完毕后加载数据
        // loadProfileDataAsync(); // 移到 onResume 可能更好，以确保数据最新
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Refreshing profile data.");
        // 每次Fragment可见时，重新加载数据以确保最新状态
        currentUser = MyApplication.curUser; // 再次获取，以防万一在后台被修改
        if (currentUser == null) {
            Log.e(TAG, "onResume: Current user is null. Redirecting to LoginActivity.");
            Toast.makeText(getContext(), "登录状态已失效，请重新登录", Toast.LENGTH_LONG).show();
            navigateToLogin();
            return;
        }
        loadProfileDataAsync();
    }

    /**
     * 异步加载用户资料和书籍统计数据
     */
    private void loadProfileDataAsync() {
        if (currentUser == null) {
            Log.w(TAG, "loadProfileDataAsync: CurrentUser is null, cannot load data.");
            return; // Should have been handled by onResume redirect
        }

        // 显示当前用户信息 (非耗时)
        //String userInfo = String.format(getString(R.string.profile_user_info_format), currentUser.getUsername(), currentUser.getRole());
        // 请在 strings.xml 中添加: <string name="profile_user_info_format"> %1$s (角色: %2$s)</string>
        tvCurrentUsername.setText("当前用户: " +currentUser.getUsername());
        etProfileUsername.setText(currentUser.getUsername());
        etProfilePassword.setText(""); // 清空密码字段
        etProfileConfirmPassword.setText("");

        // 异步加载书籍统计
        Log.d(TAG, "Starting thread to load book statistics.");
        new Thread(() -> {
            final int totalBooks = dbHelper.getTotalBookCount();
            final Map<String, Integer> categoryCounts = dbHelper.getBookCountPerCategory();

            if (getActivity() != null && isAdded()) { // 检查Fragment是否附加到Activity
                getActivity().runOnUiThread(() -> {
                    Log.d(TAG, "Updating UI with book statistics. Total: " + totalBooks);
                    tvTotalBooksCount.setText(String.valueOf(totalBooks));
                    displayCategoryCountsUI(categoryCounts);
                });
            }
        }).start();
    }

    /**
     * 动态创建并显示各分类书籍数量的卡片到UI
     */
    private void displayCategoryCountsUI(Map<String, Integer> categoryCounts) {
        if (getContext() == null) return; // 防止Context为null
        llCategoryCountsContainer.removeAllViews(); // 清除旧的视图

        if (categoryCounts == null || categoryCounts.isEmpty()) {
            TextView emptyMsg = new TextView(getContext());
            emptyMsg.setText("暂无分类统计信息");
            emptyMsg.setGravity(Gravity.CENTER);
            emptyMsg.setPadding(0, dpToPx(16), 0, dpToPx(16));
            llCategoryCountsContainer.addView(emptyMsg);
            Log.d(TAG, "No category counts to display.");
            return;
        }

        Log.d(TAG, "Displaying " + categoryCounts.size() + " categories.");
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            CardView cardView = new CardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, dpToPx(8));
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(dpToPx(4));
            cardView.setCardElevation(dpToPx(2));
            cardView.setContentPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

            LinearLayout innerLayout = new LinearLayout(requireContext());
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvCategoryName = new TextView(getContext());
            tvCategoryName.setText(String.format("%s: ", entry.getKey()));
            tvCategoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            innerLayout.addView(tvCategoryName);

            TextView tvCategoryCount = new TextView(getContext());
            tvCategoryCount.setText(String.valueOf(entry.getValue()));
            tvCategoryCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvCategoryCount.setTypeface(null, Typeface.BOLD);
            innerLayout.addView(tvCategoryCount);

            cardView.addView(innerLayout);
            llCategoryCountsContainer.addView(cardView);
        }
    }

    /**
     * 将dp单位转换为px
     */
    private int dpToPx(int dp) {
        if (getContext() == null) return dp; // Fallback if context is somehow null
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 尝试更新用户资料
     */
    private void attemptUpdateProfile() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "用户未登录，无法更新资料", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        final String newUsername = etProfileUsername.getText().toString().trim();
        String newPassword = etProfilePassword.getText().toString().trim();
        String confirmPassword = etProfileConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newUsername)) {
            etProfileUsername.setError("用户名不能为空");
            etProfileUsername.requestFocus();
            return;
        }

        final String passwordToUpdate; // 最终用于更新的密码
        if (!TextUtils.isEmpty(newPassword)) { // 用户打算修改密码
            if (newPassword.length() < 6) { // 假设密码最小长度为6
                etProfilePassword.setError("新密码长度至少为6位");
                etProfilePassword.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                etProfileConfirmPassword.setError("请确认新密码");
                etProfileConfirmPassword.requestFocus();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                etProfileConfirmPassword.setError("两次输入的密码不一致");
                etProfileConfirmPassword.requestFocus();
                return;
            }
            passwordToUpdate = newPassword;
        } else { // 用户不打算修改密码
            if (!TextUtils.isEmpty(confirmPassword)){
                 etProfileConfirmPassword.setError("如不修改密码，请勿填写确认密码");
                 etProfileConfirmPassword.requestFocus();
                 return;
            }
            // 保持原密码不变，从currentUser中获取 (注意：确保currentUser.getPassword()是可用的)
            // OpenHelperDataBase.updateUserProfile 需要密码参数，所以必须传递一个
            passwordToUpdate = currentUser.getPassword(); 
        }

        Log.d(TAG, "Attempting to update profile for user ID: " + currentUser.getId());
        new Thread(() -> {
            // 数据库操作
            final int rowsAffected = dbHelper.updateUserProfile(currentUser.getId(), newUsername, passwordToUpdate);
            if (getActivity() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(getContext(), "用户资料更新成功", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "用户 " + newUsername + " 资料更新成功");
                        // 更新全局User对象
                        currentUser.setUsername(newUsername);
                        currentUser.setPassword(passwordToUpdate); // 更新密码，即使是旧密码，确保对象一致性
                        MyApplication.setUser(currentUser);
                        // 重新加载本页信息以显示更新后的用户名，并清空密码框
                        loadProfileDataAsync();
                    } else {
                        Toast.makeText(getContext(), "用户资料更新失败或未做更改", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "用户资料更新失败，数据库未返回影响行数");
                    }
                });
            }
        }).start();
    }

    /**
     * 弹出确认对话框，然后执行退出登录
     */
    private void confirmLogout() {
        if (getContext() == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("您确定要退出当前账号吗？")
                .setPositiveButton("确定退出", (dialog, which) -> performLogout())
                .setNegativeButton("取消", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * 执行退出登录操作
     */
    private void performLogout() {
        Log.i(TAG, "用户: " + (currentUser != null ? currentUser.getUsername() : "未知用户") + " 执行退出登录");
        MyApplication.setUser(null); // 清除全局用户状态
        Toast.makeText(getContext(), "已成功退出登录", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    /**
     * 导航到登录界面并结束当前Activity栈
     */
    private void navigateToLogin() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finishAffinity(); // 关闭所有相关activities
        }
    }
} 