package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;

import java.util.Random;

/**
 * ToolsActivity - Displays various utility tools for health and diet management
 * Includes: Food Rankings, What to Eat Today, Food Weight Estimation, Daily Q&A
 */
public class ToolsActivity extends AppCompatActivity {

    // Toolbar 控件
    private Button backButton;
    private TextView toolbarTitle;

    private ScrollView toolsScrollView;
    private LinearLayout foodRankingCard;
    private LinearLayout whatToEatCard;
    private LinearLayout foodWeightCard;
    private LinearLayout dailyQACard;
    private TextView foodRankingContent;
    private TextView whatToEatContent;
    private TextView foodWeightContent;
    private TextView dailyQAContent;
    private Button randomSnackButton;
    private Button randomFastFoodButton;

    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        initializeViews();
        initializeData();
        setupListeners();
    }

    private void initializeViews() {
        // 绑定公共 Toolbar 并设置标题
        backButton = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        if (toolbarTitle != null) {
            toolbarTitle.setText("健康工具");
        }

        // 绑定卡片与内容视图
        toolsScrollView = findViewById(R.id.toolsScrollView);
        foodRankingCard = findViewById(R.id.foodRankingCard);
        whatToEatCard = findViewById(R.id.whatToEatCard);
        foodWeightCard = findViewById(R.id.foodWeightCard);
        dailyQACard = findViewById(R.id.dailyQACard);

        foodRankingContent = findViewById(R.id.foodRankingContent);
        whatToEatContent = findViewById(R.id.whatToEatContent);
        foodWeightContent = findViewById(R.id.foodWeightContent);
        dailyQAContent = findViewById(R.id.dailyQAContent);

        randomSnackButton = findViewById(R.id.randomSnackButton);
        randomFastFoodButton = findViewById(R.id.randomFastFoodButton);
    }

    private void initializeData() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        random = new Random();

        populateStaticData();
        displayDefaultQA();
    }

    private void setupListeners() {
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // 随机零食推荐
        randomSnackButton.setOnClickListener(v -> {
            String[] snacks = {
                    "苹果切片配花生酱",
                    "希腊无糖酸奶配坚果",
                    "一小把混合坚果",
                    "两个水煮蛋",
                    "全麦吐司配牛油果",
                    "香蕉和一小块黑巧克力"
            };
            String selected = snacks[random.nextInt(snacks.length)];
            whatToEatContent.setText("推荐零食：\n" + selected);
        });

        // 随机外卖/正餐推荐
        randomFastFoodButton.setOnClickListener(v -> {
            String[] meals = {
                    "轻食波波碗 (Poke Bowl)",
                    "日式寿司或刺身",
                    "烤鸡肉蔬菜沙拉",
                    "全麦火腿三明治",
                    "牛肉荞麦面",
                    "无糖无油的健康蒸菜盒"
            };
            String selected = meals[random.nextInt(meals.length)];
            whatToEatContent.setText("推荐外卖：\n" + selected);
        });
    }

    /**
     * 填充静态的提示与参考数据 (汉化版)
     */
    private void populateStaticData() {
        // 1. 食物排行榜
        String rankings = "1. 鸡胸肉 - 高蛋白，极低脂肪\n" +
                "2. 西兰花 - 高纤维，富含各类维生素\n" +
                "3. 燕麦 - 复合碳水，提供持久的能量\n" +
                "4. 三文鱼 - 富含 Omega-3 和优质脂肪\n" +
                "5. 红薯 - 优质的低 GI (升糖指数) 碳水化合物";
        foodRankingContent.setText(rankings);

        // 2. 今天吃什么（初始占位文本）
        whatToEatContent.setText("点击下方按钮，获取随机的健康饮食建议！");

        // 3. 食物重量估算参考
        String weightGuide = "• 1 个手掌大小的肉类 ≈ 85克\n" +
                "• 1 个拳头大小的主食 ≈ 1 碗 (约150克)\n" +
                "• 1 个拇指大小的脂肪 ≈ 1 汤匙 (约15克)\n" +
                "• 1 捧零食 (如坚果) ≈ 半碗 (约30克)\n" +
                "• 2 捧蔬菜 ≈ 1 盘 (约200克)";
        foodWeightContent.setText(weightGuide);
    }

    /**
     * 随机展示一条每日健康问答 (汉化版)
     */
    private void displayDefaultQA() {
        String[] questions = {
                "❓ 每天我应该喝多少水？\n\n✅ 建议每天喝 8-10 杯（2-3 升）水。具体请根据活动量、体重和天气炎热程度进行适度调整。",
                "❓ 什么时候吃饭最好？\n\n✅ 建议每 3-4 小时进食一次。起床后 1 小时内吃早餐，睡前 2-3 小时吃晚餐，让肠胃有时间消化。",
                "❓ 我应该摄入多少卡路里？\n\n✅ 成年人平均每天需要 2000-2500 卡路里。具体请根据您的年龄、性别、体重目标和运动量进行科学调整。",
                "❓ 不吃早餐可以减肥吗？\n\n✅ 不建议。吃健康的早餐能唤醒新陈代谢，避免午餐过度饥饿导致暴饮暴食。最好在起床后 1-2 小时内进食。",
                "❓ 什么是健康的减脂零食？\n\n✅ 优先选择天然原木食物：坚果、新鲜水果、无糖酸奶或全谷物饼干。尽量避免深加工、高糖、高油的食品。",
                "❓ 我应该多久锻炼一次？\n\n✅ 目标是每周进行 150 分钟的中等强度有氧运动（如快走），或 75 分钟的高强度运动（如跑步），搭配 2 次力量训练。",
                "❓ 太晚吃饭会发胖吗？\n\n✅ 发胖主要看全天总摄入的热量。但睡前 2-3 小时进食是最理想的，太晚吃大餐会加重肠胃负担并影响睡眠质量。",
                "❓ 最好的饮食方式是什么？\n\n✅ 适合自己的才是最好的。包含优质蛋白质、粗粮碳水、健康脂肪和大量蔬菜的均衡饮食最重要。长期的坚持比短期苛刻更有效。"
        };

        // 随机抽取一个问题显示在卡片上
        String randomQA = questions[random.nextInt(questions.length)];
        dailyQAContent.setText(randomQA);
    }
}