package com.archive.app.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.adapter.BookingAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Booking;
import com.example.myapplication.R;

import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private OpenHelperDataBase dbHelper;
    private TextView noBookingsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new OpenHelperDataBase(this);
        recyclerView = findViewById(R.id.recycler_view_bookings);
        noBookingsTextView = findViewById(R.id.text_view_no_bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        // FIXME: Hardcoded user ID 1
        long userId = 1;
        bookingList = dbHelper.getBookingsByUserId(userId);

        if (bookingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noBookingsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noBookingsTextView.setVisibility(View.GONE);
            adapter = new BookingAdapter(this, bookingList);
            recyclerView.setAdapter(adapter);
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