package com.example.orderfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.example.orderfood.adapter.ChatAdapter;
import com.example.orderfood.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnMessageLongClickListener {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private Button sendButton;
    private DataBaseOpenHelper dbHelper;

    private int currentUserId = -1;
    private int contactId = -1;
    private String contactNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        contactNickname = getIntent().getStringExtra("CONTACT_NICKNAME");

        if (contactId == -1 || contactNickname == null || currentUserId == -1) {
            Toast.makeText(this, "无效的会话", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(contactNickname);
        }

        dbHelper = new DataBaseOpenHelper(this);

        recyclerView = findViewById(R.id.recycler_view_messages);
        messageEditText = findViewById(R.id.et_message);
        sendButton = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        loadMessages();

        adapter = new ChatAdapter(messageList, currentUserId, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getMessages(currentUserId, contactId);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Message message = new Message();
                        message.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID)));
                        message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID)));
                        message.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_RECEIVER_ID)));
                        message.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP)));
                        boolean isRetracted = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_IS_RETRACTED)) == 1;
                        message.setRetracted(isRetracted);
                        if (isRetracted) {
                            if (message.getSenderId() == currentUserId) {
                                message.setContent("你撤回了一条消息");
                            } else {
                                message.setContent("对方撤回了一条消息");
                            }
                        } else {
                            message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT)));
                        }
                        messageList.add(message);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void sendMessage() {
        String content = messageEditText.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        dbHelper.addDirectMessage(currentUserId, contactId, content);
        messageEditText.setText("");

        loadMessages();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.getItemCount() > 0 ? adapter.getItemCount() - 1 : 0);
    }

    @Override
    public void onMessageLongClicked(Message message, int position) {
        List<String> options = new ArrayList<>();
        options.add("删除本条消息");
        if (message.getSenderId() == currentUserId) {
            options.add("撤回");
        }

        new AlertDialog.Builder(this)
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    switch (options.get(which)) {
                        case "删除本条消息":
                            deleteMessageForMe(message, position);
                            break;
                        case "撤回":
                            retractMessage(message, position);
                            break;
                    }
                })
                .show();
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