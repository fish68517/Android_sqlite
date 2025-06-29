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

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {
    private List<User> members;
    private OnRemoveMemberClickListener listener;
    private int creatorId;
    private int currentUserId;

    public interface OnRemoveMemberClickListener {
        void onRemoveMemberClick(User member);
    }

    public GroupMembersAdapter(List<User> members, int creatorId, int currentUserId, OnRemoveMemberClickListener listener) {
        this.members = members;
        this.creatorId = creatorId;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User member = members.get(position);
        holder.memberName.setText(member.getNickname());

        // Only the group creator can remove members
        if (currentUserId == creatorId && member.getId() != creatorId) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveMemberClick(member);
                }
            });
        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.tv_member_name);
            removeButton = itemView.findViewById(R.id.btn_remove_member);
        }
    }
} 