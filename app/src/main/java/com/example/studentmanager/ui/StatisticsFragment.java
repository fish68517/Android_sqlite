package com.example.studentmanager.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.studentmanagement.R;
import com.example.studentmanager.db.StudentDBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class StatisticsFragment extends Fragment {
    private TextView tvTotalStudents;
    private TextView tvCurrentStudents;
    private TextView tvGraduatedStudents;
    private TextView tvComputerClassStudents;
    private TextView tvElectronicClassStudents;
    
    // 按年查询相关
    private TextInputEditText etYear;
    private MaterialButton btnQueryByYear;
    private LinearLayout yearlyStatsResultLayout;
    private TextView tvAdmissionsByYear;
    private TextView tvGraduationsByYear;
    private int selectedYear;

    private StudentDBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new StudentDBHelper(getActivity());
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadStatistics();
    }

    private void initViews(View view) {
        tvTotalStudents = view.findViewById(R.id.tv_total_students);
        tvCurrentStudents = view.findViewById(R.id.tv_current_students);
        tvGraduatedStudents = view.findViewById(R.id.tv_graduated_students);
        tvComputerClassStudents = view.findViewById(R.id.tv_computer_class_students);
        tvElectronicClassStudents = view.findViewById(R.id.tv_electronic_class_students);

        // 初始化按年查询的视图
        etYear = view.findViewById(R.id.et_year);
        btnQueryByYear = view.findViewById(R.id.btn_query_by_year);
        yearlyStatsResultLayout = view.findViewById(R.id.yearly_stats_result_layout);
        tvAdmissionsByYear = view.findViewById(R.id.tv_admissions_by_year);
        tvGraduationsByYear = view.findViewById(R.id.tv_graduations_by_year);

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        etYear.setText(String.valueOf(selectedYear));

        setupListeners();
    }
    
    private void setupListeners() {
        etYear.setOnClickListener(v -> showYearPickerDialog());
        btnQueryByYear.setOnClickListener(v -> loadYearlyStatistics(selectedYear));
    }

    private void showYearPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择年份");

        final NumberPicker numberPicker = new NumberPicker(getContext());
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        numberPicker.setMinValue(2000);
        numberPicker.setMaxValue(currentYear + 10);
        numberPicker.setValue(selectedYear);

        builder.setView(numberPicker);

        builder.setPositiveButton("确定", (dialog, which) -> {
            selectedYear = numberPicker.getValue();
            etYear.setText(String.valueOf(selectedYear));
            yearlyStatsResultLayout.setVisibility(View.GONE); // 选择新年份后隐藏旧结果
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void loadYearlyStatistics(int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String yearPattern = year + "%";

        // 查询该年入学人数
        Cursor admissionsCursor = db.rawQuery("SELECT COUNT(*) FROM students WHERE admission_date LIKE ?", new String[]{yearPattern});
        if (admissionsCursor != null && admissionsCursor.moveToFirst()) {
            int admissionsCount = admissionsCursor.getInt(0);
            tvAdmissionsByYear.setText("该年入学人数：" + admissionsCount);
            admissionsCursor.close();
        }

        // 查询该年毕业人数
        Cursor graduationsCursor = db.rawQuery("SELECT COUNT(*) FROM students WHERE graduation_date LIKE ?", new String[]{yearPattern});
        if (graduationsCursor != null && graduationsCursor.moveToFirst()) {
            int graduationsCount = graduationsCursor.getInt(0);
            tvGraduationsByYear.setText("该年毕业人数：" + graduationsCount);
            graduationsCursor.close();
        }

        yearlyStatsResultLayout.setVisibility(View.VISIBLE);
    }

    private void loadStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 查询总学生数
        Cursor totalCursor = db.rawQuery("SELECT COUNT(*) FROM students", null);
        if (totalCursor != null && totalCursor.moveToFirst()) {
            int total = totalCursor.getInt(0);
            tvTotalStudents.setText(String.valueOf(total));
            totalCursor.close();
        }

        // 查询在校学生数
        Cursor currentCursor = db.rawQuery("SELECT COUNT(*) FROM students WHERE status = '在校'", null);
        if (currentCursor != null && currentCursor.moveToFirst()) {
            int current = currentCursor.getInt(0);
            tvCurrentStudents.setText(String.valueOf(current));
            currentCursor.close();
        }

        // 查询已毕业学生数
        Cursor graduatedCursor = db.rawQuery("SELECT COUNT(*) FROM students WHERE status = '毕业'", null);
        if (graduatedCursor != null && graduatedCursor.moveToFirst()) {
            int graduated = graduatedCursor.getInt(0);
            tvGraduatedStudents.setText(String.valueOf(graduated));
            graduatedCursor.close();
        }

        // 查询计算机类学生数
        Cursor computerCursor = db.rawQuery("SELECT COUNT(*) FROM students WHERE class_id IN (SELECT class_id FROM classes WHERE class_type = '计算机类')", null);
        if (computerCursor != null && computerCursor.moveToFirst()) {
            int computer = computerCursor.getInt(0);
            tvComputerClassStudents.setText(String.valueOf(computer));
            computerCursor.close();
        }

        // 查询电子信息类学生数
        Cursor electronicCursor = db.rawQuery("SELECT COUNT(*) FROM students WHERE class_id IN (SELECT class_id FROM classes WHERE class_type = '电子信息类')", null);
        if (electronicCursor != null && electronicCursor.moveToFirst()) {
            int electronic = electronicCursor.getInt(0);
            tvElectronicClassStudents.setText(String.valueOf(electronic));
            electronicCursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics(); // 刷新统计数据
    }
} 