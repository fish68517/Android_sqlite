package com.example.orderfood.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.ConversationAdapter;
import com.example.orderfood.model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = -1;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sessionPrefs = getActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        dbHelper = new DataBaseOpenHelper(getContext());
        recyclerView = view.findViewById(R.id.recycler_view_conversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        conversationList = new ArrayList<>();
        adapter = new ConversationAdapter(conversationList);
        recyclerView.setAdapter(adapter);

        return view;
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
