package com.example.orderfood.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.adapter.UserSearchAdapter
import com.example.orderfood.model.User

class AddContactActivity : AppCompatActivity(), UserSearchAdapter.OnAddContactListener {

    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserSearchAdapter
    private lateinit var searchResults: MutableList<User>
    private lateinit var dbHelper: DataBaseOpenHelper
    private var currentUserId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "添加联系人"
            setDisplayHomeAsUpEnabled(true)
        }

        val sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)

        if (currentUserId == -1) {
            Toast.makeText(this, "用户状态异常, 请重新登录", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DataBaseOpenHelper(this)

        searchEditText = findViewById(R.id.et_search_username)
        recyclerView = findViewById(R.id.recycler_view_search_results)

        searchResults = ArrayList()
        adapter = UserSearchAdapter(searchResults, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        performSearch("")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun performSearch(query: String) {
        val users = dbHelper.searchUsers(query, currentUserId)
        searchResults.clear()
        searchResults.addAll(users)
        adapter.notifyDataSetChanged()

        if (users.isEmpty() && query.isEmpty()) {
            Toast.makeText(this, "没有其他可添加的用户", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAddContact(user: User) {
        dbHelper.addContact(currentUserId, user.id)
        Toast.makeText(this, "已添加 ${user.nickname} 为联系人", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        performSearch(searchEditText.text.toString().trim())
    }
} 