package com.example.orderfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.activity.ChatActivity;
import com.example.orderfood.activity.GroupChatActivity;
import com.example.orderfood.model.Conversation;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Conversation> conversations;

    public ConversationAdapter(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        holder.name.setText(conversation.getName());
        holder.lastMessage.setText(conversation.getLastMessage());
        holder.timestamp.setText(conversation.getTimestamp()); // Consider formatting this nicely

        if (conversation.isGroup()) {
            holder.avatar.setImageResource(R.mipmap.ic_launcher); // Group avatar
        } else {
            holder.avatar.setImageResource(R.mipmap.ic_launcher_round); // User avatar
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent;
            if (conversation.isGroup()) {
                intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("GROUP_ID", conversation.getId());
                intent.putExtra("GROUP_NAME", conversation.getName());
            } else {
                intent = new Intent(context, ChatActivity.class);
                intent.putExtra("CONTACT_ID", (int) conversation.getId());
                intent.putExtra("CONTACT_NICKNAME", conversation.getName());
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }
    
    public void updateList(List<Conversation> newList) {
        conversations = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView lastMessage;
        TextView timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            name = itemView.findViewById(R.id.tv_conversation_name);
            lastMessage = itemView.findViewById(R.id.tv_last_message);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
} 