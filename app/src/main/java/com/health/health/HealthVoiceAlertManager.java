package com.Health.health;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class HealthVoiceAlertManager implements TextToSpeech.OnInitListener {

    private static HealthVoiceAlertManager instance;

    private final Context appContext;
    private TextToSpeech textToSpeech;
    private boolean ready;
    private String pendingText;

    private HealthVoiceAlertManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.textToSpeech = new TextToSpeech(appContext, this);
    }

    public static synchronized HealthVoiceAlertManager getInstance(Context context) {
        if (instance == null) {
            instance = new HealthVoiceAlertManager(context);
        }
        return instance;
    }

    public synchronized void speak(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        if (!ready) {
            pendingText = text;
            return;
        }
        textToSpeech.stop();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "health-alert");
    }

    @Override
    public synchronized void onInit(int status) {
        ready = status == TextToSpeech.SUCCESS;
        if (!ready) {
            return;
        }
        textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
        textToSpeech.setSpeechRate(1.0f);
        if (pendingText != null) {
            String text = pendingText;
            pendingText = null;
            speak(text);
        }
    }
}
