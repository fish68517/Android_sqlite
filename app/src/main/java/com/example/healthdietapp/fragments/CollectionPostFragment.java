package com.example.healthdietapp.fragments;

import android.content.Intent;
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
import com.example.healthdietapp.activities.PostDetailActivity;
import com.example.healthdietapp.adapters.CollectionPostAdapter;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.Post;

import java.util.List;

/**
 * CollectionPostFragment - Displays user's collected posts
 */
public class CollectionPostFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_POST_DAO = "postDAO";

    private String userId;
    private PostDAO postDAO;
    private RecyclerView postsRecyclerView;
    private CollectionPostAdapter adapter;

    public static CollectionPostFragment newInstance(String userId, PostDAO postDAO) {
        CollectionPostFragment fragment = new CollectionPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        fragment.postDAO = postDAO;
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
        return inflater.inflate(R.layout.fragment_collection_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        setupRecyclerView();
        loadCollectedPosts();
    }

    private void setupRecyclerView() {
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionPostAdapter(post -> onPostClick(post));
        postsRecyclerView.setAdapter(adapter);
    }

    private void loadCollectedPosts() {
        new Thread(() -> {
            try {
                List<Post> posts = postDAO.getUserCollectedPosts(userId);
                getActivity().runOnUiThread(() -> {
                    if (posts != null && !posts.isEmpty()) {
                        adapter.updatePosts(posts);
                    } else {
                        Toast.makeText(getContext(), "No collected posts", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onPostClick(Post post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra("postId", post.getPostId());
        startActivity(intent);
    }
}
