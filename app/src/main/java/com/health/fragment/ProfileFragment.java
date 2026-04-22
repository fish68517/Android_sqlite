package com.Health.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.Health.HealthApplication;
import com.Health.R;
import com.Health.activity.LoginActivity;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.model.User;
import com.Health.utils.SharedPrefManager;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvEmail;

    private LocalHealthRepository localRepository;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveAvatar(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        localRepository = LocalHealthRepository.getInstance(requireContext());
        sharedPrefManager = SharedPrefManager.getInstance();

        initViews(view);
        setupListeners(view);
        loadUserInfo();
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvHeight = view.findViewById(R.id.tv_height);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvEmail = view.findViewById(R.id.tv_email);
    }

    private void setupListeners(View view) {
        ivAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        view.findViewById(R.id.btn_edit_body_data).setOnClickListener(v -> showEditBodyDataDialog());
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
    }

    private void loadUserInfo() {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            renderGuestState();
            return;
        }

        User user = localRepository.getUserById(userId);
        if (user == null) {
            renderGuestState();
            return;
        }

        currentUser = user;
        HealthApplication.setUser(user);
        sharedPrefManager.saveUsername(user.getUsername());
        bindUserInfo(user);
    }

    private void bindUserInfo(User user) {
        tvUsername.setText(TextUtils.isEmpty(user.getUsername()) ? "未登录用户" : user.getUsername());
        tvEmail.setText(TextUtils.isEmpty(user.getPhone()) ? "--" : user.getPhone());
        tvHeight.setText(user.getHeight() == null ? "--" : String.valueOf(user.getHeight()));
        tvWeight.setText(user.getWeight() == null ? "--" : String.valueOf(user.getWeight()));

        if (!TextUtils.isEmpty(user.getAvatar())) {
            ivAvatar.setPadding(0, 0, 0, 0);
            ivAvatar.setImageTintList(null);
            Glide.with(this)
                    .load(Uri.parse(user.getAvatar()))
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            ivAvatar.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            ivAvatar.setImageTintList(ColorStateList.valueOf(0xFF4A90E2));
            ivAvatar.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }

    private void renderGuestState() {
        tvUsername.setText("未登录用户");
        tvEmail.setText("--");
        tvHeight.setText("--");
        tvWeight.setText("--");
        ivAvatar.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        ivAvatar.setImageTintList(ColorStateList.valueOf(0xFF4A90E2));
        ivAvatar.setImageResource(android.R.drawable.ic_menu_camera);
    }

    private void showEditBodyDataDialog() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(20);
        container.setPadding(padding, padding, padding, 0);

        EditText etHeight = new EditText(requireContext());
        etHeight.setHint("身高(cm)");
        etHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentUser.getHeight() != null) {
            etHeight.setText(String.valueOf(currentUser.getHeight()));
        }

        EditText etWeight = new EditText(requireContext());
        etWeight.setHint("体重(kg)");
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentUser.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUser.getWeight()));
        }

        container.addView(etHeight);
        container.addView(etWeight);

        new AlertDialog.Builder(requireContext())
                .setTitle("编辑身体数据")
                .setView(container)
                .setNegativeButton("取消", null)
                .setPositiveButton("保存", (dialog, which) ->
                        updateBodyData(etHeight.getText().toString().trim(),
                                etWeight.getText().toString().trim()))
                .show();
    }

    private void updateBodyData(String height, String weight) {
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        LocalResult<User> result = localRepository.updateBodyData(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                parseDouble(height),
                parseDouble(weight),
                currentUser.getAvatar());

        if (!result.isSuccess() || result.getData() == null) {
            Toast.makeText(getContext(), "身体数据保存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser = result.getData();
        HealthApplication.setUser(currentUser);
        bindUserInfo(currentUser);
        Toast.makeText(getContext(), "身体数据保存成功", Toast.LENGTH_SHORT).show();
    }

    private void saveAvatar(Uri uri) {
        if (currentUser == null || currentUser.getId() == null) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<User> result = localRepository.updateBodyData(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getHeight(),
                currentUser.getWeight(),
                uri.toString());

        if (!result.isSuccess() || result.getData() == null) {
            Toast.makeText(getContext(), "头像保存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser = result.getData();
        HealthApplication.setUser(currentUser);
        bindUserInfo(currentUser);
        Toast.makeText(getContext(), "头像更换成功", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        sharedPrefManager.clear();
        HealthApplication.setUser(null);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HealthApplication.curUser != null && HealthApplication.curUser.getId() != null) {
            return HealthApplication.curUser.getId();
        }
        return null;
    }

    private Double parseDouble(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
