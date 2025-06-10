package com.archive.app.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Itinerary;
import com.example.myapplication.R;

public class AddItineraryActivity extends AppCompatActivity {

    private EditText nameEditText, startDateEditText, endDateEditText;
    private Button saveButton;
    private OpenHelperDataBase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_itinerary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new OpenHelperDataBase(this);
        nameEditText = findViewById(R.id.edit_text_itinerary_name);
        startDateEditText = findViewById(R.id.edit_text_start_date);
        endDateEditText = findViewById(R.id.edit_text_end_date);
        saveButton = findViewById(R.id.button_save_itinerary);

        saveButton.setOnClickListener(v -> saveItinerary());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 处理返回按钮的点击事件
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItinerary() {
        String name = nameEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        if (name.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // FIXME: 暂时硬编码用户ID为1。在实际应用中，应从登录会话中获取。
        long userId = 1;

        Itinerary itinerary = new Itinerary();
        itinerary.setUserId(userId);
        itinerary.setName(name);
        itinerary.setStartDate(startDate);
        itinerary.setEndDate(endDate);

        long id = dbHelper.addItinerary(itinerary);
        if (id != -1) {
            Toast.makeText(this, "行程已保存", Toast.LENGTH_SHORT).show();
            finish(); // 保存成功后关闭此活动
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
} 