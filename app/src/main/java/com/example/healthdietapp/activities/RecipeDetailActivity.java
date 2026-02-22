package com.example.healthdietapp.activities;

import android.content.Intent;
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
import com.example.healthdietapp.utils.ImageUtils;

/**
 * RecipeDetailActivity - Displays detailed information about a recipe and allows editing
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private static final String TAG = "RecipeDetailActivity";

    private EditText recipeName;
    private EditText recipeCategory;
    private EditText recipeDescription;
    private EditText recipeIngredients;
    private EditText recipeInstructions;
    private EditText recipeNutrition;

    private Button addRecipeButton;
    private Button updateRecipeButton; // 新增：更新食谱按钮
    private Button backButton;
    private TextView toolbarTitle;
    private ImageView recipeImage;

    private DatabaseHelper dbHelper;
    private RecipeDAO recipeDAO;
    private Recipe recipe;
    private String recipeId;

    private String currentImageUri = ""; // 记录当前图片路径（原有或新选相册图片）
    private ActivityResultLauncher<String> imagePickerLauncher; // 相册选择器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initializeViews();
        setupImagePicker();
        initializeDatabase();
        loadRecipeData();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText("食谱详情 (可编辑)");
        }
        backButton = findViewById(R.id.backButton);

        recipeImage = findViewById(R.id.recipeImage);
        recipeName = findViewById(R.id.recipeName);
        recipeCategory = findViewById(R.id.recipeCategory);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeNutrition = findViewById(R.id.recipeNutrition);

        addRecipeButton = findViewById(R.id.addRecipeButton);
        updateRecipeButton = findViewById(R.id.updateRecipeButton);
    }

    // 1. 替换原有的 setupImagePicker 方法
    private void setupImagePicker() {
        // 注册相册返回结果
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // 核心修改：将外部相册图片拷贝到 APP 私有目录，获得绝对路径
                        String localPath = copyImageToInternalStorage(uri);
                        currentImageUri = localPath;

                        // 显示图片 (如果是绝对路径，通过 Uri.fromFile 转换)
                        recipeImage.setImageURI(Uri.fromFile(new java.io.File(localPath)));
                        Log.d(TAG, "图片已保存至私有目录: " + currentImageUri);
                    }
                }
        );
    }

    // 2. 在下方新增这个方法：专门用来拷贝相册图片到私有目录
    private String copyImageToInternalStorage(Uri uri) {
        try {
            java.io.InputStream is = getContentResolver().openInputStream(uri);
            // 在 APP 内部存储创建一个 recipe_images 文件夹
            java.io.File dir = new java.io.File(getFilesDir(), "recipe_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成唯一的文件名
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

            // 返回类似 /data/user/0/com.example.healthdietapp/files/recipe_images/img_12345.jpg 的绝对路径
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "复制图片到本地失败", e);
            return uri.toString(); // 如果拷贝失败，降级返回原路径
        }
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        recipeDAO = new RecipeDAO(dbHelper);
        recipeId = getIntent().getStringExtra("recipe_id");
    }

    private void loadRecipeData() {
        if (recipeId == null) {
            Toast.makeText(this, "未获取到食谱ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            try {
                recipe = recipeDAO.getRecipeById(recipeId);
                runOnUiThread(() -> {
                    if (recipe != null) {
                        displayRecipeDetails();
                    } else {
                        Toast.makeText(this, "食谱加载失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "加载食谱异常", e);
            }
        }).start();
    }

    private void displayRecipeDetails() {
        recipeName.setText(recipe.getName());
        recipeCategory.setText(recipe.getCategory() != null ? recipe.getCategory() : "");
        recipeDescription.setText(recipe.getDescription() != null ? recipe.getDescription() : "");
        recipeIngredients.setText(recipe.getIngredients() != null ? recipe.getIngredients() : "");
        recipeInstructions.setText(recipe.getInstructions() != null ? recipe.getInstructions() : "");
        recipeNutrition.setText(recipe.getNutritionInfo() != null ? recipe.getNutritionInfo() : "");

        currentImageUri = recipe.getImageUrl() != null ? recipe.getImageUrl() : "";
        Log.d(TAG, "当前图片路径: " + currentImageUri);
        ImageUtils.loadFirstImage(recipeImage, currentImageUri);
    }

    private void setupClickListeners() {
        // 点击大图，拉起相册选取新图片
        recipeImage.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(recipeImage);
            imagePickerLauncher.launch("image/*");
        });

        // 原有的添加到安排按钮
        addRecipeButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(addRecipeButton);
            if (recipe != null) {
                try {
                    Intent intent = new Intent(this, AddRecipeActivity.class);
                    intent.putExtra("recipe_id", recipe.getRecipeId());
                    startActivity(intent);
                    AnimationUtils.applySlideInActivityTransition(this);
                } catch (Exception e) {
                    Toast.makeText(this, "功能暂未实现", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 绑定更新食谱逻辑
        updateRecipeButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(updateRecipeButton);
            updateRecipeData();
        });

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                AnimationUtils.applyRippleEffect(backButton);
                finish();
            });
        }
    }

    private void updateRecipeData() {
        if (recipe == null) return;

        // 获取当前输入框中的最新值
        recipe.setName(recipeName.getText().toString().trim());
        recipe.setCategory(recipeCategory.getText().toString().trim());
        recipe.setDescription(recipeDescription.getText().toString().trim());
        recipe.setIngredients(recipeIngredients.getText().toString().trim());
        recipe.setInstructions(recipeInstructions.getText().toString().trim());
        recipe.setNutritionInfo(recipeNutrition.getText().toString().trim());
        recipe.setImageUrl(currentImageUri);

        // 防止食谱名字为空
        if (recipe.getName().isEmpty()) {
            Toast.makeText(this, "食谱名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        updateRecipeButton.setEnabled(false);
        updateRecipeButton.setText("更新中...");

        new Thread(() -> {
            try {
                // 注意：这里需要确保你的 RecipeDAO 里面有 updateRecipe() 方法
                boolean success = recipeDAO.updateRecipe(recipe);
                runOnUiThread(() -> {
                    updateRecipeButton.setEnabled(true);
                    updateRecipeButton.setText("更新食谱");

                    if (success) {
                        Toast.makeText(RecipeDetailActivity.this, "食谱更新成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecipeDetailActivity.this, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "更新食谱异常", e);
                runOnUiThread(() -> {
                    updateRecipeButton.setEnabled(true);
                    updateRecipeButton.setText("更新食谱");
                    Toast.makeText(RecipeDetailActivity.this, "更新异常", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}