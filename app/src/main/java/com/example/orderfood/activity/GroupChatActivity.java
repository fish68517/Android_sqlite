package com.example.orderfood.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupChatActivity extends AppCompatActivity implements GroupChatAdapter.OnMessageLongClickListener {

    private RecyclerView recyclerView;
    private GroupChatAdapter adapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private Button sendButton;
    private DataBaseOpenHelper dbHelper;

    private int currentUserId = 1; // Assume user ID is 1
    private long groupId;
    private String groupName;
    private Map<Integer, String> memberNicknames;


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

        setTitle(groupName);

        dbHelper = new DataBaseOpenHelper(this);

        recyclerView = findViewById(R.id.recycler_view_messages);
        messageEditText = findViewById(R.id.et_message);
        sendButton = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        loadMemberNicknames();
        loadMessages();

        adapter = new GroupChatAdapter(messageList, currentUserId, memberNicknames, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_group_settings) {
            Intent intent = new Intent(this, GroupSettingsActivity.class);
            intent.putExtra("GROUP_ID", groupId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMemberNicknames() {
        List<User> members = dbHelper.getGroupMembers(groupId);
        memberNicknames = members.stream().collect(Collectors.toMap(User::getId, User::getNickname));
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getGroupMessages((int)groupId, currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID)));
                message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID)));
                // receiverId and groupId might be null depending on message type, handle this
                message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT)));
                message.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP)));
                message.setRetracted(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_IS_RETRACTED)) == 1);
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

        Message message = new Message();
        message.setSenderId(currentUserId);
        message.setContent(content);
        messageList.add(message);

        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        messageEditText.setText("");
    }

    @Override
    public void onMessageLongClicked(Message message, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> options = new ArrayList<>();

        options.add("为我删除");
        if (message.getSenderId() == currentUserId) {
            options.add("撤回");
        }

        builder.setItems(options.toArray(new String[0]), (dialog, which) -> {
            String selectedOption = options.get(which);
            if (selectedOption.equals("为我删除")) {
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
        Message retractedMessage = messageList.get(position);
        retractedMessage.setRetracted(true);
        if (retractedMessage.getSenderId() == currentUserId) {
            retractedMessage.setContent("你撤回了一条消息");
        } else {
            retractedMessage.setContent("对方撤回了一条消息");
        }
        adapter.notifyItemChanged(position);
        Toast.makeText(this, "消息已撤回", Toast.LENGTH_SHORT).show();
    }
} 