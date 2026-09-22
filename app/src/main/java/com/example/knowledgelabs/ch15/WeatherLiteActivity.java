package com.example.knowledgelabs.ch15;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.knowledgelabs.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class WeatherLiteActivity extends Activity {
    private SwipeRefreshLayout swipeRefresh;
    private EditText cityInput;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private ImageView weatherIcon;
    private TextView nowView;
    private TextView forecastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_lite);
        swipeRefresh = findViewById(R.id.swipeWeather);
        cityInput = findViewById(R.id.etWeatherCity);
        latitudeInput = findViewById(R.id.etWeatherLat);
        longitudeInput = findViewById(R.id.etWeatherLng);
        weatherIcon = findViewById(R.id.ivWeather);
        nowView = findViewById(R.id.tvWeatherNow);
        forecastView = findViewById(R.id.tvWeatherForecast);
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
        swipeRefresh.setOnRefreshListener(this::loadWeather);
        findViewById(R.id.btnRefreshWeather).setOnClickListener(v -> loadWeather());
        loadWeather();
    }

    private void loadWeather() {
        String latitudeText = latitudeInput.getText().toString().trim();
        String longitudeText = longitudeInput.getText().toString().trim();
        if (TextUtils.isEmpty(latitudeText) || TextUtils.isEmpty(longitudeText)) {
            nowView.setText("请输入经纬度");
            swipeRefresh.setRefreshing(false);
            return;
        }
        final double latitude;
        final double longitude;
        try {
            latitude = Double.parseDouble(latitudeText);
            longitude = Double.parseDouble(longitudeText);
        } catch (NumberFormatException error) {
            nowView.setText("经纬度格式不正确");
            swipeRefresh.setRefreshing(false);
            return;
        }
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            nowView.setText("纬度应为 -90~90，经度应为 -180~180");
            swipeRefresh.setRefreshing(false);
            return;
        }
        swipeRefresh.setRefreshing(true);
        nowView.setText("正在获取天气…");
        forecastView.setText("");
        new Thread(() -> requestWeather(latitude, longitude), "weather-client").start();
    }

    private void requestWeather(double latitude, double longitude) {
        HttpURLConnection connection = null;
        try {
            String endpoint = String.format(Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f"
                            + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m"
                            + "&daily=weather_code,temperature_2m_max,temperature_2m_min"
                            + "&timezone=auto&forecast_days=3",
                    latitude, longitude);
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException("HTTP " + connection.getResponseCode());
            }
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
            }
            WeatherResult result = parseWeather(new JSONObject(body.toString()));
            runOnUiThread(() -> renderWeather(result));
        } catch (Exception error) {
            runOnUiThread(() -> {
                swipeRefresh.setRefreshing(false);
                nowView.setText("天气获取失败：" + error.getMessage());
            });
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private WeatherResult parseWeather(JSONObject root) throws Exception {
        JSONObject current = root.getJSONObject("current");
        int currentCode = current.getInt("weather_code");
        String now = String.format(Locale.CHINA,
                "%s  %.1f℃\n体感 %.1f℃ · 湿度 %d%% · 风速 %.1f km/h",
                weatherDescription(currentCode),
                current.getDouble("temperature_2m"),
                current.getDouble("apparent_temperature"),
                current.getInt("relative_humidity_2m"),
                current.getDouble("wind_speed_10m"));

        JSONObject daily = root.getJSONObject("daily");
        JSONArray dates = daily.getJSONArray("time");
        JSONArray codes = daily.getJSONArray("weather_code");
        JSONArray highs = daily.getJSONArray("temperature_2m_max");
        JSONArray lows = daily.getJSONArray("temperature_2m_min");
        StringBuilder forecast = new StringBuilder("未来三日\n");
        for (int i = 0; i < dates.length(); i++) {
            forecast.append(String.format(Locale.CHINA, "%s  %-6s  %.1f℃ / %.1f℃",
                    dates.getString(i), weatherDescription(codes.getInt(i)),
                    highs.getDouble(i), lows.getDouble(i)));
            if (i < dates.length() - 1) forecast.append('\n');
        }
        return new WeatherResult(now, forecast.toString(), currentCode);
    }

    private void renderWeather(WeatherResult result) {
        swipeRefresh.setRefreshing(false);
        String city = cityInput.getText().toString().trim();
        nowView.setText((city.isEmpty() ? "当前位置" : city) + "\n" + result.now);
        forecastView.setText(result.forecast);
        weatherIcon.setImageResource(iconForWeather(result.code));
    }

    private static String weatherDescription(int code) {
        if (code == 0) return "晴";
        if (code <= 3) return "多云";
        if (code == 45 || code == 48) return "有雾";
        if (code >= 51 && code <= 67) return "有雨";
        if (code >= 71 && code <= 77) return "有雪";
        if (code >= 80 && code <= 82) return "阵雨";
        if (code >= 85 && code <= 86) return "阵雪";
        if (code >= 95) return "雷暴";
        return "未知";
    }

    private static int iconForWeather(int code) {
        if (code == 0) return R.drawable.ic_weather_sun;
        if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82) || code >= 95) {
            return R.drawable.ic_weather_rain;
        }
        return R.drawable.ic_weather_cloud;
    }

    private static final class WeatherResult {
        private final String now;
        private final String forecast;
        private final int code;

        private WeatherResult(String now, String forecast, int code) {
            this.now = now;
            this.forecast = forecast;
            this.code = code;
        }
    }
}
