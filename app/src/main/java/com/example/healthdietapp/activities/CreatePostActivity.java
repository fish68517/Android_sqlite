package com.example.healthdietapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.AnimationUtils;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;
import com.example.healthdietapp.utils.ValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * CreatePostActivity - Allows users to create and publish new posts
 * Supports image upload from gallery or camera, text content, and tags
 */
public class CreatePostActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;

    private FrameLayout imageUploadArea;
    private ImageView selectedImage;
    private LinearLayout uploadPlaceholder;
    private Button selectFromGalleryButton;
    private Button takePhotoButton;
    private EditText postTitleInput;
    private EditText postContentInput;
    private EditText postTagsInput;
    private Button publishButton;
    private Button cancelButton;

    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private SessionManager sessionManager;
    private String userId;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initializeViews();
        initializeDatabase();
        setupImageUploadListeners();
        setupPublishListener();
        setupCancelListener();
    }

    private void initializeViews() {
        imageUploadArea = findViewById(R.id.imageUploadArea);
        selectedImage = findViewById(R.id.selectedImage);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);
        selectFromGalleryButton = findViewById(R.id.selectFromGalleryButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        postTitleInput = findViewById(R.id.postTitleInput);
        postContentInput = findViewById(R.id.postContentInput);
        postTagsInput = findViewById(R.id.postTagsInput);
        publishButton = findViewById(R.id.publishButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void setupImageUploadListeners() {
        imageUploadArea.setOnClickListener(v -> openImageGallery());
        selectFromGalleryButton.setOnClickListener(v -> openImageGallery());
        takePhotoButton.setOnClickListener(v -> openCamera());
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                Uri imageUri = data.getData();
                handleGalleryImage(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAMERA && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                handleCameraImage(bitmap);
            }
        }
    }

    private void handleGalleryImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            selectedImagePath = saveImageToFile(bitmap);
            if (selectedImagePath != null) {
                displaySelectedImage(bitmap);
            } else {
                ErrorHandler.showShortToast(this, "保存图片失败");
            }
        } catch (IOException e) {
            ErrorHandler.logException("CreatePostActivity", e);
            ErrorHandler.handleGenericException(this, e);
        }
    }

    private void handleCameraImage(Bitmap bitmap) {
        selectedImagePath = saveImageToFile(bitmap);
        displaySelectedImage(bitmap);
    }

    private String saveImageToFile(Bitmap bitmap) {
        try {
            File imagesDir = new File(getFilesDir(), "post_images");
            if (!imagesDir.exists()) {
                if (!imagesDir.mkdirs()) {
                    ErrorHandler.showShortToast(this, "无法创建图片目录");
                    return null;
                }
            }

            File imageFile = new File(imagesDir, "post_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            ErrorHandler.logException("CreatePostActivity", e);
            ErrorHandler.handleGenericException(this, e);
            return null;
        }
    }

    private void displaySelectedImage(Bitmap bitmap) {
        selectedImage.setImageBitmap(bitmap);
        uploadPlaceholder.setVisibility(android.view.View.GONE);
    }

    private void setupPublishListener() {
        publishButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(publishButton, this::publishPost);
        });
    }

    private void publishPost() {
        String title = postTitleInput.getText().toString().trim();
        String content = postContentInput.getText().toString().trim();
        String tags = postTagsInput.getText().toString().trim();

        // Validate title
        ValidationUtils.ValidationResult titleValidation = ValidationUtils.validateTextInput(title, "帖子标题", 1, 100);
        if (!titleValidation.isValid()) {
            ErrorHandler.handleValidationException(this, titleValidation.getMessage());
            return;
        }

        // Validate content
        ValidationUtils.ValidationResult contentValidation = ValidationUtils.validateTextInput(content, "帖子内容", 1, 5000);
        if (!contentValidation.isValid()) {
            ErrorHandler.handleValidationException(this, contentValidation.getMessage());
            return;
        }

        publishButton.setEnabled(false);

        new Thread(() -> {
            try {
                Post post = new Post();
                post.setUserId(userId);
                post.setTitle(title);
                post.setContent(content);
                post.setImages(selectedImagePath != null ? selectedImagePath : "");
                post.setTags(tags);
                post.setLikes(0);
                post.setComments(0);
                post.setCreatedAt(System.currentTimeMillis());
                post.setUpdatedAt(System.currentTimeMillis());

                boolean success = postDAO.createPost(post);

                runOnUiThread(() -> {
                    publishButton.setEnabled(true);
                    if (success) {
                        ErrorHandler.showShortToast(this, "帖子已发布");
                        finish();
                    } else {
                        ErrorHandler.showShortToast(this, "发布帖子失败");
                    }
                });
            } catch (Exception e) {
                ErrorHandler.logException("CreatePostActivity", e);
                runOnUiThread(() -> {
                    publishButton.setEnabled(true);
                    ErrorHandler.handleDatabaseException(this, e);
                });
            }
        }).start();
    }

    private void setupCancelListener() {
        cancelButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(cancelButton);
            finish();
        });
    }
}
