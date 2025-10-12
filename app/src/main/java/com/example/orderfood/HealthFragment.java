package com.example.orderfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.orderfood.model.Diet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealthFragment extends Fragment {

    private Spinner spinnerMealType;
    private EditText etFoodContent;
    private Button btnSaveDiet, btnCanteenCalories, btnDietAdvice;
    private DatabaseHelper dbHelper;
    private int loggedInUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        loggedInUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        spinnerMealType = view.findViewById(R.id.spinner_meal_type);
        etFoodContent = view.findViewById(R.id.et_food_content);
        btnSaveDiet = view.findViewById(R.id.btn_save_diet);
        btnCanteenCalories = view.findViewById(R.id.btn_canteen_calories);
        btnDietAdvice = view.findViewById(R.id.btn_diet_advice);

        // 设置 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(adapter);

        btnSaveDiet.setOnClickListener(v -> saveDietLog());

        btnCanteenCalories.setOnClickListener(v -> showCanteenCalories());

        btnDietAdvice.setOnClickListener(v -> showDietAdvice());

        return view;
    }

    // 为了让Spinner工作, 你需要在 res/values/strings.xml 中添加
    // <string-array name="meal_types">
    //     <item>早餐</item>
    //     <item>午餐</item>
    //     <item>晚餐</item>
    //     <item>加餐</item>
    // </string-array>

    private void saveDietLog() {
        String mealType = spinnerMealType.getSelectedItem().toString();
        String foodContent = etFoodContent.getText().toString().trim();
        if (foodContent.isEmpty()) {
            Toast.makeText(getContext(), "请输入食物内容", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Diet diet = new Diet();
        diet.setUserId(loggedInUserId);
        diet.setMealType(mealType);
        diet.setFoodContent(foodContent);
        diet.setRecordDate(currentDate);

        dbHelper.addDietLog(diet);
        Toast.makeText(getContext(), "饮食记录已保存", Toast.LENGTH_SHORT).show();
        etFoodContent.setText(""); // 清空输入框
    }

    private void showCanteenCalories() {
        new AlertDialog.Builder(getContext())
                .setTitle("食堂卡路里参考 (模拟)")
                .setMessage("- 红烧肉 (1份): 约 450 大卡\n- 番茄炒蛋 (1份): 约 200 大卡\n- 米饭 (1碗): 约 250 大卡\n- 清炒时蔬 (1份): 约 90 大卡")
                .setPositiveButton("了解", null)
                .show();
    }

    private void showDietAdvice() {
        new AlertDialog.Builder(getContext())
                .setTitle("饮食建议 (模拟)")
                .setMessage("1. 均衡营养，多吃蔬菜水果。\n2. 适量摄入蛋白质，如鱼、肉、蛋、奶。\n3. 减少高油、高糖、高盐食物的摄入。\n4. 每天保证充足饮水。")
                .setPositiveButton("好的", null)
                .show();
    }
}