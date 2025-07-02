package com.example.orderfood.activity

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.adapter.MessageSearchAdapter
import com.example.orderfood.model.SearchResult

class SearchMessagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageSearchAdapter
    private lateinit var searchResults: MutableList<SearchResult>
    private lateinit var dbHelper: DataBaseOpenHelper
    private lateinit var tvInfo: TextView
    private var query: String? = null
    private var currentUserId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_messages)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        query = intent.getStringExtra("QUERY")
        if (query.isNullOrBlank()) {
            Toast.makeText(this, "无效的搜索词", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.apply {
            title = "搜索: $query"
            setDisplayHomeAsUpEnabled(true)
        }

        val sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "用户状态异常", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DataBaseOpenHelper(this)
        initViews()
        performSearch()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view_search_results)
        tvInfo = findViewById(R.id.tv_search_results_info)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchResults = ArrayList()
        adapter = MessageSearchAdapter(searchResults, query)
        recyclerView.adapter = adapter
    }

    private fun performSearch() {
        dbHelper.searchAllMessages(currentUserId, query)?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val result = SearchResult().apply {
                        messageId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID))
                        content = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT))
                        timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP))
                        senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID))
                        senderNickname = cursor.getString(cursor.getColumnIndexOrThrow("sender_nickname"))
                        conversationName = cursor.getString(cursor.getColumnIndexOrThrow("conversation_name"))
                        receiverId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_RECEIVER_ID))
                        groupId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_GROUP_ID))
                    }
                    searchResults.add(result)
                } while (cursor.moveToNext())
            }
        }

        tvInfo.text = "$query 的搜索结果 (${searchResults.size}条)"
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "未找到相关消息", Toast.LENGTH_SHORT).show()
        }
        adapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 