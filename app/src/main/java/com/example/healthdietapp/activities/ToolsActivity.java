package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.HealthQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ToolsActivity - Displays various utility tools for health and diet management
 * Includes: Food Rankings, What to Eat Today, Food Weight Estimation, Daily Q&A
 */
public class ToolsActivity extends AppCompatActivity {

    private Button backButton;
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
        initializeDatabase();
        setupClickListeners();
        loadToolsContent();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
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
        random = new Random();
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        randomSnackButton.setOnClickListener(v -> displayRandomSnack());
        randomFastFoodButton.setOnClickListener(v -> displayRandomFastFood());
    }

    private void loadToolsContent() {
        loadFoodRankings();
        loadFoodWeightEstimation();
        loadDailyQA();
    }

    /**
     * Load and display food rankings
     */
    private void loadFoodRankings() {
        String rankings = "🏆 Weight Loss Ranking:\n" +
                "1. Chicken Breast - 165 cal/100g\n" +
                "2. Broccoli - 34 cal/100g\n" +
                "3. Salmon - 208 cal/100g\n\n" +
                "🍕 Takeout Ranking:\n" +
                "1. Pizza - 285 cal/slice\n" +
                "2. Burger - 354 cal/piece\n" +
                "3. Fried Chicken - 320 cal/piece\n\n" +
                "😴 Sleep-Friendly Foods:\n" +
                "1. Almonds - 579 cal/100g\n" +
                "2. Milk - 61 cal/100ml\n" +
                "3. Honey - 304 cal/100g\n\n" +
                "💇 Hair Care Foods:\n" +
                "1. Eggs - 155 cal/100g\n" +
                "2. Walnuts - 654 cal/100g\n" +
                "3. Spinach - 23 cal/100g\n\n" +
                "🌟 Acne-Fighting Foods:\n" +
                "1. Green Tea - 2 cal/100ml\n" +
                "2. Blueberries - 57 cal/100g\n" +
                "3. Carrots - 41 cal/100g";

        foodRankingContent.setText(rankings);
    }

    /**
     * Display random snack recommendation
     */
    private void displayRandomSnack() {
        String[] snacks = {
            "🍎 Apple - 52 cal/100g\nGreat source of fiber",
            "🥜 Almonds - 579 cal/100g\nRich in protein and healthy fats",
            "🍌 Banana - 89 cal/100g\nGood for energy",
            "🥕 Carrot - 41 cal/100g\nLow calorie, high fiber",
            "🍓 Strawberries - 32 cal/100g\nVitamin C rich",
            "🥒 Cucumber - 16 cal/100g\nHydrating and low calorie",
            "🧀 Greek Yogurt - 59 cal/100g\nHigh protein",
            "🍞 Whole Wheat Bread - 247 cal/100g\nGood carbs"
        };

        int randomIndex = random.nextInt(snacks.length);
        whatToEatContent.setText(snacks[randomIndex]);
        Toast.makeText(this, "Random snack suggestion!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Display random fast food recommendation
     */
    private void displayRandomFastFood() {
        String[] fastFoods = {
            "🍕 Pizza - 285 cal/slice\nModerate portion recommended",
            "🍔 Burger - 354 cal/piece\nPair with salad",
            "🍟 French Fries - 365 cal/100g\nEnjoy occasionally",
            "🌮 Taco - 226 cal/piece\nGood protein source",
            "🥙 Wrap - 298 cal/piece\nVegetable-filled option",
            "🍗 Fried Chicken - 320 cal/piece\nBaked alternative available",
            "🥤 Smoothie - 120 cal/250ml\nHealthy option",
            "🥗 Salad - 150 cal/serving\nNutritious choice"
        };

        int randomIndex = random.nextInt(fastFoods.length);
        whatToEatContent.setText(fastFoods[randomIndex]);
        Toast.makeText(this, "Random takeout suggestion!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Load and display food weight estimation reference
     */
    private void loadFoodWeightEstimation() {
        String weightEstimation = "📏 STAPLE FOODS:\n" +
                "• Rice (cooked) - 1 cup = 150g\n" +
                "• Pasta (cooked) - 1 cup = 140g\n" +
                "• Bread - 1 slice = 30g\n\n" +
                "🥚 PROTEIN SOURCES:\n" +
                "• Chicken Breast - 1 piece = 100g\n" +
                "• Egg - 1 large = 50g\n" +
                "• Salmon - 1 fillet = 150g\n" +
                "• Tofu - 1 block = 200g\n\n" +
                "🥬 VEGETABLES:\n" +
                "• Broccoli - 1 cup = 90g\n" +
                "• Spinach - 1 cup = 30g\n" +
                "• Carrot - 1 medium = 60g\n" +
                "• Tomato - 1 medium = 120g\n\n" +
                "🥜 NUTS & SEEDS:\n" +
                "• Almonds - 1 handful = 30g\n" +
                "• Peanuts - 1 handful = 35g\n" +
                "• Sunflower Seeds - 1 tbsp = 10g\n" +
                "• Walnuts - 1 handful = 30g";

        foodWeightContent.setText(weightEstimation);
    }

    /**
     * Load and display daily health Q&A
     */
    private void loadDailyQA() {
        new Thread(() -> {
            try {
                // Try to fetch from database first
                HealthQuestion question = getRandomHealthQuestion();
                runOnUiThread(() -> {
                    if (question != null) {
                        String qaText = "❓ " + question.getQuestion() + "\n\n" +
                                "✅ " + question.getAnswer();
                        dailyQAContent.setText(qaText);
                    } else {
                        displayDefaultQA();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::displayDefaultQA);
            }
        }).start();
    }

    /**
     * Get a random health question from database
     */
    private HealthQuestion getRandomHealthQuestion() {
        try {
            // This would require a method in PostDAO to get health questions
            // For now, return null to use default Q&A
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Display default Q&A if database fetch fails
     */
    private void displayDefaultQA() {
        String[] questions = {
            "❓ How much water should I drink daily?\n\n✅ Aim for 8-10 glasses (2-3 liters) per day. Adjust based on activity level and climate.",
            "❓ What's the best time to eat?\n\n✅ Eat every 3-4 hours. Breakfast within 1 hour of waking, dinner 2-3 hours before bed.",
            "❓ How many calories should I consume?\n\n✅ Average adult needs 2000-2500 calories. Adjust based on age, gender, and activity level.",
            "❓ Is skipping breakfast healthy?\n\n✅ No, breakfast jumpstarts metabolism. Eat within 1-2 hours of waking.",
            "❓ What's a healthy snack?\n\n✅ Choose nuts, fruits, yogurt, or whole grains. Avoid processed foods.",
            "❓ How often should I exercise?\n\n✅ Aim for 150 minutes of moderate activity or 75 minutes of vigorous activity weekly.",
            "❓ Is eating late bad?\n\n✅ Eating 2-3 hours before bed is ideal. Late eating can disrupt sleep.",
            "❓ What's the best diet?\n\n✅ A balanced diet with proteins, carbs, fats, and vegetables. Consistency matters more than perfection."
        };

        int randomIndex = random.nextInt(questions.length);
        dailyQAContent.setText(questions[randomIndex]);
    }
}
