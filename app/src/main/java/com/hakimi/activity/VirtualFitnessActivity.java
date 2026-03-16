package com.hakimi.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.LocalResult;
import com.hakimi.model.FitnessPlan;
import com.hakimi.utils.SharedPrefManager;

public class VirtualFitnessActivity extends AppCompatActivity {

    private static final String PREF_NAME = "virtual_fitness_pref";
    private static final String KEY_LAST_GOAL = "last_goal";
    private static final String KEY_LAST_PLAN = "last_plan";

    private EditText etGoalInput;
    private Button btnSubmitGoal;
    private TextView tvGeneratedPlan;

    private SharedPreferences sharedPreferences;
    private SharedPrefManager sharedPrefManager;
    private LocalHealthRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_fitness);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sharedPrefManager = SharedPrefManager.getInstance();
        repository = LocalHealthRepository.getInstance(this);
        initViews();
        loadLastData();
        setupListeners();
    }

    private void initViews() {
        etGoalInput = findViewById(R.id.et_fitness_goal);
        btnSubmitGoal = findViewById(R.id.btn_submit_goal);
        tvGeneratedPlan = findViewById(R.id.tv_fitness_plan_result);
    }

    private void loadLastData() {
        String lastGoal = sharedPreferences.getString(KEY_LAST_GOAL, "");
        String lastPlan = sharedPreferences.getString(KEY_LAST_PLAN, "");
        etGoalInput.setText(lastGoal);
        if (!TextUtils.isEmpty(lastPlan)) {
            tvGeneratedPlan.setText(lastPlan);
        }
    }

    private void setupListeners() {
        btnSubmitGoal.setOnClickListener(v -> submitGoal());
    }

    private void submitGoal() {
        String goal = etGoalInput.getText().toString().trim();
        if (TextUtils.isEmpty(goal)) {
            Toast.makeText(this, "请输入健身目标", Toast.LENGTH_SHORT).show();
            return;
        }

        String generatedPlan = generatePlaceholderPlan(goal);
        tvGeneratedPlan.setText(generatedPlan);

        sharedPreferences.edit()
                .putString(KEY_LAST_GOAL, goal)
                .putString(KEY_LAST_PLAN, generatedPlan)
                .apply();

        savePlanToDatabase(goal, generatedPlan);
    }

    private void savePlanToDatabase(String goal, String planContent) {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "已生成计划（未获取到用户，未同步数据库）",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<FitnessPlan> result = repository.saveFitnessPlan(userId, goal, planContent);
        if (result.isSuccess()) {
            Toast.makeText(this, "已生成并保存到本地数据库", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "计划已生成，但入库失败", Toast.LENGTH_SHORT).show();
        }
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

    private String generatePlaceholderPlan(String goal) {
        String lower = goal.toLowerCase();
        if (lower.contains("减脂") || lower.contains("减重") || lower.contains("fat")) {
            return "【虚拟AI减脂计划（7天）】\n"
                    + "1. 周一/三/五：30分钟慢跑 + 15分钟核心训练\n"
                    + "2. 周二/四：20分钟HIIT + 10分钟拉伸\n"
                    + "3. 周末：60分钟快走或骑行\n"
                    + "4. 饮食：减少含糖饮料，每天水摄入2L";
        }
        if (lower.contains("增肌") || lower.contains("muscle")) {
            return "【虚拟AI增肌计划（7天）】\n"
                    + "1. 周一：胸+三头，每个动作4组\n"
                    + "2. 周三：背+二头，每个动作4组\n"
                    + "3. 周五：腿+肩，每个动作4组\n"
                    + "4. 每次训练后补充蛋白质，保证7小时睡眠";
        }
        if (lower.contains("耐力") || lower.contains("endurance")) {
            return "【虚拟AI耐力计划（7天）】\n"
                    + "1. 周一/三/五：40分钟有氧跑\n"
                    + "2. 周二/四：间歇跑 10组（快1分钟+慢1分钟）\n"
                    + "3. 每天加入 10分钟呼吸训练和拉伸";
        }
        return "【虚拟AI通用计划（7天）】\n"
                + "目标：" + goal + "\n"
                + "1. 每周训练4天，每次45分钟\n"
                + "2. 有氧与力量结合，循序渐进\n"
                + "3. 运动后进行拉伸10分钟，每天记录状态";
    }
}
