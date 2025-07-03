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

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    private final List<User> memberList;
    private final int currentUserId;
    private final int creatorId;
    private final OnRemoveMemberClickListener listener;

    public GroupMemberAdapter(List<User> memberList, int currentUserId, int creatorId, OnRemoveMemberClickListener listener) {
        this.memberList = memberList;
        this.currentUserId = currentUserId;
        this.creatorId = creatorId;
        this.listener = listener;
    }

    public interface OnRemoveMemberClickListener {
        void onRemoveMember(User member);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User member = memberList.get(position);
        holder.nickname.setText(member.getNickname());

        // Only the group creator can remove other members
        if (currentUserId == creatorId && member.getId() != currentUserId) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(v -> {
                listener.onRemoveMember(member);
            });
        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nickname;
        public final Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.tv_member_nickname);
            removeButton = itemView.findViewById(R.id.btn_remove_member);
        }
    }
} 