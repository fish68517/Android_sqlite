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

class GlmClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private volatile Call runningCall;

    GlmClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(BuildConfig.AI_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(BuildConfig.AI_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(BuildConfig.AI_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    void requestTextSummary(String pageText, AiCallback callback) {
        if (BuildConfig.GLM_API_KEY.trim().isEmpty()) {
            DebugLog.w("GLM text request skipped: missing API key");
            callback.onFailure("未配置 GLM API Key");
            return;
        }
        try {
            DebugLog.d("GLM text request build model=" + BuildConfig.GLM_TEXT_MODEL
                    + " textLength=" + (pageText == null ? 0 : pageText.length()));
            JSONObject body = baseBody(BuildConfig.GLM_TEXT_MODEL);
            JSONArray messages = new JSONArray();
            messages.put(message("system", PromptFactory.systemPrompt()));
            messages.put(message("user", PromptFactory.textUserPrompt(pageText)));
            body.put("messages", messages);
            if (BuildConfig.GLM_TEXT_MODEL.startsWith("glm-5")) {
                JSONObject thinking = new JSONObject();
                thinking.put("type", "enabled");
                body.put("thinking", thinking);
            }
            execute(body, callback);
        } catch (JSONException e) {
            DebugLog.e("GLM text request build failed", e);
            callback.onFailure("GLM 请求构造失败");
        }
    }

    void requestImageSummary(String base64Jpeg, AiCallback callback) {
        if (BuildConfig.GLM_API_KEY.trim().isEmpty()) {
            DebugLog.w("GLM image request skipped: missing API key");
            callback.onFailure("未配置 GLM API Key");
            return;
        }
        try {
            DebugLog.d("GLM image request build model=" + BuildConfig.GLM_VISION_MODEL
                    + " base64Length=" + (base64Jpeg == null ? 0 : base64Jpeg.length()));
            JSONObject body = baseBody(BuildConfig.GLM_VISION_MODEL);
            JSONArray messages = new JSONArray();
            messages.put(message("system", PromptFactory.systemPrompt()));

            JSONArray content = new JSONArray();
            JSONObject textPart = new JSONObject();
            textPart.put("type", "text");
            textPart.put("text", PromptFactory.imageUserPrompt());
            content.put(textPart);

            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/jpeg;base64," + base64Jpeg);
            JSONObject imagePart = new JSONObject();
            imagePart.put("type", "image_url");
            imagePart.put("image_url", imageUrl);
            content.put(imagePart);

            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", content);
            messages.put(user);
            body.put("messages", messages);
            execute(body, callback);
        } catch (JSONException e) {
            DebugLog.e("GLM image request build failed", e);
            callback.onFailure("GLM 视觉请求构造失败");
        }
    }

    void cancelRunning() {
        Call call = runningCall;
        if (call != null) {
            DebugLog.d("GLM cancel running call");
            call.cancel();
        }
    }

    private JSONObject baseBody(String model) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("temperature", 0.2);
        body.put("max_tokens", 120);
        return body;
    }

    private JSONObject message(String role, String content) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private void execute(JSONObject body, final AiCallback callback) {
        String baseUrl = trimTrailingSlash(BuildConfig.GLM_BASE_URL);
        String model = body.optString("model", "");
        DebugLog.d("GLM HTTP request start model=" + model + " url=" + baseUrl + "/chat/completions");
        Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + BuildConfig.GLM_API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON))
                .build();
        runningCall = httpClient.newCall(request);
        runningCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    DebugLog.e("GLM HTTP request failed model=" + model, e);
                    callback.onFailure(e.getMessage() == null ? "GLM 请求失败" : e.getMessage());
                } else {
                    DebugLog.d("GLM HTTP request canceled model=" + model);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body() == null ? "" : response.body().string();
                DebugLog.d("GLM HTTP response model=" + model
                        + " code=" + response.code()
                        + " bodyLength=" + responseText.length()
                        + " bodyPreview=" + DebugLog.preview(responseText));
                if (!response.isSuccessful()) {
                    callback.onFailure("GLM 返回错误：" + response.code());
                    return;
                }
                try {
                    callback.onSuccess(parseContent(responseText));
                } catch (JSONException e) {
                    DebugLog.e("GLM response parse failed model=" + model, e);
                    callback.onFailure("GLM 返回内容解析失败");
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
        if (content instanceof JSONArray) {
            JSONArray contentArray = (JSONArray) content;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject item = contentArray.optJSONObject(i);
                if (item == null) {
                    continue;
                }
                String text = item.optString("text", "");
                if (!text.isEmpty()) {
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(text);
                }
            }
            return builder.toString();
        }
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
