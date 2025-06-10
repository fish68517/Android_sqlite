package com.archive.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.activity.ItineraryDetailActivity;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Itinerary;
import com.example.myapplication.R;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {

    private final Context context;
    private final List<Itinerary> itineraryList;
    private final OpenHelperDataBase dbHelper;

    public ItineraryAdapter(Context context, List<Itinerary> itineraryList) {
        this.context = context;
        this.itineraryList = itineraryList;
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_itinerary, parent, false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
        Itinerary itinerary = itineraryList.get(position);
        holder.nameTextView.setText(itinerary.getName());
        holder.datesTextView.setText(String.format("%s - %s", itinerary.getStartDate(), itinerary.getEndDate()));

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteItinerary(itinerary.getId());
            itineraryList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itineraryList.size());
            Toast.makeText(context, "行程已删除", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItineraryDetailActivity.class);
            intent.putExtra(ItineraryDetailActivity.EXTRA_ITINERARY_ID, itinerary.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itineraryList.size();
    }

    static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView datesTextView;
        ImageButton deleteButton;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itinerary_name);
            datesTextView = itemView.findViewById(R.id.itinerary_dates);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
} 