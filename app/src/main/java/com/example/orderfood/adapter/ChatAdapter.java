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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private final int currentUserId;
    private OnMessageLongClickListener longClickListener;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RETRACTED = 3;

    public interface OnMessageLongClickListener {
        void onMessageLongClicked(Message message, int position);
    }

    public ChatAdapter(List<Message> messageList, int currentUserId, OnMessageLongClickListener listener) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.longClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isRetracted()) {
            return VIEW_TYPE_RETRACTED;
        }
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        } else { // VIEW_TYPE_RETRACTED
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_retracted, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (getItemViewType(position) != VIEW_TYPE_RETRACTED) {
            holder.messageContent.setText(message.getContent());
            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onMessageLongClicked(message, position);
                }
                return true;
            });
        } else {
            // For retracted messages, we might have a different holder or just set text
            holder.messageContent.setText(message.getContent());
            holder.itemView.setOnLongClickListener(null); // No actions for retracted messages
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.tv_message_content);
        }
    }
} 