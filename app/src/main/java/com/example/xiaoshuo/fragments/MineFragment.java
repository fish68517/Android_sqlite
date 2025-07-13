package com.example.xiaoshuo.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.xiaoshuo.R;
import com.example.xiaoshuo.activities.LoginActivity;
import com.example.xiaoshuo.activities.ReadHistoryActivity;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.AppUsageTracker;
import com.example.xiaoshuo.utils.ReadHistoryManager;
import com.example.xiaoshuo.utils.UserManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvReadHistory;
    private TextView tvMyDownload;
    private TextView tvMyCollection;
    private TextView tvSettings;
    private Button btnLogin;
    private Button btnLogout;
    
    // 使用时长相关控件
    private TextView tvTodayUsage;
    private TextView tvTotalUsage;
    private Button btnUsageDetail;
    
    private UserManager userManager;
    private AppUsageTracker usageTracker;
    private ReadHistoryManager readHistoryManager;
    private static final int REQUEST_IMAGE_PICK = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        
        userManager = UserManager.getInstance(requireContext());
        usageTracker = AppUsageTracker.getInstance(requireContext());
        readHistoryManager = ReadHistoryManager.getInstance(requireContext());
        
        initViews(view);
        setupListeners();
        updateUI();
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次页面恢复时更新UI，以反映可能的登录状态变化
        updateUI();
        // 更新使用时长显示
        updateUsageDisplay();
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvReadHistory = view.findViewById(R.id.tv_read_history);
        tvMyDownload = view.findViewById(R.id.tv_my_download);
        tvMyCollection = view.findViewById(R.id.tv_my_collection);
        tvSettings = view.findViewById(R.id.tv_settings);
        btnLogin = view.findViewById(R.id.btn_login);
        btnLogout = view.findViewById(R.id.btn_logout);
        
        // 初始化使用时长控件
        tvTodayUsage = view.findViewById(R.id.tv_today_usage);
        tvTotalUsage = view.findViewById(R.id.tv_total_usage);
        btnUsageDetail = view.findViewById(R.id.btn_usage_detail);
    }
    
    /**
     * 更新使用时长显示
     */
    private void updateUsageDisplay() {
        if (tvTodayUsage != null && tvTotalUsage != null) {
            tvTodayUsage.setText(usageTracker.getTodayUsageTime());
            tvTotalUsage.setText(usageTracker.getTotalUsageTime());
        }
    }
    
    private void updateUI() {
        if (userManager.isLoggedIn()) {
            User user = userManager.getCurrentUser();
            tvUsername.setText(user.getUsername());
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            
            // 加载用户头像
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivAvatar);
            }
        } else {
            tvUsername.setText("未登录");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    private void setupListeners() {
        // 登录按钮
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        
        // 退出登录按钮
        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
        
        // 头像点击事件
        ivAvatar.setOnClickListener(v -> {
            if (userManager.isLoggedIn()) {
                // 已登录状态下，点击头像可以选择更换头像
                showAvatarOptions();
            } else {
                // 未登录状态下，点击头像跳转到登录页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        
        // 阅读历史
        tvReadHistory.setOnClickListener(v -> {
            // 直接跳转到阅读历史页面，不需要登录验证
            Intent intent = new Intent(getActivity(), ReadHistoryActivity.class);
            startActivity(intent);
        });
        
        // 我的下载
        tvMyDownload.setOnClickListener(v -> {
            if (checkLoginStatus()) {
                Toast.makeText(getContext(), "查看我的下载", Toast.LENGTH_SHORT).show();
                // TODO: 跳转到我的下载页面
            }
        });
        
        // 我的收藏
        tvMyCollection.setOnClickListener(v -> {
            if (checkLoginStatus()) {
                Toast.makeText(getContext(), "查看我的收藏", Toast.LENGTH_SHORT).show();
                // TODO: 跳转到我的收藏页面
            }
        });
        
        // 设置
        tvSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "进入设置页面", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到设置页面
        });
        
        // 使用时长详情
        btnUsageDetail.setOnClickListener(v -> {
            showUsageDetailDialog();
        });
    }
    
    /**
     * 显示退出登录确认对话框
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("退出登录")
            .setMessage(R.string.logout_confirm)
            .setPositiveButton("确定", (dialog, which) -> {
                performLogout();
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    /**
     * 执行退出登录操作
     */
    private void performLogout() {
        // 退出登录
        userManager.logout();
        
        // 通知ReadHistoryManager用户已更改
        readHistoryManager.onUserChanged();
        
        // 更新UI
        updateUI();
        
        // 提示用户已退出登录
        Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        
        // 跳转到登录页面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
    
    /**
     * 显示使用时长详细信息对话框
     */
    private void showUsageDetailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_usage_detail, null);
        
        // 如果没有布局，只用简单的方式显示
        if (dialogView == null) {
            StringBuilder message = new StringBuilder();
            message.append("今日使用时长: ").append(usageTracker.getTodayUsageTime()).append("\n");
            message.append("累计使用时长: ").append(usageTracker.getTotalUsageTime()).append("\n");
            message.append("当前会话时长: ").append(usageTracker.getCurrentSessionTime()).append("\n");
            
            builder.setTitle("使用时长统计")
                   .setMessage(message.toString())
                   .setPositiveButton("确定", null);
        } else {
            // 使用自定义布局
            TextView tvDialogTodayUsage = dialogView.findViewById(R.id.tv_dialog_today_usage);
            TextView tvDialogTotalUsage = dialogView.findViewById(R.id.tv_dialog_total_usage);
            TextView tvDialogCurrentSession = dialogView.findViewById(R.id.tv_dialog_current_session);
            
            tvDialogTodayUsage.setText(usageTracker.getTodayUsageTime());
            tvDialogTotalUsage.setText(usageTracker.getTotalUsageTime());
            tvDialogCurrentSession.setText(usageTracker.getCurrentSessionTime());
            
            builder.setView(dialogView)
                   .setTitle("使用时长统计")
                   .setPositiveButton("确定", null);
        }
        
        builder.create().show();
    }
    
    /**
     * 检查登录状态，如果未登录则提示用户登录
     * @return 是否已登录
     */
    private boolean checkLoginStatus() {
        if (!userManager.isLoggedIn()) {
            new AlertDialog.Builder(requireContext())
                .setTitle("提示")
                .setMessage("您尚未登录，是否前往登录页面？")
                .setPositiveButton("去登录", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
            return false;
        }
        return true;
    }
    
    /**
     * 显示头像选择对话框
     */
    private void showAvatarOptions() {
        String[] options = {"从相册选择", "拍照", "取消"};
        
        new AlertDialog.Builder(requireContext())
            .setTitle("更换头像")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // 从相册选择
                        openGallery();
                        break;
                    case 1: // 拍照
                        Toast.makeText(getContext(), "拍照功能暂未实现", Toast.LENGTH_SHORT).show();
                        break;
                }
            })
            .show();
    }
    
    /**
     * 打开相册选择图片
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            
            // 更新用户头像
            if (selectedImage != null && userManager.isLoggedIn()) {
                User user = userManager.getCurrentUser();
                user.setAvatarUrl(selectedImage.toString());
                userManager.updateUser(user);
                
                // 更新UI显示新头像
                Glide.with(this)
                    .load(selectedImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivAvatar);
            }
        }
    }
} 