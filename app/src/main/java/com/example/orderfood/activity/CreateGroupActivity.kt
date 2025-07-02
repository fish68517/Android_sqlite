package com.example.orderfood.activity

import android.content.Context
import android.os.Bundle
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
import com.example.orderfood.adapter.SelectableContactsAdapter

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var rvContacts: RecyclerView
    private lateinit var btnCreate: Button
    private lateinit var adapter: SelectableContactsAdapter
    private lateinit var dbHelper: DataBaseOpenHelper
    private var currentUserId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "创建群聊"
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

        rvContacts = findViewById(R.id.recycler_view_selectable_contacts)
        btnCreate = findViewById(R.id.btn_create)

        setupRecyclerView()

        btnCreate.setOnClickListener { createGroup() }
    }

    private fun setupRecyclerView() {
        val contacts = dbHelper.getContacts(currentUserId)
        adapter = SelectableContactsAdapter(contacts)
        rvContacts.layoutManager = LinearLayoutManager(this)
        rvContacts.adapter = adapter
    }

    private fun createGroup() {
        val selectedMemberIds = adapter.getSelectedContactIds()
        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "请至少选择一个群成员", Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this).apply {
            hint = "输入群聊名称"
        }

        AlertDialog.Builder(this)
            .setTitle("群聊名称")
            .setView(input)
            .setPositiveButton("确定") { _, _ ->
                val groupName = input.text.toString().trim()
                if (groupName.isEmpty()) {
                    Toast.makeText(this@CreateGroupActivity, "群聊名称不能为空", Toast.LENGTH_SHORT).show()
                } else {
                    val groupId = dbHelper.createGroup(groupName, currentUserId, selectedMemberIds)
                    if (groupId != -1L) {
                        Toast.makeText(this, "群聊 '$groupName' 创建成功", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "创建失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 