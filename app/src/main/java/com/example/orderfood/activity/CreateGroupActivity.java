package com.example.orderfood.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private RecyclerView contactsRecyclerView;
    private Button createGroupButton;
    private SelectableContactsAdapter adapter;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = 1; // Assume current user ID is 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setTitle("创建群聊");

        dbHelper = new DataBaseOpenHelper(this);

        groupNameEditText = findViewById(R.id.et_group_name);
        contactsRecyclerView = findViewById(R.id.recycler_view_contacts_selection);
        createGroupButton = findViewById(R.id.btn_create_group);

        List<User> contacts = dbHelper.getContacts(currentUserId);
        adapter = new SelectableContactsAdapter(contacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setAdapter(adapter);

        createGroupButton.setOnClickListener(v -> createGroup());
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "请输入群聊名称", Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> selectedContacts = adapter.getSelectedContacts();
        if (selectedContacts.size() < 2) {
            Toast.makeText(this, "请至少选择2位联系人", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> memberIds = selectedContacts.stream().map(User::getId).collect(Collectors.toList());

        long groupId = dbHelper.createGroup(groupName, currentUserId, memberIds);

        if (groupId != -1) {
            Toast.makeText(this, "群聊 '" + groupName + "' 创建成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "创建群聊失败", Toast.LENGTH_SHORT).show();
        }
    }
} 