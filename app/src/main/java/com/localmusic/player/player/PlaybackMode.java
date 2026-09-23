package com.localmusic.player.player;

import androidx.annotation.StringRes;

import com.localmusic.player.R;

public enum PlaybackMode {
    SEQUENCE(R.string.playback_mode_sequence),
    LIST_LOOP(R.string.playback_mode_list_loop),
    SINGLE_LOOP(R.string.playback_mode_single_loop),
    SHUFFLE(R.string.playback_mode_shuffle);

    private final int titleResId;

    PlaybackMode(@StringRes int titleResId) {
        this.titleResId = titleResId;
    }

    @StringRes
    public int getTitleResId() {
        return titleResId;
    }

    public PlaybackMode next() {
        PlaybackMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }
}
