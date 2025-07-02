package com.example.orderfood.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
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
import com.example.orderfood.adapter.GroupChatAdapter
import com.example.orderfood.model.Message
import com.example.orderfood.model.User

class GroupChatActivity : AppCompatActivity(), GroupChatAdapter.OnMessageLongClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupChatAdapter
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var dbHelper: DataBaseOpenHelper

    private var currentUserId = -1
    private var groupId = -1L
    private var groupName: String? = null
    private lateinit var membersMap: Map<Int, User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        groupName = intent.getStringExtra("GROUP_NAME")

        if (groupId == -1L || groupName == null) {
            Toast.makeText(this, "无效的群组", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.apply {
            title = groupName
            setDisplayHomeAsUpEnabled(true)
        }

        val sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)

        dbHelper = DataBaseOpenHelper(this)

        recyclerView = findViewById(R.id.recycler_view_messages)
        messageEditText = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send)

        messageList = mutableListOf()
        membersMap = HashMap()
        loadGroupMembers()
        loadMessages()

        adapter = GroupChatAdapter(messageList, currentUserId, membersMap, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(if (adapter.itemCount > 0) adapter.itemCount - 1 else 0)

        sendButton.setOnClickListener { sendMessage() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_group_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_group_settings -> {
                val intent = Intent(this, GroupSettingsActivity::class.java)
                intent.putExtra("GROUP_ID", groupId)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadGroupMembers() {
        val members = dbHelper.getGroupMembers(groupId)
        membersMap = members.associateBy { it.id }
    }

    private fun loadMessages() {
        messageList.clear()
        dbHelper.getGroupMessages(groupId.toInt(), currentUserId)?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val message = Message().apply {
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID))
                        senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID))
                        timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP))
                        isRetracted = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_IS_RETRACTED)) == 1

                        content = if (isRetracted) {
                            val sender = membersMap[senderId]
                            val senderName = if (sender != null && sender.id != currentUserId) sender.nickname else "你"
                            "$senderName 撤回了一条消息"
                        } else {
                            cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT))
                        }
                    }
                    messageList.add(message)
                } while (cursor.moveToNext())
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun sendMessage() {
        val content = messageEditText.text.toString().trim()
        if (content.isEmpty()) return

        dbHelper.addGroupMessage(currentUserId, groupId.toInt(), content)
        messageEditText.setText("")

        loadMessages()
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
        loadMessages()
        Toast.makeText(this, "消息已撤回", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 