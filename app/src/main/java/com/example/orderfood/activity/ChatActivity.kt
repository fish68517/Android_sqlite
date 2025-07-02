package com.example.orderfood.activity

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.adapter.ChatAdapter
import com.example.orderfood.model.Message

class ChatActivity : AppCompatActivity(), ChatAdapter.OnMessageLongClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var dbHelper: DataBaseOpenHelper

    private var currentUserId = -1
    private var contactId = -1
    private var contactNickname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)

        contactId = intent.getIntExtra("CONTACT_ID", -1)
        contactNickname = intent.getStringExtra("CONTACT_NICKNAME")

        if (contactId == -1 || contactNickname == null || currentUserId == -1) {
            Toast.makeText(this, "无效的会话", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.title = contactNickname

        dbHelper = DataBaseOpenHelper(this)

        recyclerView = findViewById(R.id.recycler_view_messages)
        messageEditText = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send)

        messageList = ArrayList()
        loadMessages()

        adapter = ChatAdapter(messageList, currentUserId, this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(adapter.itemCount - 1)

        sendButton.setOnClickListener { sendMessage() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadMessages() {
        messageList.clear()
        val cursor = dbHelper.getMessages(currentUserId, contactId)
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val message = Message().apply {
                        id = it.getLong(it.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID))
                        senderId = it.getInt(it.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID))
                        receiverId = it.getInt(it.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_RECEIVER_ID))
                        timestamp = it.getString(it.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP))
                        isRetracted = it.getInt(it.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_IS_RETRACTED)) == 1
                        content = if (isRetracted) {
                            if (senderId == currentUserId) "你撤回了一条消息" else "对方撤回了一条消息"
                        } else {
                            it.getString(it.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT))
                        }
                    }
                    messageList.add(message)
                } while (it.moveToNext())
            }
        }
    }

    private fun sendMessage() {
        val content = messageEditText.text.toString().trim()
        if (content.isEmpty()) {
            return
        }

        dbHelper.addDirectMessage(currentUserId, contactId, content)
        messageEditText.setText("")

        loadMessages()
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(if (adapter.itemCount > 0) adapter.itemCount - 1 else 0)
    }

    override fun onMessageLongClicked(message: Message, position: Int) {
        val options = mutableListOf("删除本条消息")
        if (message.senderId == currentUserId) {
            options.add("撤回")
        }

        AlertDialog.Builder(this)
            .setItems(options.toTypedArray()) { _, which ->
                when (options[which]) {
                    "删除本条消息" -> deleteMessageForMe(message, position)
                    "撤回" -> retractMessage(message, position)
                }
            }
            .show()
    }

    private fun deleteMessageForMe(message: Message, position: Int) {
        dbHelper.deleteMessageForMe(message.id, currentUserId)
        messageList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, messageList.size)
        Toast.makeText(this, "消息已删除", Toast.LENGTH_SHORT).show()
    }

    private fun retractMessage(message: Message, position: Int) {
        dbHelper.retractMessage(message.id)
        val retractedMessage = messageList[position]
        retractedMessage.isRetracted = true
        retractedMessage.content = if (retractedMessage.senderId == currentUserId) "你撤回了一条消息" else "对方撤回了一条消息"
        adapter.notifyItemChanged(position)
        Toast.makeText(this, "消息已撤回", Toast.LENGTH_SHORT).show()
    }
} 