package com.hakimi.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.Diary;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
import com.hakimi.ui.activity.WriteDiaryActivity;
import com.hakimi.ui.adapter.DiaryAdapter;
import com.hakimi.utils.SharedPrefManager;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryFragment extends Fragment {

    private RecyclerView rvDiaryList;
    private Button btnWriteDiary;
    private LineChart lineChartMood;
    private DiaryAdapter diaryAdapter;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private final List<Diary> diaryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitClient.getInstance().getApiService();
        sharedPrefManager = SharedPrefManager.getInstance();

        initViews(view);
        setupChart();
        setupRecyclerView();
        loadDiaries();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDiaries();
    }

    private void initViews(View view) {
        rvDiaryList = view.findViewById(R.id.rv_diary_list);
        btnWriteDiary = view.findViewById(R.id.btn_write_diary);
        lineChartMood = view.findViewById(R.id.line_chart_mood);

        btnWriteDiary.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), WriteDiaryActivity.class)));
    }

    private void setupRecyclerView() {
        rvDiaryList.setLayoutManager(new LinearLayoutManager(getContext()));
        diaryAdapter = new DiaryAdapter(diaryList);
        rvDiaryList.setAdapter(diaryAdapter);
    }

    private void setupChart() {
        lineChartMood.getDescription().setEnabled(false);
        lineChartMood.getLegend().setEnabled(false);
        lineChartMood.setTouchEnabled(true);
        lineChartMood.setDragEnabled(true);
        lineChartMood.setScaleEnabled(false);

        XAxis xAxis = lineChartMood.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setAxisLineColor(Color.parseColor("#EEEEEE"));
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChartMood.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F5F7FA"));
        leftAxis.setAxisLineColor(Color.TRANSPARENT);
        leftAxis.setTextColor(Color.parseColor("#333333"));
        leftAxis.setAxisMinimum(0.5f);
        leftAxis.setAxisMaximum(3.5f);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 1f) {
                    return "\u4e0d\u5f00\u5fc3";
                }
                if (value == 2f) {
                    return "\u4e00\u822c";
                }
                if (value == 3f) {
                    return "\u5f00\u5fc3";
                }
                return "";
            }
        });

        lineChartMood.getAxisRight().setEnabled(false);
    }

    private void loadDiaries() {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            return;
        }

        apiService.getDiaries(userId).enqueue(new Callback<ApiResponse<List<Diary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Diary>>> call, Response<ApiResponse<List<Diary>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Diary> diaries = response.body().getData();
                    diaryList.clear();
                    if (diaries != null) {
                        diaryList.addAll(diaries);
                    }
                    diaryAdapter.setDiaries(diaryList);
                    updateChart(diaryList);
                } else {
                    Toast.makeText(getContext(), "\u52a0\u8f7d\u65e5\u8bb0\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Diary>>> call, Throwable t) {
                Toast.makeText(getContext(), "\u52a0\u8f7d\u65e5\u8bb0\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChart(List<Diary> diaries) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Diary> latestDiaryByDate = new HashMap<>();

        List<Diary> sortedDiaries = new ArrayList<>(diaries);
        Collections.sort(sortedDiaries, Comparator.comparing(Diary::getCreatedAt, Comparator.nullsLast(String::compareTo)));

        for (Diary diary : sortedDiaries) {
            LocalDate date = parseDate(diary.getCreatedAt());
            if (date != null) {
                latestDiaryByDate.put(date, diary);
            }
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault());

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            labels.add(formatter.format(date));
            Diary diary = latestDiaryByDate.get(date);
            float moodValue = diary == null || diary.getMood() == null ? 2f : diary.getMood();
            entries.add(new Entry(6 - i, moodValue));
        }

        XAxis xAxis = lineChartMood.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });

        LineDataSet dataSet = new LineDataSet(entries, "Mood");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.parseColor("#4A90E2"));
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.parseColor("#4A90E2"));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4A90E2"));
        dataSet.setFillAlpha(40);

        lineChartMood.setData(new LineData(dataSet));
        lineChartMood.invalidate();
        lineChartMood.animateX(800);
    }

    private LocalDate parseDate(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toLocalDate();
        } catch (Exception ignore) {
        }
        try {
            return LocalDate.parse(value.substring(0, 10));
        } catch (Exception ignore) {
        }
        return null;
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
        }
        return null;
    }
}
