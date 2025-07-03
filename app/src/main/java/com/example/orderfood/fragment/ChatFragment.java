package com.example.orderfood.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.activity.SearchMessagesActivity;
import com.example.orderfood.adapter.ConversationAdapter;
import com.example.orderfood.model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList = new ArrayList<>();
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = -1;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sessionPrefs = requireActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        dbHelper = new DataBaseOpenHelper(requireContext());
        recyclerView = view.findViewById(R.id.recycler_view_conversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.search_view_messages);

        adapter = new ConversationAdapter(conversationList);
        recyclerView.setAdapter(adapter);

        setupSearch();

        return view;
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    Intent intent = new Intent(getActivity(), SearchMessagesActivity.class);
                    intent.putExtra("QUERY", query.trim());
                    startActivity(intent);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        List<Conversation> loadedConversations = dbHelper.getConversations(currentUserId);
        conversationList.clear();
        conversationList.addAll(loadedConversations);
        adapter.notifyDataSetChanged();
    }
} 