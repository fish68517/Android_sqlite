package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.UserSearchAdapter;
import com.example.orderfood.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends AppCompatActivity implements UserSearchAdapter.OnAddContactListener {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private UserSearchAdapter adapter;
    private List<User> searchResults;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = 1; // This should be dynamically set based on logged in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        dbHelper = new DataBaseOpenHelper(this);

        searchEditText = findViewById(R.id.et_search_username);
        searchButton = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.recycler_view_search_results);

        searchResults = new ArrayList<>();
        adapter = new UserSearchAdapter(searchResults, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            } else {
                Toast.makeText(AddContactActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        List<User> users = dbHelper.searchUsers(query, currentUserId);
        searchResults.clear();
        searchResults.addAll(users);
        adapter.notifyDataSetChanged();

        if (users.isEmpty()) {
            Toast.makeText(this, "未找到用户", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddContact(User user) {
        dbHelper.addContact(currentUserId, user.getId());
        Toast.makeText(this, "已添加 " + user.getNickname() + " 为联系人", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK); // Notify ContactsFragment to refresh
    }
} 