package com.example.orderfood.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.model.Dish;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditDishActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private Toolbar toolbar;
    private ImageView ivDish;
    private TextInputEditText etName;
    private TextInputEditText etPrice;
    private TextInputEditText etStock;
    private TextInputEditText etDescription;
    private TextInputEditText etCategory;
    private MaterialButton btnSave;

    private DBMysqlHelper dbHelper;
    private Dish dish;
    private String selectedImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dish);

        dbHelper = DBMysqlHelper.getInstance(this);
        dish = (Dish) getIntent().getSerializableExtra("dish");

        initViews();
        setupToolbar();
        if (dish != null) {
            fillDishData();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivDish = findViewById(R.id.ivDish);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etStock.setVisibility(View.GONE);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        btnSave = findViewById(R.id.btnSave);

        ivDish.setOnClickListener(v -> {
           /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);*/
            checkPermissionAndOpenPicker();
        });

        btnSave.setOnClickListener(v -> saveDish());
    }

    private static final int PICK_IMAGE_REQUEST = 1;


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            selectedImagePath = imageUri.toString();
            System.out.println("selectedImagePath: " + selectedImagePath);
            Glide.with(this).load(imageUri).into(ivDish);
        }
/*        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            selectedImagePath = imageUri.getPath();
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.mipmap.jiushui_natie)
                    .error(R.mipmap.jiushui_natie)
                    .into(ivDish);
        }*/
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(dish == null ? "添加菜品" : "编辑菜品");
    }

    private void fillDishData() {
        etName.setText(dish.getName());
        etPrice.setText(String.valueOf(dish.getPrice()));
        etStock.setText(String.valueOf(dish.getStock()));
        etDescription.setText(dish.getDescription());
        etCategory.setText(dish.getCategory());

        if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {

            if (dish.getImageUrl().startsWith("content://") || dish.getImageUrl().contains("media")) {
                System.out.println("imageResId ggg: " + dish.getImageUrl());
                String imageUrl =dish.getImageUrl();
                Glide.with(this)
                        .load(imageUrl)
                        .into(ivDish);
            } else {
                int imageResId = getResources().getIdentifier(
                        dish.getImageUrl(), "mipmap", getPackageName());
                Glide.with(this)
                        .load(imageResId)
                        .placeholder(R.mipmap.jiushui_natie)
                        .error(R.mipmap.jiushui_natie)
                        .into(ivDish);
            }
        }
    }

    private void saveDish() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("请输入菜品名称");
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("请输入价格");
            return;
        }


        double price = Double.parseDouble(priceStr);


        if (dish == null) {
            dish = new Dish();
        }

        dish.setName(name);
        dish.setPrice(price);
        dish.setStock(0);
        dish.setDescription(description);
        dish.setCategory(category);
        dish.setMerchantId(MyApplication.curMerchant.getMerchantId());
        
        if (selectedImagePath != null) {
            dish.setImageUrl(selectedImagePath);
        }

        if (dish.getDishId() == 0) {
            // 添加新菜品
            System.out.println("添加新菜品");
            dish.setMerchantName(MyApplication.curMerchant.getName());
            dbHelper.addDish(dish, new DBMysqlHelper.DatabaseCallback<Dish>() {
                @Override
                public void onSuccess(Dish result) {
                    Toast.makeText(EditDishActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("添加失败" + e.getMessage());
                    Toast.makeText(EditDishActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 更新已有菜品
            dbHelper.updateDish(dish, new DBMysqlHelper.DatabaseCallback<Dish>() {
                @Override
                public void onSuccess(Dish result) {
                    Toast.makeText(EditDishActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EditDishActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 2;

    private void checkPermissionAndOpenPicker() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }
}
