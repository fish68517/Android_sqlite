package com.archive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Attraction;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import java.util.Locale;

public class AttractionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ATTRACTION_ID = "extra_attraction_id";

    private ImageView imageView;
    private TextView nameTextView, descriptionTextView, locationTextView, priceTextView;
    private Button bookNowButton;
    private OpenHelperDataBase dbHelper;
    private Attraction currentAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new OpenHelperDataBase(this);
        imageView = findViewById(R.id.detail_attraction_image);
        nameTextView = findViewById(R.id.detail_attraction_name);
        descriptionTextView = findViewById(R.id.detail_attraction_description);
        locationTextView = findViewById(R.id.detail_attraction_location);
        priceTextView = findViewById(R.id.detail_attraction_price);
        bookNowButton = findViewById(R.id.button_book_now);

        long attractionId = getIntent().getLongExtra(EXTRA_ATTRACTION_ID, -1);

        if (attractionId == -1) {
            Toast.makeText(this, "无效的景点ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Attraction attraction = dbHelper.getAttractionById(attractionId);
        currentAttraction = attraction;
        if (currentAttraction != null) {
            setTitle(currentAttraction.getName());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentAttraction.getName());
            }
            nameTextView.setText(currentAttraction.getName());
            descriptionTextView.setText(currentAttraction.getDescription());
            locationTextView.setText(String.format("地点: %s", currentAttraction.getLocation()));
            priceTextView.setText(String.format(Locale.getDefault(), "价格: %.2f 元", currentAttraction.getPrice()));
            // 这里可以设置图片加载
            // 从数据库获取图片名称字符串
            String imageName = attraction.getImageUrl();
            int imageResId = 0;
            if (imageName != null && !imageName.isEmpty()) {
                // 移除文件扩展名
                String drawableName = imageName.substring(0, imageName.lastIndexOf('.'));
                // 动态获取资源ID
                imageResId = this.getResources().getIdentifier(drawableName, "drawable", this.getPackageName());
            }

            Glide.with(this)
                    .load(imageResId) // 加载获取到的资源ID
                    .error(R.drawable.attraction_image4) // 如果ID为0或加载失败，显示默认图片
                    .into(imageView);

        } else {
            Toast.makeText(this, "未找到景点", Toast.LENGTH_SHORT).show();
            finish();
        }

        bookNowButton.setOnClickListener(v -> {
            if (currentAttraction != null) {
                Intent intent = new Intent(AttractionDetailActivity.this, BookingActivity.class);
                intent.putExtra(BookingActivity.EXTRA_ATTRACTION_ID, currentAttraction.getId());
                intent.putExtra(BookingActivity.EXTRA_ATTRACTION_NAME, currentAttraction.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 