package com.hakimi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.R;
import com.hakimi.ai.AiSymptomService;
import com.hakimi.ai.SiliconFlowAiSymptomService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIAssistantActivity extends AppCompatActivity {

    private EditText etQuestionInput;
    private Button btnVoiceInput;
    private Button btnSendQuestion;
    private TextView tvAnswerResult;
    private AiSymptomService aiSymptomService;
    private ExecutorService ioExecutor;

    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                ArrayList<String> text = result.getData()
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (text != null && !text.isEmpty()) {
                    etQuestionInput.setText(text.get(0));
                    etQuestionInput.setSelection(etQuestionInput.getText().length());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        aiSymptomService = new SiliconFlowAiSymptomService();
        ioExecutor = Executors.newSingleThreadExecutor();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etQuestionInput = findViewById(R.id.et_question_input);
        btnVoiceInput = findViewById(R.id.btn_voice_input);
        btnSendQuestion = findViewById(R.id.btn_send_question);
        tvAnswerResult = findViewById(R.id.tv_answer_result);
    }

    private void setupListeners() {
        btnVoiceInput.setOnClickListener(v -> startVoiceInput());
        btnSendQuestion.setOnClickListener(v -> sendQuestion());
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toLanguageTag());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe symptoms, e.g. stomach ache");
        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Voice input is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendQuestion() {
        String question = etQuestionInput.getText().toString().trim();
        if (TextUtils.isEmpty(question)) {
            Toast.makeText(this, "Please input your question", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSendQuestion.setEnabled(false);
        btnSendQuestion.setText("Analyzing...");
        tvAnswerResult.setText("Calling AI, please wait...");

        ioExecutor.execute(() -> {
            String answer = aiSymptomService.askSymptom(question);
            runOnUiThread(() -> {
                btnSendQuestion.setEnabled(true);
                btnSendQuestion.setText("Send");
                tvAnswerResult.setText(answer);
            });
        });
    }

    @Override
    protected void onDestroy() {
        if (ioExecutor != null) {
            ioExecutor.shutdownNow();
        }
        super.onDestroy();
    }
}
