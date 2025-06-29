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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private final int currentUserId;
    private final Map<Integer, User> membersMap;
    private OnMessageLongClickListener longClickListener;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RETRACTED = 3;

    public interface OnMessageLongClickListener {
        void onMessageLongClicked(Message message, int position);
    }

    public GroupChatAdapter(List<Message> messageList, int currentUserId, Map<Integer, User> membersMap, OnMessageLongClickListener listener) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.membersMap = membersMap;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_message_received, parent, false);
        } else { // VIEW_TYPE_RETRACTED
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_retracted, parent, false);
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

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, timestamp, senderNickname;

        MessageViewHolder(View itemView, int viewType) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.tv_message_content);
            if (viewType != VIEW_TYPE_RETRACTED) {
                timestamp = itemView.findViewById(R.id.tv_timestamp);
                if (viewType == VIEW_TYPE_RECEIVED) {
                    senderNickname = itemView.findViewById(R.id.tv_sender_nickname);
                }
            }
        }

        void bind(final Message message, final int position, final OnMessageLongClickListener listener, final User sender) {
            messageContent.setText(message.getContent());

            if (message.isRetracted()) {
                itemView.setOnLongClickListener(null);
                if (timestamp != null) timestamp.setVisibility(View.GONE);
                if (senderNickname != null) senderNickname.setVisibility(View.GONE);
            } else {
                if (timestamp != null) timestamp.setText(formatTimestamp(message.getTimestamp()));
                if (senderNickname != null && sender != null) {
                    senderNickname.setText(sender.getNickname());
                    senderNickname.setVisibility(View.VISIBLE);
                } else if (senderNickname != null) {
                     senderNickname.setVisibility(View.GONE);
                }

                itemView.setOnLongClickListener(v -> {
                    if (listener != null) {
                        listener.onMessageLongClicked(message, position);
                    }
                    return true;
                });
            }
        }

        private String formatTimestamp(String dbTimestamp) {
            if (dbTimestamp == null || dbTimestamp.isEmpty()) return "";
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                Date date = dbFormat.parse(dbTimestamp);
                return displayFormat.format(date);
            } catch (ParseException e) {
                return "";
            }
        }
    }
} 