package com.example.orderfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private RecyclerView recyclerView;
    private UserSearchAdapter adapter;
    private List<User> searchResults;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = -1; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("添加联系人");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "用户状态异常, 请重新登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DataBaseOpenHelper(this);

        searchEditText = findViewById(R.id.et_search_username);
        recyclerView = findViewById(R.id.recycler_view_search_results);

        searchResults = new ArrayList<>();
        adapter = new UserSearchAdapter(searchResults, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        performSearch("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void performSearch(String query) {
        List<User> users = dbHelper.searchUsers(query, currentUserId);
        System.out.println("users: " + users.size());
        searchResults.clear();
        searchResults.addAll(users);
        adapter.notifyDataSetChanged();

        if (users.isEmpty() && query.isEmpty()) {
            Toast.makeText(this, "没有其他可添加的用户", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddContact(User user) {
        dbHelper.addContact(currentUserId, user.getId());
        Toast.makeText(this, "已添加 " + user.getNickname() + " 为联系人", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK); // Notify ContactsFragment to refresh
        
        // performSearch(searchEditText.getText().toString().trim());
    }
} 