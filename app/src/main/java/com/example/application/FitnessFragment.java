package com.example.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.model.Exercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FitnessFragment extends Fragment {

    private Spinner spinnerExerciseType;
    private Button btnCheckinExercise, btnFitnessPlan;
    private ListView lvExerciseHistory;
    private DatabaseHelper dbHelper;
    private int loggedInUserId = -1;
    private ArrayAdapter<String> historyAdapter;
    private ArrayList<String> exerciseHistoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        loggedInUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        spinnerExerciseType = view.findViewById(R.id.spinner_exercise_type);
        btnCheckinExercise = view.findViewById(R.id.btn_checkin_exercise);
        btnFitnessPlan = view.findViewById(R.id.btn_fitness_plan);
        lvExerciseHistory = view.findViewById(R.id.lv_exercise_history);

        // 设置 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.exercise_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExerciseType.setAdapter(adapter);

        // 设置历史记录的Adapter
        historyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, exerciseHistoryList);
        lvExerciseHistory.setAdapter(historyAdapter);

        btnCheckinExercise.setOnClickListener(v -> saveExerciseLog());
        btnFitnessPlan.setOnClickListener(v -> showFitnessPlan());

        // 加载历史记录
        loadExerciseHistory();

        return view;
    }



    private void saveExerciseLog() {
        String exerciseType = spinnerExerciseType.getSelectedItem().toString();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        Exercise exercise = new Exercise();
        exercise.setUserId(loggedInUserId);
        exercise.setExerciseType(exerciseType);
        exercise.setExerciseDate(currentDate);

        dbHelper.addExerciseLog(exercise);
        Toast.makeText(getContext(), "运动打卡成功！", Toast.LENGTH_SHORT).show();

        // 刷新列表
        loadExerciseHistory();
    }

    private void loadExerciseHistory() {
        exerciseHistoryList.clear();
        List<Exercise> exercises = dbHelper.getAllExerciseLogsForUser(loggedInUserId);
        for(Exercise ex : exercises) {
            exerciseHistoryList.add(ex.getExerciseDate() + " - " + ex.getExerciseType());
        }
        historyAdapter.notifyDataSetChanged();
    }

    private void showFitnessPlan() {
        new AlertDialog.Builder(getContext())
                .setTitle("健身计划 (模拟)")
                .setMessage("周一: 胸部训练\n周二: 背部训练\n周三: 腿部训练\n周四: 肩部训练\n周五: 手臂训练\n周末: 有氧或休息")
                .setPositiveButton("收到", null)
                .show();
    }
}