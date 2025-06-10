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

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_ATTRACTION_ID = "extra_attraction_id";
    public static final String EXTRA_ATTRACTION_NAME = "extra_attraction_name";

    private TextView attractionNameTextView;
    private EditText bookingDateEditText;
    private Button confirmBookingButton;
    private OpenHelperDataBase dbHelper;
    private long attractionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("创建预订");
        }

        dbHelper = new OpenHelperDataBase(this);
        attractionNameTextView = findViewById(R.id.booking_attraction_name);
        bookingDateEditText = findViewById(R.id.edit_text_booking_date);
        confirmBookingButton = findViewById(R.id.button_confirm_booking);

        attractionId = getIntent().getLongExtra(EXTRA_ATTRACTION_ID, -1);
        String attractionName = getIntent().getStringExtra(EXTRA_ATTRACTION_NAME);

        if (attractionId == -1 || attractionName == null) {
            Toast.makeText(this, "无效的景点信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        attractionNameTextView.setText(attractionName);

        confirmBookingButton.setOnClickListener(v -> createBooking());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBooking() {
        String bookingDate = bookingDateEditText.getText().toString().trim();
        if (bookingDate.isEmpty()) {
            Toast.makeText(this, "请输入预订日期", Toast.LENGTH_SHORT).show();
            return;
        }

        // FIXME: 暂时硬编码用户ID为1
        long userId = 1;

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setAttractionId(attractionId);
        booking.setBookingDate(bookingDate);
        booking.setStatus("confirmed");

        long bookingId = dbHelper.addBooking(booking);
        if (bookingId != -1) {
            Toast.makeText(this, "预订成功！", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "预订失败", Toast.LENGTH_SHORT).show();
        }
    }
} 