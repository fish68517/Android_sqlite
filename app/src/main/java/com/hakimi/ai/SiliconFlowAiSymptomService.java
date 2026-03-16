package com.hakimi.ai;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hakimi.utils.AiConfigUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SiliconFlowAiSymptomService implements AiSymptomService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public String askSymptom(String symptomDescription) {
        String endpoint = AiConfigUtils.SILICONFLOW_BASE_URL + "/chat/completions";
        String body = buildRequestBody(symptomDescription);

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("Authorization", "Bearer " + AiConfigUtils.SILICONFLOW_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "AI接口调用失败：HTTP " + response.code();
            }
            if (response.body() == null) {
                return "AI接口调用失败：响应为空";
            }
            String resp = response.body().string();
            // 打印响应
            System.out.println("AI Response: " + resp);
            String answer = parseContent(resp);
            if (TextUtils.isEmpty(answer)) {
                return "AI接口调用失败：未返回可用内容";
            }
            return answer;
        } catch (IOException e) {
            return "AI接口调用失败：" + e.getMessage();
        }
    }

    private String buildRequestBody(String symptomDescription) {
        JsonObject root = new JsonObject();
        root.addProperty("model", AiConfigUtils.SILICONFLOW_MODEL);
        root.addProperty("temperature", 0.3);

        JsonArray messages = new JsonArray();
        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content",
                "你是中文健康问答助手。回答格式必须包含：可能病因、应对措施、建议科室。"
                        + "必须加上风险提示：若症状加重或持续，请尽快线下就医。");
        messages.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", "用户症状描述：" + symptomDescription);
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
        if (choices.size() == 0) {
            return null;
        }
        JsonObject first = choices.get(0).getAsJsonObject();
        if (!first.has("message")) {
            return null;
        }
        JsonObject message = first.getAsJsonObject("message");
        if (!message.has("content")) {
            return null;
        }
        return message.get("content").getAsString();
    }
}
