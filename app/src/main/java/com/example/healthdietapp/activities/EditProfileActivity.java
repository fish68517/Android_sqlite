package com.example.healthdietapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;
import com.example.healthdietapp.utils.ValidationUtils;

/**
 * EditProfileActivity - Allows users to edit their profile information
 * Supports editing nickname and uploading avatar
 */
public class EditProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private UserDAO userDAO;
    
    private ImageView avatarImageView;
    private Button uploadAvatarButton;
    private EditText nicknameEditText;
    private TextView usernameTextView;
    private Button saveButton;
    private Button backButton;
    
    private String userId;
    private User currentUser;
    private String selectedAvatarPath;
    
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        initializeManagers();
        initializeViews();
        setupActivityResultLaunchers();
        loadUserInfo();
        setupButtonListeners();
    }

    private void initializeManagers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper);
        userId = sessionManager.getUserId();
    }

    private void initializeViews() {
        avatarImageView = findViewById(R.id.avatarImageView);
        uploadAvatarButton = findViewById(R.id.uploadAvatarButton);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        usernameTextView = findViewById(R.id.usernameTextView);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupActivityResultLaunchers() {
        // Image picker launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedAvatarPath = imageUri.toString();
                            avatarImageView.setImageURI(imageUri);
                        }
                    }
                });

        // Permission request launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserInfo() {
        new Thread(() -> {
            try {
                currentUser = userDAO.getUserById(userId);
                if (currentUser != null) {
                    runOnUiThread(() -> {
                        displayUserInfo(currentUser);
                    });
                } else {
                    runOnUiThread(() -> {
                        ErrorHandler.showShortToast(this, "用户信息加载失败");
                    });
                }
            } catch (Exception e) {
                ErrorHandler.logException("EditProfileActivity", e);
                runOnUiThread(() -> {
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }

    private void displayUserInfo(User user) {
        if (user.getNickname() != null && !user.getNickname().isEmpty()) {
            nicknameEditText.setText(user.getNickname());
        }
        
        if (user.getUsername() != null) {
            usernameTextView.setText(user.getUsername());
        }
        
        // TODO: Load avatar image from URL if available
        // For now, use default avatar
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> finish());
        
        uploadAvatarButton.setOnClickListener(v -> requestImagePermissionAndPick());
        
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void requestImagePermissionAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, 
                    android.Manifest.permission.READ_MEDIA_IMAGES) 
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, 
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) 
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void saveProfileChanges() {
        String newNickname = nicknameEditText.getText().toString().trim();
        
        // Validate nickname
        ValidationUtils.ValidationResult nicknameValidation = ValidationUtils.validateNickname(newNickname);
        if (!nicknameValidation.isValid()) {
            ErrorHandler.handleValidationException(this, nicknameValidation.getMessage());
            return;
        }
        
        saveButton.setEnabled(false);
        
        // Update user object
        currentUser.setNickname(newNickname);
        if (selectedAvatarPath != null) {
            currentUser.setAvatar(selectedAvatarPath);
        }
        
        // Save to database in background thread
        new Thread(() -> {
            try {
                boolean success = userDAO.updateUser(currentUser);
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    if (success) {
                        ErrorHandler.showShortToast(this, "个人资料已更新");
                        finish();
                    } else {
                        ErrorHandler.showShortToast(this, "更新个人资料失败");
                    }
                });
            } catch (Exception e) {
                ErrorHandler.logException("EditProfileActivity", e);
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }
}
