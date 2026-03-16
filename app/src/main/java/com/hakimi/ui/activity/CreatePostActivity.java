package com.hakimi.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.Post;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
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

public class CreatePostActivity extends AppCompatActivity {

    private EditText etContent;
    private ImageView ivSelectedImage;
    private ImageView ivAddIcon;
    private Button btnPublish;

    private Uri selectedImageUri;
    private ApiService apiService;
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

        apiService = RetrofitClient.getInstance().getApiService();
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
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u5e16\u5b50\u5185\u5bb9", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublish.setEnabled(false);
        btnPublish.setText("\u53d1\u5e03\u4e2d...");

        if (selectedImageUri == null) {
            createPost(content, null);
            return;
        }

        try {
            File imageFile = copyUriToCacheFile(selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    "file", imageFile.getName(), requestFile);

            apiService.uploadFile(filePart).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        createPost(content, response.body().getData());
                    } else {
                        resetPublishButton();
                        Toast.makeText(CreatePostActivity.this,
                                "\u56fe\u7247\u4e0a\u4f20\u5931\u8d25",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    resetPublishButton();
                    Toast.makeText(CreatePostActivity.this,
                            "\u7f51\u7edc\u9519\u8bef: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            resetPublishButton();
            Toast.makeText(this, "\u56fe\u7247\u5904\u7406\u5931\u8d25", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPost(String content, @Nullable String imagePath) {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            resetPublishButton();
            Toast.makeText(this,
                    "\u672a\u83b7\u53d6\u5230\u5f53\u524d\u767b\u5f55\u7528\u6237",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImagePath(imagePath);
        post.setLikesCount(0);

        apiService.createPost(post).enqueue(new Callback<ApiResponse<Post>>() {
            @Override
            public void onResponse(Call<ApiResponse<Post>> call, Response<ApiResponse<Post>> response) {
                resetPublishButton();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CreatePostActivity.this, "\u53d1\u5e03\u6210\u529f", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreatePostActivity.this, "\u53d1\u5e03\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Post>> call, Throwable t) {
                resetPublishButton();
                Toast.makeText(CreatePostActivity.this,
                        "\u7f51\u7edc\u9519\u8bef: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPublishButton() {
        btnPublish.setEnabled(true);
        btnPublish.setText("\u53d1\u5e03");
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

    private File copyUriToCacheFile(Uri uri) throws IOException {
        String fileName = getFileName(uri);
        File tempFile = new File(getCacheDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
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
        String result = "post_image_" + System.currentTimeMillis() + ".jpg";
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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
}
