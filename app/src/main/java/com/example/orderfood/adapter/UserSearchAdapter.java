package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.User;

import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private final List<User> userList;
    private final OnAddContactListener listener;

    public UserSearchAdapter(List<User> userList, OnAddContactListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public interface OnAddContactListener {
        void onAddContact(User user);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nickname.setText(user.getNickname());
        holder.addButton.setOnClickListener(v -> {
            listener.onAddContact(user);
            holder.addButton.setText("已添加");
            holder.addButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nickname;
        public final Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.tv_user_nickname);
            addButton = itemView.findViewById(R.id.btn_add);
        }
    }
} 