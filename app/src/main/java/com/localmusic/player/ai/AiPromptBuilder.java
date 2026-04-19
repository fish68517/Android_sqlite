package com.localmusic.player.ai;

import android.content.Context;

import com.localmusic.player.R;
import com.localmusic.player.model.Song;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.MusicLibraryRepository;

public class AiPromptBuilder {

    private final Context context;
    private final MusicLibraryRepository repository;
    private final MusicPlayerManager playerManager;

    public AiPromptBuilder(Context context,
                           MusicLibraryRepository repository,
                           MusicPlayerManager playerManager) {
        this.context = context.getApplicationContext();
        this.repository = repository;
        this.playerManager = playerManager;
    }

    public String buildSystemPrompt() {
        return "你是本地音乐播放器里的 AI 音乐助手。"
                + "你只能使用简体中文回答。"
                + "回答重点应围绕本地曲库分析、听歌建议、风格总结、播放功能建议。"
                + "如果上下文信息不足，就明确说明信息不足，不要编造用户本地不存在的具体歌曲。"
                + "如果用户询问推荐，请优先结合本地曲库内容给出建议，再补充通用方向。"
                + "输出要清晰、实用、直接。";
    }

    public String buildUserPrompt(String userMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("以下是当前播放器上下文：\n");
        builder.append(repository.buildLibrarySummary()).append('\n');

        Song currentSong = playerManager.getCurrentSong();
        if (currentSong != null) {
            builder.append(context.getString(
                    R.string.ai_current_song_template,
                    currentSong.getTitle(),
                    currentSong.getArtist(),
                    currentSong.getAlbum()));
        } else {
            builder.append(context.getString(R.string.ai_no_current_song));
        }

        builder.append("\n\n用户问题：\n").append(userMessage);
        return builder.toString();
    }
}
