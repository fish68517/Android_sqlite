package com.example.orderfood.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.model.MerchantBean;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MerchantStatsFragment extends Fragment {
    private LineChart lineChart;
    private DBMysqlHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_stats, container, false);
        initViews(view);
        loadSalesStats();
        return view;
    }

    private void initViews(View view) {
        lineChart = view.findViewById(R.id.lineChart);
        view.findViewById(R.id.btnGenerateData).setOnClickListener(v -> generateMockData());
        setupLineChart();
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setHighlightPerDragEnabled(true);

        // 设置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(45f);

        // 设置Y轴
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawZeroLine(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 设置图例
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        legend.setTextSize(10f);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setWordWrapEnabled(true);

        lineChart.setExtraOffsets(10f, 20f, 30f, 10f);
    }

    private void loadSalesStats() {
        MerchantBean merchant = MyApplication.curMerchant;
        dbHelper.getWeekDishSales(merchant.getMerchantId(), new DBMysqlHelper.DatabaseCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                // 按菜品ID和日期组织数据
                Map<Integer, Map<String, Integer>> dishSalesMap = new HashMap<>();
                Map<Integer, String> dishNames = new HashMap<>();
                List<String> dates = new ArrayList<>();

                // 第一次遍历：收集所有唯一的日期
                for (Map<String, Object> data : result) {
                    String date = (String) data.get("date");
                    if (!dates.contains(date)) {
                        dates.add(date);
                    }
                }

                // 第二次遍历：组织销售数据
                for (Map<String, Object> data : result) {
                    int dishId = (int) data.get("dishId");
                    String date = (String) data.get("date");
                    int quantity = (int) data.get("quantity");
                    String dishName = (String) data.get("dishName");

                    // 保存菜品名称
                    dishNames.put(dishId, dishName);

                    // 保存销售数据
                    dishSalesMap.computeIfAbsent(dishId, k -> new TreeMap<>())
                            .put(date, quantity);
                }

                // 准备折线图数据
                List<LineDataSet> dataSets = new ArrayList<>();
                int colorIndex = 0;

                // 为每个菜品创建一个数据集
                for (Map.Entry<Integer, Map<String, Integer>> entry : dishSalesMap.entrySet()) {
                    int dishId = entry.getKey();
                    Map<String, Integer> salesByDate = entry.getValue();
                    List<Entry> entries = new ArrayList<>();

                    // 添加每天的销量数据
                    for (int i = 0; i < dates.size(); i++) {
                        String date = dates.get(i);
                        float value = salesByDate.getOrDefault(date, 0);
                        entries.add(new Entry(i, value));
                    }

                    // 创建数据集
                    LineDataSet dataSet = new LineDataSet(entries, dishNames.get(dishId));
                    dataSet.setColor(ColorTemplate.MATERIAL_COLORS[colorIndex % ColorTemplate.MATERIAL_COLORS.length]);
                    dataSet.setLineWidth(2f);
                    dataSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[colorIndex % ColorTemplate.MATERIAL_COLORS.length]);
                    dataSet.setCircleRadius(4f);
                    dataSet.setDrawCircleHole(false);
                    dataSet.setValueTextSize(10f);
                    dataSet.setDrawValues(true);
                    dataSet.setMode(LineDataSet.Mode.LINEAR);
                    dataSet.setDrawFilled(true);
                    dataSet.setFillAlpha(50);
                    dataSet.setFillColor(ColorTemplate.MATERIAL_COLORS[colorIndex % ColorTemplate.MATERIAL_COLORS.length]);

                    dataSets.add(dataSet);
                    colorIndex++;
                }

                if (dataSets.isEmpty()) {
                    Toast.makeText(getContext(), "暂无销售数据", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 设置X轴标签
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                xAxis.setLabelCount(dates.size());

                // 创建LineData并设置数据
                LineData lineData = new LineData(dataSets.toArray(new LineDataSet[0]));
                lineChart.setData(lineData);

                // 刷新图表
                lineChart.invalidate();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "获取销售数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMockData() {
        MerchantBean merchant = MyApplication.curMerchant;
        dbHelper.generateMockOrders(merchant.getMerchantId(), new DBMysqlHelper.DatabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    Toast.makeText(getContext(), "生成模拟数据成功", Toast.LENGTH_SHORT).show();
                    loadSalesStats();
                } else {
                    Toast.makeText(getContext(), "生成模拟数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "生成模拟数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 