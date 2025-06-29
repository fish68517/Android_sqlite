package com.example.orderfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.activity.ChatActivity;
import com.example.orderfood.activity.GroupChatActivity;
import com.example.orderfood.model.SearchResult;

import java.util.List;

public class MessageSearchAdapter extends RecyclerView.Adapter<MessageSearchAdapter.ViewHolder> {

    private List<SearchResult> results;
    private String query;

    public MessageSearchAdapter(List<SearchResult> results, String query) {
        this.results = results;
        this.query = query;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult result = results.get(position);

        // Highlight the query in the content
        String content = result.getContent();
        int startIndex = content.toLowerCase().indexOf(query.toLowerCase());
        if (startIndex != -1) {
            String highlightedContent = content.substring(0, startIndex) +
                    "<font color='red'>" + content.substring(startIndex, startIndex + query.length()) + "</font>" +
                    content.substring(startIndex + query.length());
            holder.content.setText(Html.fromHtml(highlightedContent));
        } else {
            holder.content.setText(content);
        }

        String details = "来自 " + result.getSenderNickname() + " 在 " + result.getConversationName() + " - " + result.getTimestamp();
        holder.details.setText(details);

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent;
            if (result.getGroupId() > 0) { // It's a group chat
                intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("GROUP_ID", result.getGroupId());
                intent.putExtra("GROUP_NAME", result.getConversationName());
            } else { // It's a direct message
                intent = new Intent(context, ChatActivity.class);
                intent.putExtra("CONTACT_ID", result.getSenderId() == holder.itemView.getContext().getSharedPreferences("AppSession", Context.MODE_PRIVATE).getInt("CURRENT_USER_ID", -1) ? result.getReceiverId() : result.getSenderId());
                intent.putExtra("CONTACT_NICKNAME", result.getConversationName());
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView details;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_message_content);
            details = itemView.findViewById(R.id.tv_message_details);
        }
    }
} 