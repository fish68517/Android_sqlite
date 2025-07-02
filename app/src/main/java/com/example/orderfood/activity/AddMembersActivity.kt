package com.example.orderfood.activity

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.adapter.SelectableContactsAdapter
import com.example.orderfood.model.User

class AddMembersActivity : AppCompatActivity() {

    private lateinit var rvSelectableContacts: RecyclerView
    private lateinit var btnAddMembers: Button
    private lateinit var adapter: SelectableContactsAdapter
    private lateinit var dbHelper: DataBaseOpenHelper

    private var currentUserId = -1
    private var groupId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_members)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "添加群成员"
            setDisplayHomeAsUpEnabled(true)
        }

        val sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)
        groupId = intent.getLongExtra("GROUP_ID", -1)

        if (currentUserId == -1 || groupId == -1L) {
            Toast.makeText(this, "数据错误，无法添加成员", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DataBaseOpenHelper(this)

        rvSelectableContacts = findViewById(R.id.recycler_view_add_members)
        btnAddMembers = findViewById(R.id.btn_add_selected_members)

        setupRecyclerView()

        btnAddMembers.setOnClickListener { addSelectedMembers() }
    }

    private fun setupRecyclerView() {
        val contactsNotInGroup = dbHelper.getContactsNotInGroup(currentUserId, groupId)
        if (contactsNotInGroup.isEmpty()) {
            Toast.makeText(this, "没有可添加的联系人", Toast.LENGTH_SHORT).show()
        }
        adapter = SelectableContactsAdapter(contactsNotInGroup)
        rvSelectableContacts.layoutManager = LinearLayoutManager(this)
        rvSelectableContacts.adapter = adapter
    }

    private fun addSelectedMembers() {
        val selectedMemberIds = adapter.getSelectedContactIds()
        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "请至少选择一个联系人", Toast.LENGTH_SHORT).show()
            return
        }

        dbHelper.addGroupMembers(groupId, selectedMemberIds)
        Toast.makeText(this, "成员添加成功", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 