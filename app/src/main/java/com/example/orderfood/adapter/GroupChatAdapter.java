package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.model.Message;

import java.util.List;
import java.util.Map;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.MessageViewHolder> {

    private final List<Message> messageList;
    private final int currentUserId;
    private final Map<Integer, String> memberNicknames;
    private final OnMessageLongClickListener longClickListener;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RETRACTED = 3;

    public interface OnMessageLongClickListener {
        void onMessageLongClicked(Message message, int position);
    }

    public GroupChatAdapter(List<Message> messageList, int currentUserId, Map<Integer, String> memberNicknames, OnMessageLongClickListener listener) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.memberNicknames = memberNicknames;
        this.longClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isRetracted()) {
            return VIEW_TYPE_RETRACTED;
        }
        return message.getSenderId() == currentUserId ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_message_received, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_retracted, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        int viewType = getItemViewType(position);

        holder.messageContent.setText(message.getContent());

        if (viewType == VIEW_TYPE_RECEIVED) {
            String nickname = memberNicknames.get(message.getSenderId());
            holder.senderName.setText(nickname != null ? nickname : "Unknown User");
        }
        
        if (viewType != VIEW_TYPE_RETRACTED) {
            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onMessageLongClicked(message, position);
                }
                return true;
            });
        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView senderName; // Can be null for sent messages

        MessageViewHolder(View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.tv_message_content);
            senderName = itemView.findViewById(R.id.tv_sender_name);
        }
    }
} 