package com.hakimi.fragment;

import android.content.res.ColorStateList;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.activity.LoginActivity;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.User;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
import com.hakimi.utils.ImageLoader;
import com.hakimi.utils.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvEmail;

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadAvatar(uri);
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

        apiService = RetrofitClient.getInstance().getApiService();
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

        apiService.getUserProfile(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    currentUser = response.body().getData();
                    HakimiApplication.setUser(currentUser);
                    sharedPrefManager.saveUsername(currentUser.getUsername());
                    bindUserInfo(currentUser);
                } else {
                    Toast.makeText(getContext(), "\u52a0\u8f7d\u7528\u6237\u4fe1\u606f\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(getContext(), "\u52a0\u8f7d\u7528\u6237\u4fe1\u606f\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindUserInfo(User user) {
        tvUsername.setText(TextUtils.isEmpty(user.getUsername()) ? "\u672a\u767b\u5f55\u7528\u6237" : user.getUsername());
        tvEmail.setText(TextUtils.isEmpty(user.getEmail()) ? "--" : user.getEmail());
        tvHeight.setText(user.getHeight() == null ? "--" : String.valueOf(user.getHeight()));
        tvWeight.setText(user.getWeight() == null ? "--" : String.valueOf(user.getWeight()));

        if (!TextUtils.isEmpty(user.getAvatar())) {
            String imageUrl = ApiService.BASE_URL.replace("/api/", "") + trimLeadingSlash(user.getAvatar());
            ivAvatar.setPadding(0, 0, 0, 0);
            ivAvatar.setImageTintList(null);
            ImageLoader.loadCircleImage(imageUrl, ivAvatar);
        } else {
            ivAvatar.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            ivAvatar.setImageTintList(ColorStateList.valueOf(0xFF4A90E2));
            ivAvatar.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }

    private void renderGuestState() {
        tvUsername.setText("\u672a\u767b\u5f55\u7528\u6237");
        tvEmail.setText("--");
        tvHeight.setText("--");
        tvWeight.setText("--");
        ivAvatar.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        ivAvatar.setImageTintList(ColorStateList.valueOf(0xFF4A90E2));
        ivAvatar.setImageResource(android.R.drawable.ic_menu_camera);
    }

    private void showEditBodyDataDialog() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "\u8bf7\u5148\u767b\u5f55", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(20);
        container.setPadding(padding, padding, padding, 0);

        EditText etHeight = new EditText(requireContext());
        etHeight.setHint("\u8eab\u9ad8(cm)");
        etHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentUser.getHeight() != null) {
            etHeight.setText(String.valueOf(currentUser.getHeight()));
        }

        EditText etWeight = new EditText(requireContext());
        etWeight.setHint("\u4f53\u91cd(kg)");
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentUser.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUser.getWeight()));
        }

        container.addView(etHeight);
        container.addView(etWeight);

        new AlertDialog.Builder(requireContext())
                .setTitle("\u7f16\u8f91\u8eab\u4f53\u6570\u636e")
                .setView(container)
                .setNegativeButton("\u53d6\u6d88", null)
                .setPositiveButton("\u4fdd\u5b58", (dialog, which) ->
                        updateBodyData(etHeight.getText().toString().trim(),
                                etWeight.getText().toString().trim()))
                .show();
    }

    private void updateBodyData(String height, String weight) {
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        User updateUser = new User();
        updateUser.setUsername(currentUser.getUsername());
        updateUser.setEmail(currentUser.getEmail());
        updateUser.setAvatar(currentUser.getAvatar());
        updateUser.setHeight(parseDouble(height));
        updateUser.setWeight(parseDouble(weight));

        apiService.updateUser(currentUser.getId(), updateUser).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentUser.setHeight(updateUser.getHeight());
                    currentUser.setWeight(updateUser.getWeight());
                    bindUserInfo(currentUser);
                    Toast.makeText(getContext(), "\u8eab\u4f53\u6570\u636e\u4fdd\u5b58\u6210\u529f", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "\u8eab\u4f53\u6570\u636e\u4fdd\u5b58\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(getContext(), "\u4fdd\u5b58\u5931\u8d25: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatar(Uri uri) {
        if (currentUser == null || currentUser.getId() == null) {
            Toast.makeText(getContext(), "\u8bf7\u5148\u767b\u5f55", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File imageFile = copyUriToCacheFile(uri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

            apiService.uploadFile(filePart).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        updateAvatarPath(response.body().getData());
                    } else {
                        Toast.makeText(getContext(), "\u5934\u50cf\u4e0a\u4f20\u5931\u8d25", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(getContext(), "\u5934\u50cf\u4e0a\u4f20\u5931\u8d25: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getContext(), "\u56fe\u7247\u5904\u7406\u5931\u8d25", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAvatarPath(String avatarPath) {
        User updateUser = new User();
        updateUser.setUsername(currentUser.getUsername());
        updateUser.setEmail(currentUser.getEmail());
        updateUser.setHeight(currentUser.getHeight());
        updateUser.setWeight(currentUser.getWeight());
        updateUser.setAvatar(avatarPath);

        apiService.updateUser(currentUser.getId(), updateUser).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentUser.setAvatar(avatarPath);
                    bindUserInfo(currentUser);
                    Toast.makeText(getContext(), "\u5934\u50cf\u66f4\u6362\u6210\u529f", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "\u5934\u50cf\u4fdd\u5b58\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(getContext(), "\u5934\u50cf\u4fdd\u5b58\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        sharedPrefManager.clear();
        HakimiApplication.setUser(null);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
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

    private String trimLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private File copyUriToCacheFile(Uri uri) throws IOException {
        String fileName = getFileName(uri);
        File tempFile = new File(requireContext().getCacheDir(), fileName);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String result = "avatar_" + System.currentTimeMillis() + ".jpg";
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        return result;
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
