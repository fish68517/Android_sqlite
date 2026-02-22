package com.example.healthdietapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;

/**
 * ContactUsActivity - Displays contact information and allows users to reach out
 * Provides email, phone, and social media contact options
 */
public class ContactUsActivity extends AppCompatActivity {

    private TextView emailText;
    private TextView phoneText;
    private Button emailButton;
    private Button phoneButton;
    private Button facebookButton;
    private Button twitterButton;
    private Button instagramButton;

    // Contact information
    private static final String CONTACT_EMAIL = "support@healthdietapp.com";
    private static final String CONTACT_PHONE = "+1-800-123-4567";
    private static final String FACEBOOK_URL = "https://www.facebook.com/healthdietapp";
    private static final String TWITTER_URL = "https://www.twitter.com/healthdietapp";
    private static final String INSTAGRAM_URL = "https://www.instagram.com/healthdietapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        initializeViews();
        setupListeners();
        displayContactInfo();

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText("联系我们");
        }

        Button backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void initializeViews() {
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        emailButton = findViewById(R.id.emailButton);
        phoneButton = findViewById(R.id.phoneButton);
        facebookButton = findViewById(R.id.facebookButton);
        twitterButton = findViewById(R.id.twitterButton);
        instagramButton = findViewById(R.id.instagramButton);
    }

    private void setupListeners() {
        emailButton.setOnClickListener(v -> openEmail());
        phoneButton.setOnClickListener(v -> openPhone());
        facebookButton.setOnClickListener(v -> openSocialMedia(FACEBOOK_URL));
        twitterButton.setOnClickListener(v -> openSocialMedia(TWITTER_URL));
        instagramButton.setOnClickListener(v -> openSocialMedia(INSTAGRAM_URL));
    }

    private void displayContactInfo() {
        emailText.setText(CONTACT_EMAIL);
        phoneText.setText(CONTACT_PHONE);
    }

    private void openEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + CONTACT_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Health Diet App Support");
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + CONTACT_PHONE));
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSocialMedia(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }
}
