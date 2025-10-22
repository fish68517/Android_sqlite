// java
package com.example.application.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.ActivityRegisterBinding;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        binding.btnPerformRegister.setOnClickListener(v -> attemptRegister());
        binding.tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish(); // Finish RegisterActivity when going to Login
        });
    }

    private void attemptRegister() {
        // Reset errors
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPassword.setError(null);
        binding.tilRegisterConfirmPassword.setError(null);

        String email = binding.etRegisterEmail.getText().toString().trim();
        String password = binding.etRegisterPassword.getText().toString().trim();
        String confirmPassword = binding.etRegisterConfirmPassword.getText().toString().trim();

        boolean cancel = false;
        android.view.View focusView = null;

        // Task: Form validation - Level 2
        // Description: Validate password and confirm password.
        if (TextUtils.isEmpty(password)) {
            binding.tilRegisterPassword.setError("Password cannot be empty");
            focusView = binding.etRegisterPassword;
            cancel = true;
        } else if (password.length() < 6) {
            binding.tilRegisterPassword.setError("Password must be at least 6 characters");
            focusView = binding.etRegisterPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.tilRegisterConfirmPassword.setError("Please confirm password");
            focusView = binding.etRegisterConfirmPassword;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            binding.tilRegisterConfirmPassword.setError("Passwords do not match");
            focusView = binding.etRegisterConfirmPassword;
            cancel = true;
        }


        // Task: Form validation - Level 2
        // Description: Validate email format.
        if (TextUtils.isEmpty(email)) {
            binding.tilRegisterEmail.setError("Email cannot be empty");
            focusView = binding.etRegisterEmail;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilRegisterEmail.setError("Please enter a valid email address");
            focusView = binding.etRegisterEmail;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // TODO: In a real app, call backend API or database to create a new user
            // Here we simulate successful registration and auto-login
            performRegistration(email,password);
        }
    }
    private void performRegistration(String email,String password) {
        // Save login state and user info
        sharedPreferences.edit()
                .putBoolean(Constants.KEY_LOGGED_IN, true)
                .putString(Constants.KEY_EMAIL, email)
                .putString(Constants.PASSWORD, password)
                .apply();

        Toast.makeText(this, "Registration successful and logged in!", Toast.LENGTH_SHORT).show();
        // Close registration page and return to previous page
        finish();
    }

}
