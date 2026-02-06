package com.example.healthdietapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.adapters.CollectionQuestionAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.models.HealthQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * CollectionQuestionFragment - Displays user's collected health questions
 */
public class CollectionQuestionFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";

    private String userId;
    private RecyclerView questionsRecyclerView;
    private CollectionQuestionAdapter adapter;

    public static CollectionQuestionFragment newInstance(String userId) {
        CollectionQuestionFragment fragment = new CollectionQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionsRecyclerView = view.findViewById(R.id.questionsRecyclerView);
        setupRecyclerView();
        loadCollectedQuestions();
    }

    private void setupRecyclerView() {
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionQuestionAdapter();
        questionsRecyclerView.setAdapter(adapter);
    }

    private void loadCollectedQuestions() {
        // For now, display a placeholder message
        // In a full implementation, this would load questions from a database table
        // that tracks which questions the user has collected/bookmarked
        Toast.makeText(getContext(), "Question collections feature coming soon", Toast.LENGTH_SHORT).show();
    }
}
