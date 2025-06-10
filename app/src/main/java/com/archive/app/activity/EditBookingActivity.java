package com.archive.app.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Booking;
import com.example.myapplication.R;

public class EditBookingActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "extra_booking_id";

    private EditText bookingDateEditText;
    private TextView attractionNameTextView;
    private Button saveButton;
    private OpenHelperDataBase dbHelper;
    private Booking currentBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new OpenHelperDataBase(this);
        bookingDateEditText = findViewById(R.id.edit_text_booking_date);
        attractionNameTextView = findViewById(R.id.edit_booking_attraction_name);
        saveButton = findViewById(R.id.button_save_booking);

        long bookingId = getIntent().getLongExtra(EXTRA_BOOKING_ID, -1);
        if (bookingId == -1) {
            Toast.makeText(this, "无效的预订ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentBooking = dbHelper.getBookingById(bookingId);
        if (currentBooking != null) {
            attractionNameTextView.setText("景点: " + currentBooking.getAttractionName());
            bookingDateEditText.setText(currentBooking.getBookingDate());
        } else {
            Toast.makeText(this, "未找到预订信息", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveButton.setOnClickListener(v -> saveBooking());
    }

    private void saveBooking() {
        String newDate = bookingDateEditText.getText().toString().trim();
        if (newDate.isEmpty()) {
            Toast.makeText(this, "请输入预订日期", Toast.LENGTH_SHORT).show();
            return;
        }

        currentBooking.setBookingDate(newDate);
        int result = dbHelper.updateBooking(currentBooking);

        if (result > 0) {
            Toast.makeText(this, "预订已更新", Toast.LENGTH_SHORT).show();
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