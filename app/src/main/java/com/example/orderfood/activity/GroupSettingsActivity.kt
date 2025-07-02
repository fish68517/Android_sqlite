package com.example.orderfood.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.adapter.GroupMemberAdapter
import com.example.orderfood.model.Group
import com.example.orderfood.model.User

class GroupSettingsActivity : AppCompatActivity(), GroupMemberAdapter.OnRemoveMemberClickListener {

    private lateinit var rvMembers: RecyclerView
    private lateinit var btnAddMembers: Button
    private lateinit var btnLeaveGroup: Button
    private lateinit var tvGroupName: TextView

    private lateinit var dbHelper: DataBaseOpenHelper
    private var adapter: GroupMemberAdapter? = null
    private val memberList = mutableListOf<User>()
    private var currentGroup: Group? = null

    private var currentUserId = -1
    private var groupId = -1L

    private val addMembersLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshMemberList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_settings)
        title = "群聊设置"

        val sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)
        groupId = intent.getLongExtra("GROUP_ID", -1)

        if (currentUserId == -1 || groupId == -1L) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DataBaseOpenHelper(this)
        initViews()
        loadGroupDetails()
        setupListeners()
    }

    private fun initViews() {
        rvMembers = findViewById(R.id.recycler_view_group_members)
        btnAddMembers = findViewById(R.id.btn_add_group_members)
        btnLeaveGroup = findViewById(R.id.btn_leave_group)
        tvGroupName = findViewById(R.id.tv_group_name)
        rvMembers.layoutManager = LinearLayoutManager(this)
    }

    private fun loadGroupDetails() {
        currentGroup = dbHelper.getGroupDetails(groupId)
        currentGroup?.let {
            tvGroupName.text = it.name
            if (currentUserId == it.creatorId) {
                btnLeaveGroup.text = "解散群聊"
            }
            refreshMemberList()
        } ?: run {
            Toast.makeText(this, "无法加载群组信息", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun refreshMemberList() {
        memberList.clear()
        memberList.addAll(dbHelper.getGroupMembers(groupId))
        if (adapter == null) {
            currentGroup?.let {
                adapter = GroupMemberAdapter(memberList, currentUserId, it.creatorId, this)
                rvMembers.adapter = adapter
            }
        } else {
            adapter?.notifyDataSetChanged()
        }
    }

    private fun setupListeners() {
        btnAddMembers.setOnClickListener {
            val intent = Intent(this, AddMembersActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            addMembersLauncher.launch(intent)
        }

        btnLeaveGroup.setOnClickListener { showLeaveGroupConfirmation() }
    }

    private fun showLeaveGroupConfirmation() {
        val isCreator = currentUserId == currentGroup?.creatorId
        AlertDialog.Builder(this)
            .setTitle(if (isCreator) "解散群聊" else "退出群聊")
            .setMessage(if (isCreator) "确定要解散该群聊吗？此操作不可恢复。" else "确定要退出该群聊吗？")
            .setPositiveButton("确定") { _, _ ->
                if (isCreator) {
                    dbHelper.deleteGroup(groupId)
                    Toast.makeText(this, "群聊已解散", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.removeGroupMember(groupId, currentUserId)
                    Toast.makeText(this, "已退出群聊", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onRemoveMember(member: User) {
        AlertDialog.Builder(this)
            .setTitle("移除成员")
            .setMessage("确定要将 ${member.nickname} 移出群聊吗？")
            .setPositiveButton("移除") { _, _ ->
                dbHelper.removeGroupMember(groupId, member.id)
                Toast.makeText(this, "${member.nickname} 已被移出群聊", Toast.LENGTH_SHORT).show()
                refreshMemberList()
            }
            .setNegativeButton("取消", null)
            .show()
    }
} 