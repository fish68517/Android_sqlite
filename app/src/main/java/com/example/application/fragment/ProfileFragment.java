package com.example.application.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

    // [新增代码 1] 创建一个新的 ActivityResultLauncher 用于请求相机权限
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // 如果用户授予了权限，则直接调用拍照方法
                    dispatchTakePictureIntent();
                } else {
                    // 如果用户拒绝了权限，给出一个提示
                    Toast.makeText(getContext(), "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
                }
            }
    );

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
                // 改为调用新的权限检查方法
                checkCameraPermissionAndTakePhoto();
            } else {
                // 任务: 使用相机/画廊 - 级别 3 (画廊)
                dispatchPickImageIntent();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndTakePhoto() {
        // 检查应用是否已经被授予了相机权限
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 如果已经有权限，直接执行拍照操作
            dispatchTakePictureIntent();
        } else {
            // 如果没有权限，则启动权限请求
            // 结果将由上面定义的 requestPermissionLauncher 来处理
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "create file failed", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(requireContext(),
                        "com.example.application.provider",
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
        binding.btnBiometricLogin.setVisibility(isBiometricAvailable() ? View.VISIBLE : View.GONE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        if (isLoggedIn) {
            binding.profileName.setText("Dear user");
            binding.profileEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            binding.btnLogin.setVisibility(View.GONE);
            binding.btnLogout.setVisibility(View.VISIBLE);

            binding.btnBiometricLogin.setVisibility(View.GONE);
            String imageUriString = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null);
            if(imageUriString != null){
                Glide.with(this).load(Uri.parse(imageUriString)).circleCrop().into(binding.profileImage);
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile);
            }

        } else {
            binding.profileName.setText("Guest");
            binding.profileEmail.setText("Please log in first");
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.btnLogout.setVisibility(View.GONE);
            binding.profileImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private void showLoginDialog() {
        // 1. 加载并绑定对话框的视图
        DialogLoginBinding dialogBinding = DialogLoginBinding.inflate(getLayoutInflater());

        // 2. 创建 AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("Login")
                .setView(dialogBinding.getRoot())
                // 3. 将按钮的监听器设置为 null。这是关键一步，它阻止了对话框在按钮被点击后自动关闭。
                // 我们将手动控制对话框的关闭时机。
                .setPositiveButton("Login", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // 4. 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 5. 在对话框显示后，获取其“确定”按钮
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // 6. 为“确定”按钮设置我们自定义的点击监听器
        positiveButton.setOnClickListener(v -> {
            String email = dialogBinding.etEmail.getText().toString().trim();
            String password = dialogBinding.etPassword.getText().toString().trim();

            // 7. 进行表单验证
            boolean isEmailValid = validateEmail(dialogBinding, email);
            boolean isPasswordValid = validatePassword(dialogBinding, password);

            // 8. 只有当邮箱和密码都通过验证时，才执行登录操作并关闭对话框
            if (isEmailValid && isPasswordValid) {
                login(email);
                dialog.dismiss(); // 手动关闭对话框
            }
            // 如果验证失败，我们什么都不做，对话框会保持显示，用户可以修正输入
        });
    }

    private void showLogoutDialog() {
        // 任务: 对话框 (AlertDialog) - 级别 1
        // 描述: 使用AlertDialog来创建一个确认对话框，防止用户误操作退出登录。
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout？")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Cancel", null)
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

    /**
     * 设备必须有生物识别硬件：手机必须物理上配备了指纹传感器或用于面部识别的摄像头等硬件。
     * 用户必须在系统设置中启用了生物识别功能：硬件存在还不够，用户必须在手机的“设置” -> “安全”菜单中开启了指纹/面部解锁功能。
     * 用户必须至少注册了一个生物特征：用户必须已经在系统中录入了至少一个指纹或他/她的面部数据。如果用户只是开启了功能但从未录入过信息，那么认证也是不可用的。
     *
     * BIOMETRIC_ERROR_NO_HARDWARE: 设备上没有任何生物识别硬件。
     * BIOMETRIC_ERROR_HW_UNAVAILABLE: 硬件存在，但当前不可用（可能正在被其他应用占用，或者临时出了问题）。
     * BIOMETRIC_ERROR_NONE_ENROLLED: 设备有硬件，但用户没有录入任何指纹或面部数据。这是最常见的原因之一。
     * @return
     */
    private boolean isBiometricAvailable() {
        // 1. 获取生物识别管理器实例
        BiometricManager biometricManager = BiometricManager.from(requireContext());

        // 2. 检查设备是否可以进行指定类型的生物识别认证
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
                Toast.makeText(getContext(), "Authentication failed: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getContext(), "Authentication successful!", Toast.LENGTH_SHORT).show();
                login(sharedPreferences.getString(KEY_EMAIL, "")); // 模拟登录
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "Authentication failed, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use your fingerprint to login")
                .setNegativeButtonText("Login with account password")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

/*    @Override
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
    }*/

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
            binding.tilEmail.setError("Email cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Please enter a valid email address");
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
            binding.tilPassword.setError("Password cannot be empty");
            return false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Password length cannot be less than 6 characters");
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
