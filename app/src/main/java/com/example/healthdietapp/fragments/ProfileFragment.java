package com.example.healthdietapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.healthdietapp.R;
import com.example.healthdietapp.activities.AccountManagementActivity;
import com.example.healthdietapp.activities.ContactUsActivity;
import com.example.healthdietapp.activities.EditProfileActivity;
import com.example.healthdietapp.activities.FeedbackActivity;
import com.example.healthdietapp.activities.LoginActivity;
import com.example.healthdietapp.activities.MyPostsActivity;
import com.example.healthdietapp.activities.MyCollectionsActivity;
import com.example.healthdietapp.activities.MyFollowingActivity;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.SessionManager;

/**
 * Profile Fragment - Displays user profile and account options
 * Shows user avatar, nickname, and provides access to profile management features
 */
public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private UserDAO userDAO;
    
    private ImageView userAvatar;
    private TextView userNickname;
    private TextView userUsername;
    private Button editProfileButton;
    private Button myPostsButton;
    private Button myFollowingButton;
    private Button myCollectionsButton;
    private Button accountManagementButton;
    private Button feedbackButton;
    private Button contactUsButton;
    private Button logoutButton;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeManagers();
        initializeViews(view);
        loadUserInfo();
        setupButtonListeners();
    }

    private void initializeManagers() {
        sessionManager = new SessionManager(requireContext());
        dbHelper = new DatabaseHelper(requireContext());
        userDAO = new UserDAO(dbHelper);
        userId = sessionManager.getUserId();
    }

    private void initializeViews(View view) {
        userAvatar = view.findViewById(R.id.userAvatar);
        userNickname = view.findViewById(R.id.userNickname);
        userUsername = view.findViewById(R.id.userUsername);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        myPostsButton = view.findViewById(R.id.myPostsButton);
        myFollowingButton = view.findViewById(R.id.myFollowingButton);
        myCollectionsButton = view.findViewById(R.id.myCollectionsButton);
        accountManagementButton = view.findViewById(R.id.accountManagementButton);
        feedbackButton = view.findViewById(R.id.feedbackButton);
        contactUsButton = view.findViewById(R.id.contactUsButton);
        logoutButton = view.findViewById(R.id.logout_button);
    }

    private void loadUserInfo() {
        new Thread(() -> {
            try {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    requireActivity().runOnUiThread(() -> {
                        displayUserInfo(user);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error loading user info", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void displayUserInfo(User user) {
        if (user.getNickname() != null && !user.getNickname().isEmpty()) {
            userNickname.setText(user.getNickname());
        } else {
            userNickname.setText(user.getUsername());
        }
        
        if (user.getUsername() != null) {
            userUsername.setText("@" + user.getUsername());
        }
        
        // TODO: Load avatar image from URL if available
        // For now, use default avatar
    }

    private void setupButtonListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        myPostsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyPostsActivity.class);
            startActivity(intent);
        });

        myFollowingButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyFollowingActivity.class);
            startActivity(intent);
        });

        myCollectionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyCollectionsActivity.class);
            startActivity(intent);
        });

        accountManagementButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AccountManagementActivity.class);
            startActivity(intent);
        });

        feedbackButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FeedbackActivity.class);
            startActivity(intent);
        });

        contactUsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ContactUsActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        // Clear login session
        sessionManager.logout();
        
        // Show logout success message
        Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        
        // Navigate to LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
