package com.archive.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Itinerary;
import com.example.myapplication.R;

public class ItineraryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITINERARY_ID = "extra_itinerary_id";

    private TextView nameTextView, startDateTextView, endDateTextView;
    private Button editButton;
    private OpenHelperDataBase dbHelper;
    private Itinerary currentItinerary;
    private long itineraryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new OpenHelperDataBase(this);
        nameTextView = findViewById(R.id.detail_itinerary_name);
        startDateTextView = findViewById(R.id.detail_itinerary_start_date);
        endDateTextView = findViewById(R.id.detail_itinerary_end_date);
        editButton = findViewById(R.id.button_edit_itinerary);

        itineraryId = getIntent().getLongExtra(EXTRA_ITINERARY_ID, -1);

        if (itineraryId == -1) {
            Toast.makeText(this, "无效的行程ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ItineraryDetailActivity.this, EditItineraryActivity.class);
            intent.putExtra(EditItineraryActivity.EXTRA_ITINERARY_ID, itineraryId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItineraryData();
    }

    private void loadItineraryData() {
        currentItinerary = dbHelper.getItineraryById(itineraryId);
        if (currentItinerary != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("行程: " + currentItinerary.getName());
            }
            nameTextView.setText(currentItinerary.getName());
            startDateTextView.setText(currentItinerary.getStartDate());
            endDateTextView.setText(currentItinerary.getEndDate());
        } else {
            Toast.makeText(this, "未找到行程", Toast.LENGTH_SHORT).show();
            finish();
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