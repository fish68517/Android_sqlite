package com.example.orderfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;

import com.example.orderfood.adapter.SelectableContactsAdapter;
import com.example.orderfood.model.User;

import java.util.List;

public class AddMembersActivity extends AppCompatActivity {

    private RecyclerView rvSelectableContacts;
    private Button btnAddMembers;
    private SelectableContactsAdapter adapter;
    private DataBaseOpenHelper dbHelper;

    private int currentUserId = -1;
    private long groupId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("添加群成员");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
        groupId = getIntent().getLongExtra("GROUP_ID", -1);

        if (currentUserId == -1 || groupId == -1L) {
            Toast.makeText(this, "数据错误，无法添加成员", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DataBaseOpenHelper(this);

        rvSelectableContacts = findViewById(R.id.recycler_view_add_members);
        btnAddMembers = findViewById(R.id.btn_add_selected_members);

        setupRecyclerView();

        btnAddMembers.setOnClickListener(v -> addSelectedMembers());
    }

    private void setupRecyclerView() {
        List<User> contactsNotInGroup = dbHelper.getContactsNotInGroup(currentUserId, groupId);
        if (contactsNotInGroup.isEmpty()) {
            Toast.makeText(this, "没有可添加的联系人", Toast.LENGTH_SHORT).show();
        }
        adapter = new SelectableContactsAdapter(contactsNotInGroup);
        rvSelectableContacts.setLayoutManager(new LinearLayoutManager(this));
        rvSelectableContacts.setAdapter(adapter);
    }

    private void addSelectedMembers() {
        List<Integer> selectedMemberIds = adapter.getSelectedContactIds();
        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "请至少选择一个联系人", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addGroupMembers(groupId, selectedMemberIds);
        Toast.makeText(this, "成员添加成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 