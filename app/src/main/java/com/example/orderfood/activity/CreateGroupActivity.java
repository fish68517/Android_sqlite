package com.example.orderfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.SelectableContactsAdapter;
import com.example.orderfood.model.User;

import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText etGroupName;
    private RecyclerView rvContacts;
    private Button btnCreate;
    private SelectableContactsAdapter adapter;
    private DataBaseOpenHelper dbHelper;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("创建群聊");
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

        etGroupName = findViewById(R.id.et_group_name);
        rvContacts = findViewById(R.id.recycler_view_selectable_contacts);
        btnCreate = findViewById(R.id.btn_create);

        setupRecyclerView();

        btnCreate.setOnClickListener(v -> createGroup());
    }

    private void setupRecyclerView() {
        List<User> contacts = dbHelper.getContacts(currentUserId);
        adapter = new SelectableContactsAdapter(contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);
    }

    private void createGroup() {
        List<Integer> selectedMemberIds = adapter.getSelectedContactIds();
        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "请至少选择一个群成员", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an EditText for the dialog
        final EditText input = new EditText(this);
        input.setHint("输入群聊名称");

        // dialog to create group name
        new AlertDialog.Builder(this)
                .setTitle("群聊名称")
                .setView(input) // Add EditText to dialog
                .setPositiveButton("确定", (dialog, which) -> {
                    String groupName = input.getText().toString().trim();
                    if (groupName.isEmpty()) {
                        Toast.makeText(CreateGroupActivity.this, "群聊名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    long groupId = dbHelper.createGroup(groupName, currentUserId, selectedMemberIds);

                    if (groupId != -1) {
                        Toast.makeText(this, "群聊 '" + groupName + "' 创建成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // To notify ContactsFragment to refresh
                        finish();
                    } else {
                        Toast.makeText(this, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 