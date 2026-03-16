package com.hakimi.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.R;

public class AIAssistantActivity extends AppCompatActivity {

    private EditText etQuestionInput;
    private Button btnSendQuestion;
    private TextView tvAnswerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etQuestionInput = findViewById(R.id.et_question_input);
        btnSendQuestion = findViewById(R.id.btn_send_question);
        tvAnswerResult = findViewById(R.id.tv_answer_result);
    }

    private void setupListeners() {
        btnSendQuestion.setOnClickListener(v -> sendQuestion());
    }

    private void sendQuestion() {
        String question = etQuestionInput.getText().toString().trim();
        if (TextUtils.isEmpty(question)) {
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u95ee\u9898", Toast.LENGTH_SHORT).show();
            return;
        }

        String answer = getPlaceholderAnswer(question);
        tvAnswerResult.setText(answer);
    }

    // Placeholder answer logic. Replace this with real askAI API call later.
    private String getPlaceholderAnswer(String question) {
        String q = question.toLowerCase();
        if (q.contains("\u8dd1\u6b65") || q.contains("run")) {
            return "\u5efa\u8bae\u8dd1\u6b65\u524d\u5148\u70ed\u8eab5-10\u5206\u949f\uff0c\u8dd1\u540e\u505a\u62c9\u4f38\uff0c\u6bcf\u54683-4\u6b21\u66f4\u7a33\u5b9a\u3002";
        }
        if (q.contains("\u51cf\u8102") || q.contains("\u51cf\u91cd")) {
            return "\u51cf\u8102\u7684\u5173\u952e\u662f\u996e\u98df\u63a7\u5236+\u89c4\u5f8b\u8fd0\u52a8\uff0c\u53ef\u5148\u4ece\u6bcf\u592930\u5206\u949f\u6709\u6c27\u5f00\u59cb\u3002";
        }
        if (q.contains("\u589e\u808c")) {
            return "\u589e\u808c\u5efa\u8bae\u4ee5\u529b\u91cf\u8bad\u7ec3\u4e3b\uff0c\u6ce8\u610f\u86cb\u767d\u8d28\u6444\u5165\u548c\u8db3\u591f\u7761\u7720\u3002";
        }
        return "\u8fd9\u662f\u6682\u65f6\u5360\u4f4d\u56de\u7b54\uff1a\u4f60\u7684\u95ee\u9898\u5f88\u6709\u4ef7\u503c\uff0c\u5efa\u8bae\u7ed3\u5408\u4f5c\u606f\u5236\u5b9a\u53ef\u6301\u7eed\u7684\u8bad\u7ec3\u8ba1\u5212\u3002";
    }
}
