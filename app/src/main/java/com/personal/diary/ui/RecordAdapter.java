package com.personal.diary.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.diary.R;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    public interface Listener {
        void onItemClick(RecordItem item);

        void onPrimary(RecordItem item);

        void onSecondary(RecordItem item);

        void onEdit(RecordItem item);

        void onDelete(RecordItem item);
    }

    private final List<RecordItem> items = new ArrayList<>();
    private final Listener listener;

    public RecordAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<RecordItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        RecordItem item = items.get(position);
        holder.titleText.setText(item.title);
        holder.bodyText.setText(item.body);
        holder.metaText.setText(item.meta);
        bindImage(holder.recordImage, item.imageData);

        bindButton(holder.primaryButton, item.primaryAction, () -> listener.onPrimary(item));
        bindButton(holder.secondaryButton, item.secondaryAction, () -> listener.onSecondary(item));
        holder.editButton.setVisibility(item.showEdit ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(item.showDelete ? View.VISIBLE : View.GONE);
        holder.editButton.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        holder.deleteButton.setTextColor(holder.itemView.getContext().getColor(R.color.white));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.editButton.setOnClickListener(v -> listener.onEdit(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindButton(Button button, String text, Runnable runnable) {
        if (text == null || text.trim().isEmpty()) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setVisibility(View.VISIBLE);
        button.setText(text);
        button.setTextColor(button.getContext().getColor(R.color.white));
        button.setOnClickListener(v -> runnable.run());
    }

    private void bindImage(ImageView imageView, byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.GONE);
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView bodyText;
        TextView metaText;
        ImageView recordImage;
        Button primaryButton;
        Button secondaryButton;
        Button editButton;
        Button deleteButton;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            bodyText = itemView.findViewById(R.id.bodyText);
            metaText = itemView.findViewById(R.id.metaText);
            recordImage = itemView.findViewById(R.id.recordImage);
            primaryButton = itemView.findViewById(R.id.primaryButton);
            secondaryButton = itemView.findViewById(R.id.secondaryButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
