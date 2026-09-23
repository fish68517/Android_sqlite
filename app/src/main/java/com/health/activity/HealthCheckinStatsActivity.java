package com.Health.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.Health.HealthApplication;
import com.Health.R;
import com.Health.local.LocalHealthRepository;
import com.Health.local.model.HabitStatus;
import com.Health.local.model.HealthCheckinSummary;
import com.Health.local.model.MedicationReminderItem;
import com.Health.local.model.QuickRecordItem;
import com.Health.utils.HealthDebugLogger;
import com.Health.utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthCheckinStatsActivity extends AppCompatActivity {

    private static final String TAG = "HealthCheckinStats";

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    private final DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("MM-dd");

    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;

    private TextView tvSummary;
    private TextView tvQuickRecords;
    private TextView tvHabits;
    private TextView tvReminders;
    private BarChart chartTypeCounts;
    private LineChart chartDailyTrend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_checkin_stats);
        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();

        tvSummary = findViewById(R.id.tv_summary);
        tvQuickRecords = findViewById(R.id.tv_quick_records);
        tvHabits = findViewById(R.id.tv_habits);
        tvReminders = findViewById(R.id.tv_reminders);
        chartTypeCounts = findViewById(R.id.chart_type_counts);
        chartDailyTrend = findViewById(R.id.chart_daily_trend);
        HealthDebugLogger.i(TAG, "onCreate finished. userId=" + getCurrentUserId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderStats();
    }

    private void renderStats() {
        long userId = getCurrentUserId();
        if (userId <= 0) {
            HealthDebugLogger.w(TAG, "renderStats shows empty state because user not logged in");
            tvSummary.setText("\u672a\u767b\u5f55\uff0c\u65e0\u6cd5\u67e5\u770b\u7edf\u8ba1\u3002");
            tvQuickRecords.setText("--");
            tvHabits.setText("--");
            tvReminders.setText("--");
            chartTypeCounts.clear();
            chartDailyTrend.clear();
            return;
        }

        boolean seeded = repository.ensureCheckinStatsMockData(userId);

        HealthCheckinSummary summary = repository.getHealthCheckinSummary(userId);
        String summaryText = "\u559d\u6c34\u8bb0\u5f55\uff1a" + summary.getWaterCount()
                + "\n\u5403\u836f\u6253\u5361\uff1a" + summary.getMedicineCount()
                + "\n\u4f53\u91cd\u8bb0\u5f55\uff1a" + summary.getWeightCount()
                + "\n\u6700\u65b0\u4f53\u91cd\uff1a" + (TextUtils.isEmpty(summary.getLatestWeight()) ? "--" : summary.getLatestWeight())
                + "\n\u4e60\u60ef\u4efb\u52a1\uff1a" + summary.getHabitCount()
                + "\n\u4eca\u65e5\u5df2\u6253\u5361\u4e60\u60ef\uff1a" + summary.getHabitCheckedTodayCount()
                + "\n\u7528\u836f\u63d0\u9192\uff1a" + summary.getReminderCount()
                + "\uff08\u5df2\u5f00\u542f" + summary.getReminderEnabledCount() + "\uff09";
        tvSummary.setText(summaryText);

        List<QuickRecordItem> quickRecords = repository.listQuickRecords(userId, 200);
        renderTypeBarChart(summary);
        renderDailyLineChart(quickRecords);

        tvQuickRecords.setText(buildQuickRecordText(quickRecords));
        tvHabits.setText(buildHabitText(repository.listHabitsWithStatus(userId)));
        tvReminders.setText(buildReminderText(repository.listAllMedicationReminders(userId)));
        HealthDebugLogger.i(TAG, "renderStats loaded. userId=" + userId
                + ", seededMockData=" + seeded
                + ", waterCount=" + summary.getWaterCount()
                + ", medicineCount=" + summary.getMedicineCount()
                + ", weightCount=" + summary.getWeightCount()
                + ", habitCount=" + summary.getHabitCount()
                + ", reminderCount=" + summary.getReminderCount()
                + ", quickRecordCount=" + quickRecords.size());
    }

    private void renderTypeBarChart(HealthCheckinSummary summary) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, summary.getWaterCount()));
        entries.add(new BarEntry(1f, summary.getMedicineCount()));
        entries.add(new BarEntry(2f, summary.getWeightCount()));

        BarDataSet set = new BarDataSet(entries, "\u6253\u5361\u6b21\u6570");
        set.setColors(
                Color.parseColor("#42A5F5"),
                Color.parseColor("#66BB6A"),
                Color.parseColor("#FFA726")
        );
        set.setValueTextColor(Color.parseColor("#333333"));
        set.setValueTextSize(12f);

        BarData data = new BarData(set);
        data.setBarWidth(0.55f);
        chartTypeCounts.setData(data);

        chartTypeCounts.getDescription().setEnabled(false);
        chartTypeCounts.getLegend().setEnabled(false);
        chartTypeCounts.setDrawGridBackground(false);
        chartTypeCounts.setNoDataText("\u6682\u65e0\u6570\u636e");
        chartTypeCounts.setFitBars(true);
        chartTypeCounts.setExtraBottomOffset(8f);

        XAxis xAxis = chartTypeCounts.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{
                "\u559d\u6c34", "\u5403\u836f", "\u4f53\u91cd"
        }));

        YAxis leftAxis = chartTypeCounts.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        chartTypeCounts.getAxisRight().setEnabled(false);

        chartTypeCounts.invalidate();
        HealthDebugLogger.d(TAG, "renderTypeBarChart completed. water=" + summary.getWaterCount()
                + ", medicine=" + summary.getMedicineCount()
                + ", weight=" + summary.getWeightCount());
    }

    private void renderDailyLineChart(List<QuickRecordItem> quickRecords) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> dayCountMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            dayCountMap.put(today.minusDays(i), 0);
        }

        for (QuickRecordItem item : quickRecords) {
            LocalDate date = Instant.ofEpochMilli(item.getCreatedAt())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (dayCountMap.containsKey(date)) {
                dayCountMap.put(date, dayCountMap.get(date) + 1);
            }
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<LocalDate, Integer> item : dayCountMap.entrySet()) {
            entries.add(new Entry(index, item.getValue()));
            labels.add(item.getKey().format(dayFormat));
            index++;
        }

        LineDataSet lineSet = new LineDataSet(entries, "\u6bcf\u65e5\u6253\u5361\u603b\u6570");
        lineSet.setColor(Color.parseColor("#1E88E5"));
        lineSet.setCircleColor(Color.parseColor("#1E88E5"));
        lineSet.setCircleHoleColor(Color.WHITE);
        lineSet.setLineWidth(2f);
        lineSet.setCircleRadius(3.5f);
        lineSet.setValueTextSize(11f);
        lineSet.setValueTextColor(Color.parseColor("#333333"));
        lineSet.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(lineSet);
        chartDailyTrend.setData(lineData);

        chartDailyTrend.getDescription().setEnabled(false);
        chartDailyTrend.getLegend().setEnabled(false);
        chartDailyTrend.setNoDataText("\u6682\u65e0\u6570\u636e");
        chartDailyTrend.setDrawGridBackground(false);
        chartDailyTrend.setExtraBottomOffset(8f);

        XAxis xAxis = chartDailyTrend.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size(), true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        YAxis leftAxis = chartDailyTrend.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        chartDailyTrend.getAxisRight().setEnabled(false);

        chartDailyTrend.invalidate();
        HealthDebugLogger.d(TAG, "renderDailyLineChart completed. quickRecordCount=" + quickRecords.size()
                + ", dayCount=" + labels.size());
    }

    private String buildQuickRecordText(List<QuickRecordItem> items) {
        if (items == null || items.isEmpty()) {
            return "\u6682\u65e0\u5feb\u6377\u8bb0\u5f55";
        }
        StringBuilder builder = new StringBuilder();
        int max = Math.min(items.size(), 30);
        for (int i = 0; i < max; i++) {
            QuickRecordItem item = items.get(i);
            builder.append("[")
                    .append(timeFormat.format(new Date(item.getCreatedAt())))
                    .append("] ")
                    .append(typeLabel(item.getType()))
                    .append("  ")
                    .append(item.getValue() == null ? "--" : item.getValue());
            if (!TextUtils.isEmpty(item.getNote())) {
                builder.append("  (").append(item.getNote()).append(")");
            }
            builder.append('\n');
        }
        return builder.toString().trim();
    }

    private String buildHabitText(List<HabitStatus> items) {
        if (items == null || items.isEmpty()) {
            return "\u6682\u65e0\u4e60\u60ef\u4efb\u52a1";
        }
        StringBuilder builder = new StringBuilder("\u4e60\u60ef\u6253\u5361\uff1a\n");
        for (HabitStatus item : items) {
            builder.append(item.getHabitName())
                    .append(" - ")
                    .append(item.isCheckedToday()
                            ? "\u4eca\u65e5\u5df2\u6253\u5361"
                            : "\u4eca\u65e5\u672a\u6253\u5361")
                    .append('\n');
        }
        return builder.toString().trim();
    }

    private String buildReminderText(List<MedicationReminderItem> items) {
        if (items == null || items.isEmpty()) {
            return "\u6682\u65e0\u7528\u836f\u63d0\u9192";
        }
        StringBuilder builder = new StringBuilder("\u7528\u836f\u63d0\u9192\uff1a\n");
        for (MedicationReminderItem item : items) {
            builder.append(item.getMedicineName())
                    .append(" ")
                    .append(item.getDosage())
                    .append(" @ ")
                    .append(item.getReminderTime())
                    .append(" - ")
                    .append(item.isEnabled()
                            ? "\u5df2\u5f00\u542f"
                            : "\u5df2\u5173\u95ed");
            if (!TextUtils.isEmpty(item.getLastTakenDate())) {
                builder.append("\uff0c\u6700\u8fd1\u670d\u7528\uff1a")
                        .append(item.getLastTakenDate());
            }
            builder.append('\n');
        }
        return builder.toString().trim();
    }

    private String typeLabel(String type) {
        if (LocalHealthRepository.QUICK_TYPE_WATER.equals(type)) {
            return "\u559d\u6c34";
        }
        if (LocalHealthRepository.QUICK_TYPE_MEDICINE.equals(type)) {
            return "\u5403\u836f";
        }
        if (LocalHealthRepository.QUICK_TYPE_WEIGHT.equals(type)) {
            return "\u4f53\u91cd";
        }
        return type;
    }

    private long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HealthApplication.curUser != null && HealthApplication.curUser.getId() != null) {
            return HealthApplication.curUser.getId();
        }
        return 0L;
    }
}
