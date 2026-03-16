package com.hakimi.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.LocalResult;
import com.hakimi.model.Post;
import com.hakimi.utils.SharedPrefManager;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etContent;
    private ImageView ivSelectedImage;
    private ImageView ivAddIcon;
    private Button btnPublish;

    private Uri selectedImageUri;
    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivAddIcon.setVisibility(ImageView.GONE);
                    ivSelectedImage.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();

        etContent = findViewById(R.id.et_post_content);
        ivSelectedImage = findViewById(R.id.iv_selected_image);
        ivAddIcon = findViewById(R.id.iv_add_icon);
        btnPublish = findViewById(R.id.btn_publish);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.cv_image_container).setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnPublish.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入帖子内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "未获取到当前登录用户", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublish.setEnabled(false);
        btnPublish.setText("发布中...");

        String imagePath = selectedImageUri == null ? null : selectedImageUri.toString();
        LocalResult<Post> result = repository.createPost(userId, content, imagePath);

        btnPublish.setEnabled(true);
        btnPublish.setText("发布");

        if (result.isSuccess()) {
            Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this,
                    TextUtils.isEmpty(result.getMessage()) ? "发布失败" : result.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
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
}
