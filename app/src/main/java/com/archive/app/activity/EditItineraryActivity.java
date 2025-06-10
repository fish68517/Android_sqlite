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

public class EditItineraryActivity extends AppCompatActivity {

    public static final String EXTRA_ITINERARY_ID = "extra_itinerary_id";

    private EditText nameEditText, startDateEditText, endDateEditText;
    private Button saveButton;
    private OpenHelperDataBase dbHelper;
    private Itinerary currentItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_itinerary);

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

        long itineraryId = getIntent().getLongExtra(EXTRA_ITINERARY_ID, -1);
        if (itineraryId == -1) {
            Toast.makeText(this, "无效的行程ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentItinerary = dbHelper.getItineraryById(itineraryId);

        if (currentItinerary != null) {
            nameEditText.setText(currentItinerary.getName());
            startDateEditText.setText(currentItinerary.getStartDate());
            endDateEditText.setText(currentItinerary.getEndDate());
        } else {
            Toast.makeText(this, "未找到行程信息", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveButton.setOnClickListener(v -> saveItinerary());
    }

    private void saveItinerary() {
        String name = nameEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        if (name.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        currentItinerary.setName(name);
        currentItinerary.setStartDate(startDate);
        currentItinerary.setEndDate(endDate);

        int result = dbHelper.updateItinerary(currentItinerary);

        if (result > 0) {
            Toast.makeText(this, "行程已更新", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
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
} 