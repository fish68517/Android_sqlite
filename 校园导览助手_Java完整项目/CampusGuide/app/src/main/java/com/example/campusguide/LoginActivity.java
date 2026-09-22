package com.example.campusguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS = "campus_guide_prefs";
    public static final String KEY_LOGGED_IN = "logged_in";
    public static final String KEY_USERNAME = "username";

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (preferences.getBoolean(KEY_LOGGED_IN, false)) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_login);
        usernameInput = findViewById(R.id.edit_username);
        passwordInput = findViewById(R.id.edit_password);
        findViewById(R.id.button_login).setOnClickListener(v -> attemptLogin());
        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    private void attemptLogin() {
        String username = usernameInput.getText() == null ? "" : usernameInput.getText().toString().trim();
        String password = passwordInput.getText() == null ? "" : passwordInput.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("请输入学号或用户名");
            usernameInput.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("密码至少需要 6 位");
            passwordInput.requestFocus();
            return;
        }

        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USERNAME, username)
                .apply();
        Toast.makeText(this, "登录成功，欢迎来到校园！", Toast.LENGTH_SHORT).show();
        openMain();
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
