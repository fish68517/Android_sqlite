package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RETRACTED = 3;

    private final List<Message> messageList;
    private final int currentUserId;
    private final OnMessageLongClickListener longClickListener;

    public ChatAdapter(List<Message> messageList, int currentUserId, OnMessageLongClickListener longClickListener) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.longClickListener = longClickListener;
    }

    public interface OnMessageLongClickListener {
        void onMessageLongClicked(Message message, int position);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isRetracted()) {
            return VIEW_TYPE_RETRACTED;
        } else if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = inflater.inflate(R.layout.item_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = inflater.inflate(R.layout.item_message_received, parent, false);
        } else { // VIEW_TYPE_RETRACTED
            view = inflater.inflate(R.layout.item_message_retracted, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message, position, longClickListener, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageContent;
        private final TextView timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.tv_message_content);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        public void bind(Message message, int position, OnMessageLongClickListener listener, int viewType) {
            messageContent.setText(message.getContent());

            if (viewType != VIEW_TYPE_RETRACTED) {
                if (timestamp != null) {
                    timestamp.setText(formatTimestamp(message.getTimestamp()));
                    timestamp.setVisibility(View.VISIBLE);
                }
                itemView.setOnLongClickListener(v -> {
                    listener.onMessageLongClicked(message, position);
                    return true;
                });
            } else {
                if (timestamp != null) timestamp.setVisibility(View.GONE);
                itemView.setOnLongClickListener(null); // No actions for retracted messages
            }
        }

        private String formatTimestamp(String dbTimestamp) {
            if (dbTimestamp == null || dbTimestamp.isEmpty()) {
                return "";
            }
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                return displayFormat.format(dbFormat.parse(dbTimestamp));
            } catch (ParseException e) {
                if (dbTimestamp.length() > 16) {
                    return dbTimestamp.substring(11, 16);
                }
                return "";
            }
        }
    }
} 