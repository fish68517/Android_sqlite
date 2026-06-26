package com.readingassistant.app.ai;

import com.readingassistant.app.BuildConfig;
import com.readingassistant.app.util.DebugLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class KimiClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private volatile Call runningCall;

    KimiClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(BuildConfig.AI_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(BuildConfig.AI_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(BuildConfig.AI_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    boolean isEnabled() {
        return !BuildConfig.KIMI_API_KEY.trim().isEmpty() && !BuildConfig.KIMI_MODEL.trim().isEmpty();
    }

    void requestTextSummary(String pageText, AiCallback callback) {
        if (!isEnabled()) {
            DebugLog.w("Kimi text request skipped: disabled");
            callback.onFailure("未启用 Kimi 降级");
            return;
        }
        try {
            DebugLog.d("Kimi text request build model=" + BuildConfig.KIMI_MODEL
                    + " textLength=" + (pageText == null ? 0 : pageText.length()));
            JSONObject body = new JSONObject();
            body.put("model", BuildConfig.KIMI_MODEL);
            body.put("temperature", 0.2);
            body.put("max_tokens", 120);
            JSONArray messages = new JSONArray();
            messages.put(message("system", PromptFactory.systemPrompt()));
            messages.put(message("user", PromptFactory.textUserPrompt(pageText)));
            body.put("messages", messages);
            execute(body, callback);
        } catch (JSONException e) {
            DebugLog.e("Kimi text request build failed", e);
            callback.onFailure("Kimi 请求构造失败");
        }
    }

    void cancelRunning() {
        Call call = runningCall;
        if (call != null) {
            DebugLog.d("Kimi cancel running call");
            call.cancel();
        }
    }

    private JSONObject message(String role, String content) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private void execute(JSONObject body, final AiCallback callback) {
        String baseUrl = trimTrailingSlash(BuildConfig.KIMI_BASE_URL);
        String model = body.optString("model", "");
        DebugLog.d("Kimi HTTP request start model=" + model + " url=" + baseUrl + "/chat/completions");
        Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + BuildConfig.KIMI_API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON))
                .build();
        runningCall = httpClient.newCall(request);
        runningCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    DebugLog.e("Kimi HTTP request failed model=" + model, e);
                    callback.onFailure(e.getMessage() == null ? "Kimi 请求失败" : e.getMessage());
                } else {
                    DebugLog.d("Kimi HTTP request canceled model=" + model);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body() == null ? "" : response.body().string();
                DebugLog.d("Kimi HTTP response model=" + model
                        + " code=" + response.code()
                        + " bodyLength=" + responseText.length()
                        + " bodyPreview=" + DebugLog.preview(responseText));
                if (!response.isSuccessful()) {
                    callback.onFailure("Kimi 返回错误：" + response.code());
                    return;
                }
                try {
                    callback.onSuccess(parseContent(responseText));
                } catch (JSONException e) {
                    DebugLog.e("Kimi response parse failed model=" + model, e);
                    callback.onFailure("Kimi 返回内容解析失败");
                }
            }
        });
    }

    private String parseContent(String responseText) throws JSONException {
        JSONObject root = new JSONObject(responseText);
        JSONArray choices = root.getJSONArray("choices");
        if (choices.length() == 0) {
            return "";
        }
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        Object content = message.get("content");
        return content == null ? "" : String.valueOf(content);
    }

    private String trimTrailingSlash(String value) {
        String result = value == null ? "" : value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
