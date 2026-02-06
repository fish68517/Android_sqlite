package com.example.healthdietapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.activities.CreatePostActivity;
import com.example.healthdietapp.activities.PostDetailActivity;
import com.example.healthdietapp.activities.ToolsActivity;
import com.example.healthdietapp.adapters.PostAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Discover Fragment - Displays community posts and tools
 */
public class DiscoverFragment extends Fragment {

    private EditText postSearchBar;
    private Button postSearchButton;
    private Button publishPostButton;
    private LinearLayout foodRankingCard;
    private LinearLayout whatToEatCard;
    private LinearLayout foodWeightCard;
    private LinearLayout dailyQACard;
    private RecyclerView recommendedPostsRecyclerView;

    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private SessionManager sessionManager;
    private PostAdapter postAdapter;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeDatabase();
        setupRecyclerView();
        setupSearchFunctionality();
        setupToolsCards();
        setupPublishButton();
        loadRecommendedPosts();
    }

    private void initializeViews(View view) {
        postSearchBar = view.findViewById(R.id.postSearchBar);
        postSearchButton = view.findViewById(R.id.postSearchButton);
        publishPostButton = view.findViewById(R.id.publishPostButton);
        foodRankingCard = view.findViewById(R.id.foodRankingCard);
        whatToEatCard = view.findViewById(R.id.whatToEatCard);
        foodWeightCard = view.findViewById(R.id.foodWeightCard);
        dailyQACard = view.findViewById(R.id.dailyQACard);
        recommendedPostsRecyclerView = view.findViewById(R.id.recommendedPostsRecyclerView);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        postDAO = new PostDAO(dbHelper);
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        recommendedPostsRecyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(new ArrayList<>(), post -> {
            // Navigate to post detail page
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getPostId());
            startActivity(intent);
        });
        recommendedPostsRecyclerView.setAdapter(postAdapter);
    }

    private void setupSearchFunctionality() {
        postSearchButton.setOnClickListener(v -> performPostSearch());
        postSearchBar.setOnEditorActionListener((v, actionId, event) -> {
            performPostSearch();
            return true;
        });
    }

    private void performPostSearch() {
        String keyword = postSearchBar.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a search keyword", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                List<Post> searchResults = postDAO.searchPosts(keyword);
                requireActivity().runOnUiThread(() -> {
                    postAdapter.updatePosts(searchResults);
                    if (searchResults.isEmpty()) {
                        Toast.makeText(requireContext(), "No posts found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Search failed, please try again", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void setupToolsCards() {
        foodRankingCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ToolsActivity.class);
            startActivity(intent);
        });

        whatToEatCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ToolsActivity.class);
            startActivity(intent);
        });

        foodWeightCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ToolsActivity.class);
            startActivity(intent);
        });

        dailyQACard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ToolsActivity.class);
            startActivity(intent);
        });
    }

    private void setupPublishButton() {
        publishPostButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreatePostActivity.class);
            startActivity(intent);
        });
    }

    private void loadRecommendedPosts() {
        new Thread(() -> {
            try {
                List<Post> recommendedPosts = postDAO.getRecommendedPosts(10);
                requireActivity().runOnUiThread(() -> {
                    postAdapter.updatePosts(recommendedPosts);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
