package com.example.orderfood.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.GroupChatAdapter;
import com.example.orderfood.model.Message;
import com.example.orderfood.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupChatActivity extends AppCompatActivity implements GroupChatAdapter.OnMessageLongClickListener {

    private RecyclerView recyclerView;
    private GroupChatAdapter adapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private Button sendButton;
    private DataBaseOpenHelper dbHelper;

    private int currentUserId = -1;
    private long groupId;
    private String groupName;
    private Map<Integer, User> membersMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupId = getIntent().getLongExtra("GROUP_ID", -1);
        groupName = getIntent().getStringExtra("GROUP_NAME");

        if (groupId == -1 || groupName == null) {
            Toast.makeText(this, "无效的群组", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(groupName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);

        dbHelper = new DataBaseOpenHelper(this);

        recyclerView = findViewById(R.id.recycler_view_messages);
        messageEditText = findViewById(R.id.et_message);
        sendButton = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        membersMap = new HashMap<>();
        loadGroupMembers();
        loadMessages();

        adapter = new GroupChatAdapter(messageList, currentUserId, membersMap, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() > 0 ? adapter.getItemCount() - 1 : 0);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_group_settings) {
            Intent intent = new Intent(this, GroupSettingsActivity.class);
            intent.putExtra("GROUP_ID", groupId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadGroupMembers() {
        List<User> members = dbHelper.getGroupMembers(groupId);
        membersMap = members.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getGroupMessages((int)groupId, currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID)));
                message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID)));
                message.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP)));
                boolean isRetracted = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_IS_RETRACTED)) == 1;
                message.setRetracted(isRetracted);

                if (isRetracted) {
                    User sender = membersMap.get(message.getSenderId());
                    String senderName = (sender != null && sender.getId() != currentUserId) ? sender.getNickname() : "你";
                    message.setContent(senderName + " 撤回了一条消息");
                } else {
                    message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT)));
                }
                messageList.add(message);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void sendMessage() {
        String content = messageEditText.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        dbHelper.addGroupMessage(currentUserId, (int)groupId, content);
        messageEditText.setText("");

        loadMessages();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.getItemCount() > 0 ? adapter.getItemCount() - 1 : 0);
    }

    @Override
    public void onMessageLongClicked(Message message, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> options = new ArrayList<>();

        options.add("删除本条消息");
        if (message.getSenderId() == currentUserId) {
            options.add("撤回");
        }

        builder.setItems(options.toArray(new String[0]), (dialog, which) -> {
            String selectedOption = options.get(which);
            if (selectedOption.equals("删除本条消息")) {
                deleteMessageForMe(message, position);
            } else if (selectedOption.equals("撤回")) {
                retractMessage(message, position);
            }
        });

        builder.show();
    }

    private void deleteMessageForMe(Message message, int position) {
        dbHelper.deleteMessageForMe(message.getId(), currentUserId);
        messageList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, messageList.size());
        Toast.makeText(this, "消息已删除", Toast.LENGTH_SHORT).show();
    }

    private void retractMessage(Message message, int position) {
        dbHelper.retractMessage(message.getId());
        loadMessages();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "消息已撤回", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 