package com.Health.ai;

import android.text.TextUtils;

import com.Health.utils.AiConfigUtils;
import com.Health.utils.HealthDebugLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SiliconFlowAiSymptomService implements AiSymptomService {

    private static final String TAG = "SiliconFlowAi";
    private static final int RESPONSE_PREVIEW_LIMIT = 1200;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static volatile String preferredApiKey;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(75, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    @Override
    public String askSymptom(String symptomDescription) {
        String prompt = symptomDescription == null ? "" : symptomDescription.trim();
        List<String> configuredKeys = AiConfigUtils.getApiKeys();
        List<String> apiKeys = orderApiKeys(configuredKeys);
        String endpoint = AiConfigUtils.getChatCompletionsUrl();
        String requestBody = buildRequestBody(prompt);

        if (apiKeys.isEmpty()) {
            HealthDebugLogger.e(TAG, "No SiliconFlow API key configured.");
            return "No SiliconFlow API key configured.";
        }

        HealthDebugLogger.i(TAG, "Request start. endpoint=" + endpoint
                + ", model=" + AiConfigUtils.getModel()
                + ", promptLength=" + prompt.length()
                + ", apiKeyCount=" + apiKeys.size()
                + ", requestPreview=" + abbreviate(requestBody));

        String lastError = "AI request failed.";
        for (int i = 0; i < apiKeys.size(); i++) {
            String apiKey = apiKeys.get(i);
            String maskedKey = AiConfigUtils.maskApiKey(apiKey);
            long startedAt = System.currentTimeMillis();

            HealthDebugLogger.i(TAG, "Attempt " + (i + 1) + "/" + apiKeys.size()
                    + " using key=" + maskedKey);

            Request request = new Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                long durationMs = System.currentTimeMillis() - startedAt;
                String responseText = response.body() == null ? "" : response.body().string();

                HealthDebugLogger.i(TAG, "Attempt finished. key=" + maskedKey
                        + ", httpCode=" + response.code()
                        + ", durationMs=" + durationMs
                        + ", responsePreview=" + abbreviate(responseText));

                if (!response.isSuccessful()) {
                    lastError = "AI request failed. HTTP " + response.code();
                    continue;
                }

                if (TextUtils.isEmpty(responseText)) {
                    lastError = "AI returned an empty response.";
                    continue;
                }

                String answer = parseContent(responseText);
                if (TextUtils.isEmpty(answer)) {
                    lastError = "AI response did not contain message content.";
                    continue;
                }

                preferredApiKey = apiKey;
                HealthDebugLogger.i(TAG, "Request success. key=" + maskedKey
                        + ", durationMs=" + durationMs
                        + ", answerLength=" + answer.length());
                return answer;
            } catch (SocketTimeoutException e) {
                long durationMs = System.currentTimeMillis() - startedAt;
                lastError = "AI request timed out after " + durationMs + " ms.";
                HealthDebugLogger.e(TAG, "Timeout with key=" + maskedKey
                        + ", durationMs=" + durationMs, e);
            } catch (IOException e) {
                long durationMs = System.currentTimeMillis() - startedAt;
                lastError = "AI network request failed: " + safeMessage(e);
                HealthDebugLogger.e(TAG, "Network error with key=" + maskedKey
                        + ", durationMs=" + durationMs, e);
            } catch (Exception e) {
                long durationMs = System.currentTimeMillis() - startedAt;
                lastError = "AI response parse failed: " + safeMessage(e);
                HealthDebugLogger.e(TAG, "Unexpected error with key=" + maskedKey
                        + ", durationMs=" + durationMs, e);
            }
        }

        HealthDebugLogger.w(TAG, "All API keys failed. lastError=" + lastError);
        return lastError;
    }

    private List<String> orderApiKeys(List<String> configuredKeys) {
        if (configuredKeys == null || configuredKeys.isEmpty() || TextUtils.isEmpty(preferredApiKey)) {
            return configuredKeys;
        }
        List<String> orderedKeys = new ArrayList<>();
        if (configuredKeys.contains(preferredApiKey)) {
            orderedKeys.add(preferredApiKey);
        }
        for (String key : configuredKeys) {
            if (!TextUtils.equals(preferredApiKey, key)) {
                orderedKeys.add(key);
            }
        }
        return orderedKeys;
    }

    private String buildRequestBody(String symptomDescription) {
        JsonObject root = new JsonObject();
        root.addProperty("model", AiConfigUtils.getModel());
        root.addProperty("temperature", 0.3);

        JsonArray messages = new JsonArray();

        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content",
                "You are a health Q&A assistant. Reply in Simplified Chinese. "
                        + "Use these sections: possible causes, suggestions, recommended department, risk reminder. "
                        + "If symptoms are severe or persistent, tell the user to seek offline medical care.");
        messages.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", "User symptom description: " + symptomDescription);
        messages.add(user);

        root.add("messages", messages);
        return root.toString();
    }

    private String parseContent(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        if (!json.has("choices")) {
            return null;
        }
        JsonArray choices = json.getAsJsonArray("choices");
        if (choices == null || choices.size() == 0) {
            return null;
        }
        JsonObject first = choices.get(0).getAsJsonObject();
        if (first == null || !first.has("message")) {
            return null;
        }
        JsonObject message = first.getAsJsonObject("message");
        if (message == null || !message.has("content")) {
            return null;
        }
        return message.get("content").getAsString();
    }

    private String abbreviate(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        String oneLine = value.replace("\r", "\\r").replace("\n", "\\n");
        if (oneLine.length() <= RESPONSE_PREVIEW_LIMIT) {
            return oneLine;
        }
        return oneLine.substring(0, RESPONSE_PREVIEW_LIMIT) + "...";
    }

    private String safeMessage(Exception e) {
        return e == null || TextUtils.isEmpty(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage();
    }
}
