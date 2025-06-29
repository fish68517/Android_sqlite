package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.model.User;
import java.util.ArrayList;
import java.util.List;

public class SelectableContactsAdapter extends RecyclerView.Adapter<SelectableContactsAdapter.ViewHolder> {
    private List<User> contacts;
    private List<User> selectedContacts = new ArrayList<>();

    public SelectableContactsAdapter(List<User> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectable_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User contact = contacts.get(position);
        holder.contactName.setText(contact.getNickname());
        holder.checkBox.setChecked(selectedContacts.contains(contact));

        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            if (holder.checkBox.isChecked()) {
                if (!selectedContacts.contains(contact)) {
                    selectedContacts.add(contact);
                }
            } else {
                selectedContacts.remove(contact);
            }
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedContacts.contains(contact)) {
                    selectedContacts.add(contact);
                }
            } else {
                selectedContacts.remove(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public List<User> getSelectedContacts() {
        return selectedContacts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.tv_contact_name);
            checkBox = itemView.findViewById(R.id.checkbox_select_contact);
        }
    }
} 