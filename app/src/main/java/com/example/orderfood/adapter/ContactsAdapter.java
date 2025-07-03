package com.example.orderfood.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.activity.ChatActivity;
import com.example.orderfood.model.User;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTACT = 1;

    private final List<Object> items;
    private final OnContactActionsListener actionsListener;

    public ContactsAdapter(List<Object> items, OnContactActionsListener actionsListener) {
        this.items = items;
        this.actionsListener = actionsListener;
    }

    public interface OnContactActionsListener {
        void onEditClick(User contact);
        void onDeleteClick(User contact);
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_CONTACT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_contact_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_contact, parent, false);
            return new ContactViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerTitle.setText((String) items.get(position));
        } else if (itemViewType == VIEW_TYPE_CONTACT) {
            ContactViewHolder contactHolder = (ContactViewHolder) holder;
            User contact = (User) items.get(position);
            contactHolder.contactName.setText(contact.getNickname());

            contactHolder.itemView.setOnClickListener(v -> {
                android.content.Context context = contactHolder.itemView.getContext();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("CONTACT_ID", contact.getId());
                intent.putExtra("CONTACT_NICKNAME", contact.getNickname());
                context.startActivity(intent);
            });

            contactHolder.editButton.setOnClickListener(v -> {
                actionsListener.onEditClick(contact);
            });

            contactHolder.deleteButton.setOnClickListener(v -> {
                actionsListener.onDeleteClick(contact);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public final TextView contactName;
        public final Button editButton;
        public final Button deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.tv_contact_name);
            editButton = itemView.findViewById(R.id.btn_edit_contact);
            deleteButton = itemView.findViewById(R.id.btn_delete_contact);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.tv_header);
        }
    }
} 