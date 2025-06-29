package com.example.orderfood.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.GroupMemberAdapter;
import com.example.orderfood.model.Group;
import com.example.orderfood.model.User;

import java.util.ArrayList;
import java.util.List;

public class GroupSettingsActivity extends AppCompatActivity implements GroupMemberAdapter.OnRemoveMemberClickListener {

    private RecyclerView rvMembers;
    private Button btnAddMembers, btnLeaveGroup;
    private TextView tvGroupName;

    private DataBaseOpenHelper dbHelper;
    private GroupMemberAdapter adapter;
    private List<User> memberList = new ArrayList<>();
    private Group currentGroup;

    private int currentUserId = -1;
    private long groupId = -1;

    private final ActivityResultLauncher<Intent> addMembersLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshMemberList();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        setTitle("群聊设置");

        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
        groupId = getIntent().getLongExtra("GROUP_ID", -1);

        if (currentUserId == -1 || groupId == -1) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DataBaseOpenHelper(this);
        initViews();
        loadGroupDetails();
        setupListeners();
    }

    private void initViews() {
        rvMembers = findViewById(R.id.recycler_view_group_members);
        btnAddMembers = findViewById(R.id.btn_add_group_members);
        btnLeaveGroup = findViewById(R.id.btn_leave_group);
        tvGroupName = findViewById(R.id.tv_group_name);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadGroupDetails() {
        currentGroup = dbHelper.getGroupDetails(groupId);
        if (currentGroup == null) {
            Toast.makeText(this, "无法加载群组信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvGroupName.setText(currentGroup.getName());
        if (currentUserId == currentGroup.getCreatorId()) {
            btnLeaveGroup.setText("解散群聊");
        }
        refreshMemberList();
    }

    private void refreshMemberList() {
        memberList.clear();
        memberList.addAll(dbHelper.getGroupMembers(groupId));
        if (adapter == null) {
            adapter = new GroupMemberAdapter(memberList, currentUserId, currentGroup.getCreatorId(), this);
            rvMembers.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupListeners() {
        btnAddMembers.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMembersActivity.class);
            intent.putExtra("GROUP_ID", groupId);
            addMembersLauncher.launch(intent);
        });

        btnLeaveGroup.setOnClickListener(v -> showLeaveGroupConfirmation());
    }

    private void showLeaveGroupConfirmation() {
        boolean isCreator = currentUserId == currentGroup.getCreatorId();
        new AlertDialog.Builder(this)
                .setTitle(isCreator ? "解散群聊" : "退出群聊")
                .setMessage(isCreator ? "确定要解散该群聊吗？此操作不可恢复。" : "确定要退出该群聊吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    if (isCreator) {
                        // You might want to delete all messages as well
                        // dbHelper.deleteAllGroupMessages(groupId);
                        dbHelper.deleteGroup(groupId);
                        Toast.makeText(this, "群聊已解散", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.removeGroupMember(groupId, currentUserId);
                        Toast.makeText(this, "已退出群聊", Toast.LENGTH_SHORT).show();
                    }
                    // Notify previous activities and finish
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onRemoveMember(User member) {
        new AlertDialog.Builder(this)
                .setTitle("移除成员")
                .setMessage("确定要将 " + member.getNickname() + " 移出群聊吗？")
                .setPositiveButton("移除", (dialog, which) -> {
                    dbHelper.removeGroupMember(groupId, member.getId());
                    Toast.makeText(this, member.getNickname() + " 已被移出群聊", Toast.LENGTH_SHORT).show();
                    refreshMemberList();
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 