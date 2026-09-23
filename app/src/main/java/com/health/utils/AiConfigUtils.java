package com.Health.utils;

import android.text.TextUtils;

import com.Health.BuildConfig;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AiConfigUtils {

    private static final String DEFAULT_BASE_URL = "https://api.siliconflow.cn/v1";
    private static final String DEFAULT_MODEL = "tencent/Hunyuan-MT-7B";

    private AiConfigUtils() {
    }

    public static String getBaseUrl() {
        String value = trimToEmpty(BuildConfig.SILICONFLOW_BASE_URL);
        return TextUtils.isEmpty(value) ? DEFAULT_BASE_URL : value;
    }

    public static String getModel() {
        String value = trimToEmpty(BuildConfig.SILICONFLOW_MODEL);
        return TextUtils.isEmpty(value) ? DEFAULT_MODEL : value;
    }

    public static String getChatCompletionsUrl() {
        return getBaseUrl() + "/chat/completions";
    }

    public static List<String> getApiKeys() {
        Set<String> keys = new LinkedHashSet<>();
        addKey(keys, BuildConfig.SILICONFLOW_API_KEY_PRIMARY);
        addKey(keys, BuildConfig.SILICONFLOW_API_KEY_SECONDARY);
        return new ArrayList<>(keys);
    }

    public static String maskApiKey(String apiKey) {
        String value = trimToEmpty(apiKey);
        if (TextUtils.isEmpty(value)) {
            return "(empty)";
        }
        if (value.length() <= 10) {
            return value;
        }
        return value.substring(0, 6) + "..." + value.substring(value.length() - 4);
    }

    private static void addKey(Set<String> keys, String apiKey) {
        String value = trimToEmpty(apiKey);
        if (!TextUtils.isEmpty(value)) {
            keys.add(value);
        }
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
