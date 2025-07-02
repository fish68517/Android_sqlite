package com.example.orderfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    private int currentUserId = -1; // Default to an invalid ID
    private int contactId;
    private String contactNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get current user ID from SharedPreferences
        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        contactNickname = getIntent().getStringExtra("CONTACT_NICKNAME");

        if (contactId == -1 || contactNickname == null || currentUserId == -1) {
            Toast.makeText(this, "无效的会话", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(contactNickname);

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
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getMessages(currentUserId, contactId);
        if (cursor != null && cursor.moveToFirst()) {
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
            cursor.close();
        }
    }

    private void sendMessage() {
        String content = messageEditText.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        dbHelper.addDirectMessage(currentUserId, contactId, content);
        messageEditText.setText("");

        // Reload messages to get the new message with correct ID and timestamp from DB
        int oldSize = messageList.size();
        loadMessages();
        adapter.notifyDataSetChanged(); // Use notifyDataSetChanged as positions might shift
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onMessageLongClicked(Message message, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> options = new ArrayList<>();

        options.add("删除本条消息");
        // Only sender can retract a message
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
        // Update the message in the list to show it's retracted
        Message retractedMessage = messageList.get(position);
        retractedMessage.setRetracted(true);
        if(retractedMessage.getSenderId() == currentUserId){
            retractedMessage.setContent("你撤回了一条消息");
        } else {
            retractedMessage.setContent("对方撤回了一条消息");
        }
        adapter.notifyItemChanged(position);
        Toast.makeText(this, "消息已撤回", Toast.LENGTH_SHORT).show();
    }
} 