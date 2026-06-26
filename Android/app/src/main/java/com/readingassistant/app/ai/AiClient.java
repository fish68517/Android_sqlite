package com.readingassistant.app.ai;

import com.readingassistant.app.BuildConfig;
import com.readingassistant.app.util.DebugLog;

public class AiClient {
    private final GlmClient glmClient = new GlmClient();
    private final KimiClient kimiClient = new KimiClient();

    public void requestTextSummary(final String pageText, final String fallbackPreview, final AiCallback callback) {
        DebugLog.d("AiClient text summary requested textLength=" + (pageText == null ? 0 : pageText.length())
                + " fallbackPreview=" + DebugLog.preview(fallbackPreview));
        glmClient.requestTextSummary(pageText, new AiCallback() {
            @Override
            public void onSuccess(String summary) {
                String normalized = SummaryParser.normalize(summary, fallbackPreview, BuildConfig.AI_SUMMARY_MAX_CHARS);
                DebugLog.d("AiClient GLM text success rawLength=" + (summary == null ? 0 : summary.length())
                        + " normalized=" + DebugLog.preview(normalized));
                callback.onSuccess(normalized);
            }

            @Override
            public void onFailure(String message) {
                DebugLog.w("AiClient GLM text failure message=" + message
                        + " kimiEnabled=" + kimiClient.isEnabled());
                if (kimiClient.isEnabled()) {
                    kimiClient.requestTextSummary(pageText, new AiCallback() {
                        @Override
                        public void onSuccess(String summary) {
                            String normalized = SummaryParser.normalize(summary, fallbackPreview, BuildConfig.AI_SUMMARY_MAX_CHARS);
                            DebugLog.d("AiClient Kimi text success rawLength=" + (summary == null ? 0 : summary.length())
                                    + " normalized=" + DebugLog.preview(normalized));
                            callback.onSuccess(normalized);
                        }

                        @Override
                        public void onFailure(String fallbackMessage) {
                            DebugLog.w("AiClient Kimi text failure message=" + fallbackMessage);
                            callback.onFailure(fallbackMessage);
                        }
                    });
                } else {
                    callback.onFailure(message);
                }
            }
        });
    }

    public void requestImageSummary(String base64Jpeg, final String fallbackPreview, final AiCallback callback) {
        DebugLog.d("AiClient image summary requested base64Length=" + (base64Jpeg == null ? 0 : base64Jpeg.length())
                + " fallbackPreview=" + DebugLog.preview(fallbackPreview));
        glmClient.requestImageSummary(base64Jpeg, new AiCallback() {
            @Override
            public void onSuccess(String summary) {
                String normalized = SummaryParser.normalize(summary, fallbackPreview, BuildConfig.AI_SUMMARY_MAX_CHARS);
                DebugLog.d("AiClient GLM image success rawLength=" + (summary == null ? 0 : summary.length())
                        + " normalized=" + DebugLog.preview(normalized));
                callback.onSuccess(normalized);
            }

            @Override
            public void onFailure(String message) {
                DebugLog.w("AiClient GLM image failure message=" + message);
                callback.onFailure(message);
            }
        });
    }

    public void cancelRunning() {
        DebugLog.d("AiClient cancel running calls");
        glmClient.cancelRunning();
        kimiClient.cancelRunning();
    }
}
