package com.example.orderfood.adapter;

import android.content.Context;
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
    private List<Object> items;
    private OnContactActionsListener actionsListener;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTACT = 1;

    public interface OnContactActionsListener {
        void onEditClick(User contact);
        void onDeleteClick(User contact);
    }

    public ContactsAdapter(List<Object> items, OnContactActionsListener listener) {
        this.items = items;
        this.actionsListener = listener;
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
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ContactViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerTitle.setText((String) items.get(position));
        } else {
            ContactViewHolder contactHolder = (ContactViewHolder) holder;
            User contact = (User) items.get(position);
            contactHolder.contactName.setText(contact.getNickname());

            contactHolder.itemView.setOnClickListener(v -> {
                Context context = contactHolder.itemView.getContext();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("CONTACT_ID", contact.getId());
                intent.putExtra("CONTACT_NICKNAME", contact.getNickname());
                context.startActivity(intent);
            });

            contactHolder.editButton.setOnClickListener(v -> {
                if (actionsListener != null) {
                    actionsListener.onEditClick(contact);
                }
            });

            contactHolder.deleteButton.setOnClickListener(v -> {
                if (actionsListener != null) {
                    actionsListener.onDeleteClick(contact);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        Button editButton;
        Button deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.tv_contact_name);
            editButton = itemView.findViewById(R.id.btn_edit_contact);
            deleteButton = itemView.findViewById(R.id.btn_delete_contact);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.tv_header);
        }
    }
} 