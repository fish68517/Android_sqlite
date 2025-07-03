package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.Message;
import com.example.orderfood.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RETRACTED = 3;

    private final List<Message> messageList;
    private final int currentUserId;
    private final Map<Integer, User> membersMap;
    private final OnMessageLongClickListener longClickListener;

    public GroupChatAdapter(List<Message> messageList, int currentUserId, Map<Integer, User> membersMap, OnMessageLongClickListener longClickListener) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.membersMap = membersMap;
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
            view = inflater.inflate(R.layout.item_group_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = inflater.inflate(R.layout.item_group_message_received, parent, false);
        } else { // VIEW_TYPE_RETRACTED
            view = inflater.inflate(R.layout.item_message_retracted, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message, position, longClickListener, membersMap.get(message.getSenderId()));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageContent;
        private final TextView timestamp;
        private final TextView senderNickname;
        private final int viewType;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            messageContent = itemView.findViewById(R.id.tv_message_content);
            timestamp = (viewType != VIEW_TYPE_RETRACTED) ? itemView.findViewById(R.id.tv_timestamp) : null;
            senderNickname = (viewType == VIEW_TYPE_RECEIVED) ? itemView.findViewById(R.id.tv_sender_nickname) : null;
        }

        public void bind(Message message, int position, OnMessageLongClickListener listener, User sender) {
            messageContent.setText(message.getContent());

            if (message.isRetracted()) {
                itemView.setOnLongClickListener(null);
                if (timestamp != null) timestamp.setVisibility(View.GONE);
                if (senderNickname != null) senderNickname.setVisibility(View.GONE);
            } else {
                if (timestamp != null) {
                    timestamp.setText(formatTimestamp(message.getTimestamp()));
                    timestamp.setVisibility(View.VISIBLE);
                }
                if (senderNickname != null && sender != null) {
                    senderNickname.setText(sender.getNickname());
                    senderNickname.setVisibility(View.VISIBLE);
                } else if (senderNickname != null) {
                    senderNickname.setVisibility(View.GONE);
                }

                itemView.setOnLongClickListener(v -> {
                    listener.onMessageLongClicked(message, position);
                    return true;
                });
            }
        }

        private String formatTimestamp(String dbTimestamp) {
            if (dbTimestamp == null || dbTimestamp.isEmpty()) return "";
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                return displayFormat.format(dbFormat.parse(dbTimestamp));
            } catch (ParseException e) {
                return "";
            }
        }
    }
} 