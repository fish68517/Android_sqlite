package com.example.orderfood.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

    private int currentUserId = 1; // Assume user ID is 1
    private int contactId;
    private String contactNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        contactNickname = getIntent().getStringExtra("CONTACT_NICKNAME");

        if (contactId == -1 || contactNickname == null) {
            Toast.makeText(this, "无效的联系人", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(contactNickname);

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

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getMessages(currentUserId, contactId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID)));
                message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID)));
                message.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_RECEIVER_ID)));
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

        dbHelper.addDirectMessage(currentUserId, contactId, content);

        Message message = new Message();
        message.setSenderId(currentUserId);
        message.setReceiverId(contactId);
        message.setContent(content);
        // We can get the timestamp from DB again, or just add it here for UI
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
        // Only sender can retract a message
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