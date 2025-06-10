package com.archive.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.activity.EditBookingActivity;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Booking;
import com.example.myapplication.R;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final Context context;
    private final List<Booking> bookingList;
    private final OpenHelperDataBase dbHelper;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.attractionName.setText(booking.getAttractionName());
        holder.bookingDate.setText("预订于: " + booking.getBookingDate());

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBookingActivity.class);
            intent.putExtra(EditBookingActivity.EXTRA_BOOKING_ID, booking.getId());
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("删除预订")
                    .setMessage("您确定要删除这个预订吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        dbHelper.deleteBooking(booking.getId());
                        bookingList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, bookingList.size());
                        Toast.makeText(context, "预订已删除", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView attractionName, bookingDate;
        Button editButton, deleteButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionName = itemView.findViewById(R.id.booking_attraction_name);
            bookingDate = itemView.findViewById(R.id.booking_date);
            editButton = itemView.findViewById(R.id.button_edit_booking);
            deleteButton = itemView.findViewById(R.id.button_delete_booking);
        }
    }
} 