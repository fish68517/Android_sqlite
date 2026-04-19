package com.localmusic.player.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.localmusic.player.R;
import com.localmusic.player.ai.AiChatMessage;
import com.localmusic.player.ai.AiPromptBuilder;
import com.localmusic.player.ai.SiliconFlowAiService;
import com.localmusic.player.adapter.AiChatAdapter;
import com.localmusic.player.databinding.FragmentAiAssistantBinding;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.MusicLibraryRepository;

import java.util.ArrayList;
import java.util.List;

public class AiAssistantFragment extends Fragment
        implements MusicLibraryRepository.Listener, MusicPlayerManager.Listener {

    private final List<AiChatMessage> messages = new ArrayList<>();

    private FragmentAiAssistantBinding binding;
    private AiChatAdapter adapter;
    private SiliconFlowAiService aiService;
    private MusicLibraryRepository repository;
    private MusicPlayerManager playerManager;
    private AiPromptBuilder promptBuilder;

    public AiAssistantFragment() {
        super(R.layout.fragment_ai_assistant);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAiAssistantBinding.bind(view);

        repository = MusicLibraryRepository.getInstance();
        playerManager = MusicPlayerManager.getInstance(requireContext());
        aiService = new SiliconFlowAiService();
        promptBuilder = new AiPromptBuilder(requireContext(), repository, playerManager);
        adapter = new AiChatAdapter();

        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerChat.setAdapter(adapter);

        binding.buttonQuickPlaylist.setOnClickListener(v -> sendPrompt(getString(R.string.ai_prompt_playlist)));
        binding.buttonQuickExtend.setOnClickListener(v -> sendPrompt(getString(R.string.ai_prompt_extend)));
        binding.buttonQuickStyle.setOnClickListener(v -> sendPrompt(getString(R.string.ai_prompt_style)));
        binding.buttonQuickScene.setOnClickListener(v -> sendPrompt(getString(R.string.ai_prompt_scene)));
        binding.buttonSend.setOnClickListener(v -> sendInputMessage());
        binding.inputMessage.setOnEditorActionListener(this::onEditorAction);

        if (messages.isEmpty()) {
            messages.add(new AiChatMessage(AiChatMessage.ROLE_ASSISTANT, getString(R.string.ai_welcome_message)));
        }

        bindHeader();
        renderMessages();
    }

    @Override
    public void onStart() {
        super.onStart();
        repository.addListener(this);
        playerManager.addListener(this);
        bindHeader();
    }

    @Override
    public void onStop() {
        super.onStop();
        repository.removeListener(this);
        playerManager.removeListener(this);
    }

    @Override
    public void onMusicLibraryChanged() {
        if (isAdded()) {
            bindHeader();
        }
    }

    @Override
    public void onPlayerStateChanged() {
        if (isAdded()) {
            bindHeader();
        }
    }

    private void bindHeader() {
        binding.textModelInfo.setText(getString(R.string.ai_model_info, aiService.getModelName()));
        binding.textSongCount.setText(getString(R.string.ai_context_song_count, repository.getSongCount()));

        if (aiService.isConfigured()) {
            binding.textStatus.setText(R.string.ai_status_ready);
        } else {
            binding.textStatus.setText(R.string.ai_status_missing_key);
        }

        if (repository.getSongCount() > 0) {
            binding.textContextDesc.setText(R.string.ai_context_desc_ready);
        } else {
            binding.textContextDesc.setText(R.string.ai_context_desc_empty);
        }
    }

    private boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            sendInputMessage();
            return true;
        }
        return false;
    }

    private void sendInputMessage() {
        String input = binding.inputMessage.getText() == null
                ? ""
                : binding.inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(requireContext(), R.string.ai_error_empty_input, Toast.LENGTH_SHORT).show();
            return;
        }
        binding.inputMessage.setText("");
        sendPrompt(input);
    }

    private void sendPrompt(String prompt) {
        if (!aiService.isConfigured()) {
            Toast.makeText(requireContext(), R.string.ai_error_missing_key, Toast.LENGTH_SHORT).show();
            return;
        }

        List<AiChatMessage> requestHistory = new ArrayList<>(messages);
        AiChatMessage userMessage = new AiChatMessage(AiChatMessage.ROLE_USER, prompt);
        AiChatMessage pendingMessage = new AiChatMessage(AiChatMessage.ROLE_ASSISTANT, getString(R.string.ai_pending_message));
        pendingMessage.setPending(true);

        messages.add(userMessage);
        messages.add(pendingMessage);
        renderMessages();
        setInputEnabled(false);

        aiService.requestReply(
                promptBuilder.buildSystemPrompt(),
                promptBuilder.buildUserPrompt(prompt),
                requestHistory,
                new SiliconFlowAiService.AiCallback() {
                    @Override
                    public void onSuccess(String message) {
                        if (!isAdded()) {
                            return;
                        }
                        requireActivity().runOnUiThread(() -> {
                            pendingMessage.setPending(false);
                            pendingMessage.setContent(message);
                            renderMessages();
                            setInputEnabled(true);
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (!isAdded()) {
                            return;
                        }
                        requireActivity().runOnUiThread(() -> {
                            pendingMessage.setPending(false);
                            if ("missing_key".equals(errorMessage)) {
                                pendingMessage.setContent(getString(R.string.ai_error_missing_key));
                            } else if ("parse".equals(errorMessage)) {
                                pendingMessage.setContent(getString(R.string.ai_error_parse));
                            } else {
                                pendingMessage.setContent(getString(R.string.ai_error_request));
                            }
                            renderMessages();
                            setInputEnabled(true);
                        });
                    }
                });
    }

    private void renderMessages() {
        adapter.submitList(new ArrayList<>(messages));
        boolean empty = messages.isEmpty();
        binding.layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerChat.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.recyclerChat.post(() -> {
            if (adapter.getItemCount() > 0) {
                binding.recyclerChat.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void setInputEnabled(boolean enabled) {
        binding.buttonSend.setEnabled(enabled);
        binding.inputMessage.setEnabled(enabled);
        binding.buttonQuickPlaylist.setEnabled(enabled);
        binding.buttonQuickExtend.setEnabled(enabled);
        binding.buttonQuickStyle.setEnabled(enabled);
        binding.buttonQuickScene.setEnabled(enabled);
    }
}
