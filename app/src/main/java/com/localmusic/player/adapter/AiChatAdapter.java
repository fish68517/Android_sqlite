package com.localmusic.player.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localmusic.player.R;
import com.localmusic.player.ai.AiChatMessage;
import com.localmusic.player.databinding.ItemAiMessageAssistantBinding;
import com.localmusic.player.databinding.ItemAiMessageUserBinding;

import java.util.ArrayList;
import java.util.List;

public class AiChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ASSISTANT = 0;
    private static final int TYPE_USER = 1;

    private final List<AiChatMessage> messages = new ArrayList<>();

    public void submitList(List<AiChatMessage> items) {
        messages.clear();
        messages.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return AiChatMessage.ROLE_USER.equals(messages.get(position).getRole())
                ? TYPE_USER
                : TYPE_ASSISTANT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            ItemAiMessageUserBinding binding = ItemAiMessageUserBinding.inflate(inflater, parent, false);
            return new UserViewHolder(binding);
        }
        ItemAiMessageAssistantBinding binding = ItemAiMessageAssistantBinding.inflate(inflater, parent, false);
        return new AssistantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AiChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof AssistantViewHolder) {
            ((AssistantViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemAiMessageUserBinding binding;

        UserViewHolder(ItemAiMessageUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AiChatMessage message) {
            binding.textMessage.setText(message.getContent());
        }
    }

    static class AssistantViewHolder extends RecyclerView.ViewHolder {

        private final ItemAiMessageAssistantBinding binding;

        AssistantViewHolder(ItemAiMessageAssistantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AiChatMessage message) {
            binding.textMessage.setText(message.getContent());
            binding.textStatus.setText(message.isPending()
                    ? binding.getRoot().getContext().getString(R.string.ai_status_busy)
                    : binding.getRoot().getContext().getString(R.string.ai_status_ready));
        }
    }
}
