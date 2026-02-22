package com.example.healthdietapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.HealthRecordDAO;
import com.example.healthdietapp.models.HealthRecord;
import com.example.healthdietapp.utils.SessionManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class HealthChartActivity extends AppCompatActivity {

    private LineChart weightLineChart;
    private BarChart waterBarChart;
    private TextView toolbarTitle;
    private Button backButton;

    private DatabaseHelper dbHelper;
    private HealthRecordDAO healthRecordDAO;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_chart);

        initializeViews();
        initializeDatabase();
        loadChartData();
    }

    private void initializeViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        backButton = findViewById(R.id.backButton);
        weightLineChart = findViewById(R.id.weightLineChart);
        waterBarChart = findViewById(R.id.waterBarChart);

        if (toolbarTitle != null) {
            toolbarTitle.setText("健康数据分析");
        }
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        healthRecordDAO = new HealthRecordDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void loadChartData() {
        new Thread(() -> {
            try {
                // 获取所有历史记录 (已按日期排序)
                List<HealthRecord> records = healthRecordDAO.getAllHealthRecordsForUser(userId);
                runOnUiThread(() -> {
                    if (records != null && !records.isEmpty()) {
                        setupWeightChart(records);
                        setupWaterChart(records);
                    } else {
                        Toast.makeText(this, "暂无足够的健康数据", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 设置体重折线图
     */
    private void setupWeightChart(List<HealthRecord> records) {
        List<Entry> weightEntries = new ArrayList<>();
        final List<String> dateLabels = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            HealthRecord record = records.get(i);
            // 过滤无效数据 (0的表示未记录体重)
            if (record.getWeight() > 0) {
                weightEntries.add(new Entry(i, record.getWeight()));
            }
            // 将 "2026-02-15" 截取为 "02-15" 作为 X 轴标签
            String shortDate = record.getDate().length() >= 10 ? record.getDate().substring(5) : record.getDate();
            dateLabels.add(shortDate);
        }

        LineDataSet dataSet = new LineDataSet(weightEntries, "体重变化 (kg)");
        dataSet.setColor(Color.parseColor("#4CAF50")); // 主题绿色
        dataSet.setCircleColor(Color.parseColor("#388E3C"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // 平滑曲线

        LineData lineData = new LineData(dataSet);
        weightLineChart.setData(lineData);

        // 设置 X 轴为底部并绑定日期字符串
        XAxis xAxis = weightLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        xAxis.setGranularity(1f); // 防止标签重叠
        xAxis.setDrawGridLines(false);

        // UI 优化
        weightLineChart.getAxisRight().setEnabled(false); // 隐藏右侧 Y 轴
        Description desc = new Description();
        desc.setText("");
        weightLineChart.setDescription(desc);
        weightLineChart.animateX(1000); // 添加横向动画
        weightLineChart.invalidate();
    }

    /**
     * 设置饮水量柱状图
     */
    private void setupWaterChart(List<HealthRecord> records) {
        List<BarEntry> waterEntries = new ArrayList<>();
        final List<String> dateLabels = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            HealthRecord record = records.get(i);
            waterEntries.add(new BarEntry(i, record.getWaterIntake()));

            String shortDate = record.getDate().length() >= 10 ? record.getDate().substring(5) : record.getDate();
            dateLabels.add(shortDate);
        }

        BarDataSet dataSet = new BarDataSet(waterEntries, "每日饮水量 (L)");
        dataSet.setColor(Color.parseColor("#03A9F4")); // 清水蓝
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        waterBarChart.setData(barData);

        // 设置 X 轴
        XAxis xAxis = waterBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // UI 优化
        waterBarChart.getAxisRight().setEnabled(false);
        Description desc = new Description();
        desc.setText("");
        waterBarChart.setDescription(desc);
        waterBarChart.animateY(1000); // 添加纵向动画
        waterBarChart.invalidate();
    }
}