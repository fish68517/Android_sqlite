package com.example.orderfood.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.SelectableContactsAdapter;
import com.example.orderfood.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class AddMembersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button confirmButton;
    private SelectableContactsAdapter adapter;
    private DataBaseOpenHelper dbHelper;

    private long groupId;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        setTitle("添加成员");

        groupId = getIntent().getLongExtra("GROUP_ID", -1);
        if (groupId == -1) {
            Toast.makeText(this, "无效的群组", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DataBaseOpenHelper(this);
        recyclerView = findViewById(R.id.recycler_view_add_members);
        confirmButton = findViewById(R.id.btn_confirm_add_members);

        List<User> addableContacts = dbHelper.getContactsNotInGroup(currentUserId, groupId);
        adapter = new SelectableContactsAdapter(addableContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        confirmButton.setOnClickListener(v -> addSelectedMembers());
    }

    private void addSelectedMembers() {
        List<User> selectedContacts = adapter.getSelectedContacts();
        if (selectedContacts.isEmpty()) {
            Toast.makeText(this, "请选择要添加的联系人", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> memberIds = selectedContacts.stream().map(User::getId).collect(Collectors.toList());
        dbHelper.addGroupMembers(groupId, memberIds);

        Toast.makeText(this, "成员已添加", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
} 