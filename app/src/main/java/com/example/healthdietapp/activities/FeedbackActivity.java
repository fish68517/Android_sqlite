package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.Feedback;
import com.example.healthdietapp.utils.ErrorHandler;
import com.example.healthdietapp.utils.SessionManager;
import com.example.healthdietapp.utils.ValidationUtils;

/**
 * FeedbackActivity - Allows users to submit feedback
 * Collects user feedback and saves it to the database
 */
public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackInput;
    private Button submitButton;
    private Button cancelButton;

    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initializeViews();
        initializeDatabase();
        setupListeners();

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText("意见反馈");
        }

        Button backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void initializeViews() {
        feedbackInput = findViewById(R.id.feedbackInput);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> submitFeedback());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void submitFeedback() {
        String feedbackContent = feedbackInput.getText().toString().trim();

        // Validate feedback content
        ValidationUtils.ValidationResult validation = ValidationUtils.validateTextInput(feedbackContent, "反馈内容", 1, 5000);
        if (!validation.isValid()) {
            ErrorHandler.handleValidationException(this, validation.getMessage());
            return;
        }

        submitButton.setEnabled(false);

        try {
            Feedback feedback = new Feedback();
            feedback.setUserId(userId);
            feedback.setContent(feedbackContent);
            feedback.setCreatedAt(System.currentTimeMillis());

            boolean success = postDAO.createFeedback(feedback);

            runOnUiThread(() -> {
                submitButton.setEnabled(true);
                if (success) {
                    ErrorHandler.showShortToast(this, "反馈已提交");
                    finish();
                } else {
                    ErrorHandler.showShortToast(this, "提交反馈失败");
                }
            });
        } catch (Exception e) {
            ErrorHandler.logException("FeedbackActivity", e);
            runOnUiThread(() -> {
                submitButton.setEnabled(true);
                ErrorHandler.handleDatabaseException(this, e);
            });
        }
    }
}
