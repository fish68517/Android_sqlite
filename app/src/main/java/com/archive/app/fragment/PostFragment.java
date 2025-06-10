package com.archive.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.archive.app.adapter.PostAdapter;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Post;
import com.example.myapplication.R;
import java.util.List;

public class PostFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private OpenHelperDataBase dbHelper;
    private List<Post> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new OpenHelperDataBase(getContext());
        postList = dbHelper.getAllPosts();

        adapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(adapter);

        return view;
    }
} 