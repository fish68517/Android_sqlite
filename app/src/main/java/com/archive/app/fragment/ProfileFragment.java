package com.archive.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.archive.app.MyApplication;
import com.archive.app.activity.LoginActivity;
import com.archive.app.activity.RecycleBinActivity;
import com.archive.app.model.User;
import com.archive.app.mvp.ProfileContract;
import com.archive.app.mvp.ProfilePresenter;
import com.example.myapplication.R;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter presenter;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView notesCountTextView;
    private TextView categoriesCountTextView;
    private Button recycleBinButton;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new ProfilePresenter(getContext());
        presenter.attachView(this);

        usernameTextView = view.findViewById(R.id.profile_username);
        passwordTextView = view.findViewById(R.id.profile_password);
        notesCountTextView = view.findViewById(R.id.notes_count_text);
        categoriesCountTextView = view.findViewById(R.id.categories_count_text);
        recycleBinButton = view.findViewById(R.id.recycle_bin_button);
        logoutButton = view.findViewById(R.id.logout_button);

        recycleBinButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecycleBinActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> presenter.logout());
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadProfileData(getUserId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showProfileInfo(User user) {
        if(user != null){
            usernameTextView.setText(user.getUsername());
            // For security, never display the raw password.
            passwordTextView.setText("密码: ********");
        }
    }

    @Override
    public void showStatistics(int noteCount, int categoryCount) {
        notesCountTextView.setText(String.valueOf(noteCount));
        categoriesCountTextView.setText(String.valueOf(categoryCount));
    }

    @Override
    public void navigateToLogin() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public long getUserId() {
        if (MyApplication.curUser != null) {
            return MyApplication.curUser.getId();
        }
        return -1; // Should not happen if user is logged in
    }
}