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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableContactsAdapter extends RecyclerView.Adapter<SelectableContactsAdapter.ViewHolder> {

    private final List<User> contactList;
    private final Set<Integer> selectedContactIds;

    public SelectableContactsAdapter(List<User> contactList) {
        this.contactList = contactList;
        this.selectedContactIds = new HashSet<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selectable_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User contact = contactList.get(position);
        holder.nickname.setText(contact.getNickname());
        holder.checkBox.setChecked(selectedContactIds.contains(contact.getId()));

        holder.itemView.setOnClickListener(v -> {
            if (selectedContactIds.contains(contact.getId())) {
                selectedContactIds.remove(contact.getId());
            } else {
                selectedContactIds.add(contact.getId());
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public List<Integer> getSelectedContactIds() {
        return new ArrayList<>(selectedContactIds);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nickname;
        public final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.tv_nickname);
            checkBox = itemView.findViewById(R.id.checkbox_select);
        }
    }
} 