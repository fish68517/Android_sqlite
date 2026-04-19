package com.localmusic.player.ai;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.localmusic.player.BuildConfig;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SiliconFlowAiService {

    public interface AiCallback {
        void onSuccess(String message);

        void onError(String errorMessage);
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public boolean isConfigured() {
        return BuildConfig.SILICONFLOW_API_KEY != null
                && !BuildConfig.SILICONFLOW_API_KEY.trim().isEmpty();
    }

    public String getModelName() {
        return BuildConfig.SILICONFLOW_MODEL;
    }

    public void requestReply(String systemPrompt,
                             String userPrompt,
                             List<AiChatMessage> history,
                             AiCallback callback) {
        if (!isConfigured()) {
            callback.onError("missing_key");
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("model", BuildConfig.SILICONFLOW_MODEL);
        payload.addProperty("stream", false);
        payload.addProperty("temperature", 0.7);
        payload.addProperty("max_tokens", 700);

        JsonArray messages = new JsonArray();
        messages.add(buildMessage("system", systemPrompt));

        int start = Math.max(history.size() - 6, 0);
        for (int i = start; i < history.size(); i++) {
            AiChatMessage message = history.get(i);
            if (message.isPending()) {
                continue;
            }
            messages.add(buildMessage(message.getRole(), message.getContent()));
        }

        messages.add(buildMessage("user", userPrompt));
        payload.add("messages", messages);

        Request request = new Request.Builder()
                .url(trimBaseUrl(BuildConfig.SILICONFLOW_BASE_URL) + "/chat/completions")
                .addHeader("Authorization", "Bearer " + BuildConfig.SILICONFLOW_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(payload), JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("network");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful() || body == null) {
                        callback.onError("network");
                        return;
                    }

                    String bodyString = body.string();
                    JsonObject root = JsonParser.parseString(bodyString).getAsJsonObject();
                    JsonArray choices = root.getAsJsonArray("choices");
                    if (choices == null || choices.size() == 0) {
                        callback.onError("parse");
                        return;
                    }

                    JsonObject choice = choices.get(0).getAsJsonObject();
                    JsonObject message = choice.getAsJsonObject("message");
                    if (message == null || !message.has("content")) {
                        callback.onError("parse");
                        return;
                    }

                    callback.onSuccess(message.get("content").getAsString().trim());
                } catch (Exception exception) {
                    callback.onError("parse");
                }
            }
        });
    }

    private JsonObject buildMessage(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

    private String trimBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
