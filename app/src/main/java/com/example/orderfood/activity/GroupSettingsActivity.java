package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.GroupMembersAdapter;
import com.example.orderfood.model.Group;
import com.example.orderfood.model.User;

import java.util.List;

public class GroupSettingsActivity extends AppCompatActivity implements GroupMembersAdapter.OnRemoveMemberClickListener {

    private static final int ADD_MEMBER_REQUEST = 1;

    private TextView groupNameTextView;
    private RecyclerView membersRecyclerView;
    private Button addMembersButton;
    private GroupMembersAdapter adapter;
    private DataBaseOpenHelper dbHelper;

    private long groupId;
    private Group currentGroup;
    private List<User> members;
    private int currentUserId = 1; // Assume current user ID is 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        setTitle("群聊设置");

        groupId = getIntent().getLongExtra("GROUP_ID", -1);
        if (groupId == -1) {
            Toast.makeText(this, "无效的群组", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DataBaseOpenHelper(this);
        groupNameTextView = findViewById(R.id.tv_group_name);
        membersRecyclerView = findViewById(R.id.recycler_view_group_members);
        addMembersButton = findViewById(R.id.btn_add_members);
        
        loadGroupDetails();
        setupListeners();
    }

    private void loadGroupDetails() {
        // This is not efficient, should get group details in one query
        members = dbHelper.getGroupMembers(groupId); 
        // A placeholder method to get a single group would be better
        dbHelper.getGroupsForUser(currentUserId).stream()
                .filter(g -> g.getId() == groupId)
                .findFirst()
                .ifPresent(g -> currentGroup = g);

        if (currentGroup == null) {
             Toast.makeText(this, "无法加载群组信息", Toast.LENGTH_SHORT).show();
             finish();
             return;
        }

        groupNameTextView.setText(currentGroup.getName());
        adapter = new GroupMembersAdapter(members, currentGroup.getCreatorId(), currentUserId, this);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(adapter);

        // Only creator can edit group
        if (currentUserId != currentGroup.getCreatorId()) {
            addMembersButton.setVisibility(View.GONE);
            findViewById(R.id.layout_group_name).setClickable(false);
        }
    }

    private void setupListeners() {
        findViewById(R.id.layout_group_name).setOnClickListener(v -> showEditGroupNameDialog());
        addMembersButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMembersActivity.class);
            intent.putExtra("GROUP_ID", groupId);
            startActivityForResult(intent, ADD_MEMBER_REQUEST);
        });
    }

    private void showEditGroupNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改群聊名称");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentGroup.getName());
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(currentGroup.getName())) {
                dbHelper.updateGroupName(groupId, newName);
                groupNameTextView.setText(newName);
                Toast.makeText(this, "群聊名称已更新", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    @Override
    public void onRemoveMemberClick(User member) {
        new AlertDialog.Builder(this)
                .setTitle("移除成员")
                .setMessage("确定要将 " + member.getNickname() + " 移出群聊吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    dbHelper.removeGroupMember(groupId, member.getId());
                    loadGroupDetails(); // Refresh the list
                    Toast.makeText(this, member.getNickname() + " 已被移出群聊", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MEMBER_REQUEST && resultCode == RESULT_OK) {
            loadGroupDetails(); // Refresh member list
        }
    }
} 