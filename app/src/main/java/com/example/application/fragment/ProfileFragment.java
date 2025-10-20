package com.example.application.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import com.example.application.R;
import com.example.application.databinding.DialogLoginBinding;
import com.example.application.databinding.FragmentProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "StyleHubPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_PROFILE_IMAGE_URI = "profileImageUri";

    private Uri cameraImageUri;

    // 任务: 使用相机/画廊 - 级别 3
    // 描述: 使用新的Activity Result API来处理从相机和图库返回的结果。
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setProfileImage(cameraImageUri);
                }
            });

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    setProfileImage(imageUri);
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();

        binding.btnLogin.setOnClickListener(v -> showLoginDialog());
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
        binding.btnBiometricLogin.setOnClickListener(v -> showBiometricPrompt());
        binding.profileImage.setOnClickListener(v -> showImagePickerDialog());
    }

    private void showImagePickerDialog() {
        if (!sharedPreferences.getBoolean(KEY_LOGGED_IN, false)){
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("更换头像");
        builder.setItems(new CharSequence[]{"拍照", "从相册选择"}, (dialog, which) -> {
            if (which == 0) {
                // 任务: 使用相机/画廊 - 级别 3 (相机)
                dispatchTakePictureIntent();
            } else {
                // 任务: 使用相机/画廊 - 级别 3 (画廊)
                dispatchPickImageIntent();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "创建文件失败", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(requireContext(),
                        "com.example.stylehub.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void dispatchPickImageIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickIntent);
    }

    private void setProfileImage(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this).load(imageUri).circleCrop().into(binding.profileImage);
            sharedPreferences.edit().putString(KEY_PROFILE_IMAGE_URI, imageUri.toString()).apply();

            // 任务: 小吃店 (Snackbar) - 级别 1
            // 描述: 在成功更新头像后，使用Snackbar给用户一个简短的提示。
            Snackbar.make(binding.getRoot(), "头像更新成功!", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void updateUI() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        if (isLoggedIn) {
            binding.profileName.setText("尊贵的用户");
            binding.profileEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            binding.btnLogin.setVisibility(View.GONE);
            binding.btnLogout.setVisibility(View.VISIBLE);
            binding.btnBiometricLogin.setVisibility(isBiometricAvailable() ? View.VISIBLE : View.GONE);
            String imageUriString = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null);
            if(imageUriString != null){
                Glide.with(this).load(Uri.parse(imageUriString)).circleCrop().into(binding.profileImage);
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile);
            }

        } else {
            binding.profileName.setText("访客");
            binding.profileEmail.setText("请先登录");
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.btnLogout.setVisibility(View.GONE);
            binding.btnBiometricLogin.setVisibility(View.GONE);
            binding.profileImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private void showLoginDialog() {
        DialogLoginBinding dialogBinding = DialogLoginBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(requireContext())
                .setTitle("登录/注册")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("登录", null) // Set to null to override
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showLogoutDialog() {
        // 任务: 对话框 (AlertDialog) - 级别 1
        // 描述: 使用AlertDialog来创建一个确认对话框，防止用户误操作退出登录。
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("你确定要退出当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void login(String email) {
        sharedPreferences.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_EMAIL, email)
                .apply();
        updateUI();
    }

    private void logout() {
        sharedPreferences.edit().clear().apply();
        updateUI();
    }

    private boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    private void showBiometricPrompt() {
        // 任务: 生物识别 (Biometrics) - 级别 3
        // 描述: 这里是生物识别功能的完整实现流程。
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getContext(), "认证失败: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getContext(), "认证成功!", Toast.LENGTH_SHORT).show();
                login(sharedPreferences.getString(KEY_EMAIL, "")); // 模拟登录
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "认证失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("生物识别登录")
                .setSubtitle("使用你的指纹或面部来登录")
                .setNegativeButtonText("使用账号密码登录")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Override the positive button click listener after the dialog is shown.
        // This is a common pattern to prevent the dialog from closing on invalid input.
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                DialogLoginBinding dialogBinding = DialogLoginBinding.bind(dialog.findViewById(R.id.til_email).getRootView());
                String email = dialogBinding.etEmail.getText().toString().trim();
                String password = dialogBinding.etPassword.getText().toString().trim();

                boolean isEmailValid = validateEmail(dialogBinding, email);
                boolean isPasswordValid = validatePassword(dialogBinding, password);

                if (isEmailValid && isPasswordValid) {
                    login(email);
                    dialog.dismiss();
                }
            });
        }
    }

    private AlertDialog getDialog() {
        if (getActivity() != null) {
            final ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
            if (root != null && root.getChildCount() > 0) {
                View topView = root.getChildAt(root.getChildCount() - 1);
                if (topView.getParent().getParent() instanceof AlertDialog) {
                    return (AlertDialog) topView.getParent().getParent();
                }
            }
        }
        return null;
    }

    private boolean validateEmail(DialogLoginBinding binding, String email) {
        // 任务: 表单验证 - 级别 2
        // 描述: 验证邮箱格式是否正确。
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("邮箱不能为空");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("请输入有效的邮箱地址");
            return false;
        } else {
            binding.tilEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(DialogLoginBinding binding, String password) {
        // 任务: 表单验证 - 级别 2
        // 描述: 验证密码长度是否符合要求。
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("密码不能为空");
            return false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("密码长度不能少于6位");
            return false;
        } else {
            binding.tilPassword.setError(null);
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
