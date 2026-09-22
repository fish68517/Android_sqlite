package com.example.knowledgelabs.ch11;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.knowledgelabs.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NetworkJsonClientActivity extends Activity {
    private final List<String> results = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView resultList;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_json);
        resultList = findViewById(R.id.listNetworkResult);
        results.add("点击“请求 JSON”加载网络数据列表");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        resultList.setAdapter(adapter);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        findViewById(R.id.btnFetchJson).setOnClickListener(v -> fetchJson());
        findViewById(R.id.btnOpenWeb).setOnClickListener(v -> {
            resultList.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("https://www.example.com/");
        });
    }

    private void fetchJson() {
        webView.setVisibility(View.GONE);
        resultList.setVisibility(View.VISIBLE);
        replaceResults("正在请求 HTTPS JSON…");
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(
                        "https://jsonplaceholder.typicode.com/posts?_limit=8").openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IllegalStateException("HTTP " + responseCode);
                }
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) body.append(line);
                }
                JSONArray array = new JSONArray(body.toString());
                List<String> loaded = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    loaded.add("#" + item.getInt("id") + "  " + item.getString("title"));
                }
                runOnUiThread(() -> {
                    results.clear();
                    results.addAll(loaded);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception error) {
                runOnUiThread(() -> replaceResults("请求失败：" + error.getMessage()));
            } finally {
                if (connection != null) connection.disconnect();
            }
        }, "json-client").start();
    }

    private void replaceResults(String message) {
        results.clear();
        results.add(message);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        webView.stopLoading();
        webView.destroy();
        super.onDestroy();
    }
}
