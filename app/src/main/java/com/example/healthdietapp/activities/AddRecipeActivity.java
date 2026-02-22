package com.example.healthdietapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.utils.AnimationUtils;
import com.example.healthdietapp.utils.SessionManager;

import java.util.UUID;

/**
 * AddRecipeActivity - 允许用户创建并保存一个新的食谱
 */
public class AddRecipeActivity extends AppCompatActivity {

    private static final String TAG = "AddRecipeActivity";

    private EditText recipeName;
    private EditText recipeCategory;
    private EditText recipeDescription;
    private EditText recipeIngredients;
    private EditText recipeInstructions;
    private EditText recipeNutrition;

    private Button saveRecipeButton;
    private Button backButton;
    private TextView toolbarTitle;
    private ImageView recipeImage;

    private DatabaseHelper dbHelper;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;
    private String userId;

    private String currentImageUri = ""; // 用于保存选中的图片本地绝对路径
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initializeViews();
        setupImagePicker();
        initializeDatabase();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText("添加新食谱");
        }
        backButton = findViewById(R.id.backButton);

        recipeImage = findViewById(R.id.recipeImage);
        recipeName = findViewById(R.id.recipeName);
        recipeCategory = findViewById(R.id.recipeCategory);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeNutrition = findViewById(R.id.recipeNutrition);

        saveRecipeButton = findViewById(R.id.saveRecipeButton);
    }

    private void setupImagePicker() {
        // 注册相册返回结果
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // 将外部相册图片拷贝到 APP 私有目录，获得绝对路径
                        String localPath = copyImageToInternalStorage(uri);
                        currentImageUri = localPath;

                        // 显示选中的图片
                        recipeImage.setImageURI(Uri.fromFile(new java.io.File(localPath)));
                        Log.d(TAG, "图片已保存至私有目录: " + currentImageUri);
                    }
                }
        );
    }

    /**
     * 将相册的临时 URI 拷贝到应用私有目录，防止权限丢失
     */
    private String copyImageToInternalStorage(Uri uri) {
        try {
            java.io.InputStream is = getContentResolver().openInputStream(uri);
            java.io.File dir = new java.io.File(getFilesDir(), "recipe_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            java.io.File file = new java.io.File(dir, "img_" + System.currentTimeMillis() + ".jpg");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.flush();
            fos.close();
            is.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "复制图片到本地失败", e);
            return uri.toString();
        }
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        recipeDAO = new RecipeDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void setupClickListeners() {
        // 点击图片区域，拉起相册
        recipeImage.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(recipeImage);
            imagePickerLauncher.launch("image/*");
        });

        // 绑定返回按钮
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                AnimationUtils.applyRippleEffect(backButton);
                finish();
            });
        }

        // 保存食谱
        saveRecipeButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(saveRecipeButton);
            saveNewRecipe();
        });
    }

    private void saveNewRecipe() {
        String name = recipeName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "食谱名称不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        saveRecipeButton.setEnabled(false);
        saveRecipeButton.setText("保存中...");

        try {
            // 组装新的 Recipe 对象
            Recipe newRecipe = new Recipe();
            newRecipe.setRecipeId(UUID.randomUUID().toString()); // 随机生成 ID
            newRecipe.setName(name);
            newRecipe.setCategory(recipeCategory.getText().toString().trim());
            newRecipe.setDescription(recipeDescription.getText().toString().trim());
            newRecipe.setIngredients(recipeIngredients.getText().toString().trim());
            newRecipe.setInstructions(recipeInstructions.getText().toString().trim());
            newRecipe.setNutritionInfo(recipeNutrition.getText().toString().trim());
            newRecipe.setImageUrl(currentImageUri);

            // 绑定创建者并设置时间戳
            newRecipe.setCreatedBy(userId);
            newRecipe.setCreatedAt(System.currentTimeMillis());
            newRecipe.setUpdatedAt(System.currentTimeMillis());

            // 调用 DAO 的 createRecipe 方法插入数据库
            boolean success = recipeDAO.createRecipe(newRecipe);

            runOnUiThread(() -> {
                saveRecipeButton.setEnabled(true);
                saveRecipeButton.setText("保存并发布食谱");

                if (success) {
                    Toast.makeText(AddRecipeActivity.this, "食谱添加成功！", Toast.LENGTH_SHORT).show();
                    finish(); // 添加成功后关闭页面，返回上一页
                } else {
                    Toast.makeText(AddRecipeActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "添加食谱异常", e);
            runOnUiThread(() -> {
                saveRecipeButton.setEnabled(true);
                saveRecipeButton.setText("保存并发布食谱");
                Toast.makeText(AddRecipeActivity.this, "添加发生异常", Toast.LENGTH_SHORT).show();
            });
        }
    }
}