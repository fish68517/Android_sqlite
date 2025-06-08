package com.archive.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.archive.app.db.OpenHelperDataBase;
import com.example.myapplication.R;

public class HomeFragment extends Fragment {

    private TextView tvTotalBooks;
    private TextView tvTotalCategories;
    private OpenHelperDataBase dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new OpenHelperDataBase(getContext());
        tvTotalBooks = view.findViewById(R.id.tv_total_books);
        tvTotalCategories = view.findViewById(R.id.tv_total_categories);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void loadStatistics() {
        int totalBooks = dbHelper.getTotalBookCount();
        int totalCategories = dbHelper.getTotalCategoryCount();

        tvTotalBooks.setText(String.valueOf(totalBooks));
        tvTotalCategories.setText(String.valueOf(totalCategories));
    }
} 