package com.example.orderfood.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.MyApplication
import com.example.orderfood.R
import com.example.orderfood.activity.AddContactActivity
import com.example.orderfood.activity.CreateGroupActivity
import com.example.orderfood.adapter.ContactsAdapter
import com.example.orderfood.adapter.GroupsAdapter
import com.example.orderfood.model.Group
import com.example.orderfood.model.User

class ContactsFragment : Fragment(), ContactsAdapter.OnContactActionsListener {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var dbHelper: DataBaseOpenHelper
    private var contactListWithHeaders: MutableList<Any> = mutableListOf()
    private var groupList: MutableList<Group> = mutableListOf()

    private val currentUserId by lazy { MyApplication.user.id }

    companion object {
        private const val ADD_CONTACT_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        dbHelper = DataBaseOpenHelper(requireContext())

        val contactList = dbHelper.getContacts(currentUserId).sortedBy { it.nickname }
        contactListWithHeaders = createGroupedList(contactList)
        groupList.addAll(dbHelper.getGroupsForUser(currentUserId))

        contactsRecyclerView = view.findViewById(R.id.recycler_view_contacts)
        contactsRecyclerView.layoutManager = LinearLayoutManager(context)
        contactsAdapter = ContactsAdapter(contactListWithHeaders, this)
        contactsRecyclerView.adapter = contactsAdapter

        groupsRecyclerView = view.findViewById(R.id.recycler_view_groups)
        groupsRecyclerView.layoutManager = LinearLayoutManager(context)
        groupsAdapter = GroupsAdapter(groupList)
        groupsRecyclerView.adapter = groupsAdapter

        val menuButton: ImageView = view.findViewById(R.id.iv_menu)
        menuButton.setOnClickListener { showPopupMenu(it) }

        return view
    }

    private fun createGroupedList(contacts: List<User>): MutableList<Any> {
        val groupedList = mutableListOf<Any>()
        var lastHeader = ""
        for (contact in contacts) {
            val nickname = contact.nickname
            if (!nickname.isNullOrEmpty()) {
                val header = nickname.substring(0, 1).uppercase()
                if (lastHeader != header) {
                    groupedList.add(header)
                    lastHeader = header
                }
            }
            groupedList.add(contact)
        }
        return groupedList
    }

    private fun showPopupMenu(anchor: View) {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_menu, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAsDropDown(anchor)

        val addFriend: TextView = popupView.findViewById(R.id.tv_add_friend)
        addFriend.setOnClickListener {
            val intent = Intent(activity, AddContactActivity::class.java)
            startActivityForResult(intent, ADD_CONTACT_REQUEST)
            popupWindow.dismiss()
        }

        val startGroupChat: TextView = popupView.findViewById(R.id.tv_start_group_chat)
        startGroupChat.setOnClickListener {
            val intent = Intent(activity, CreateGroupActivity::class.java)
            startActivityForResult(intent, ADD_CONTACT_REQUEST)
            popupWindow.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            refreshLists()
        }
    }

    override fun onEditClick(contact: User) {
        showEditRemarkDialog(contact)
    }

    override fun onDeleteClick(contact: User) {
        showDeleteConfirmationDialog(contact)
    }

    private fun showDeleteConfirmationDialog(contact: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除联系人")
            .setMessage("注意：这将会永久删除你们之间的所有聊天记录。")
            .setPositiveButton("删除") { _, _ ->
                dbHelper.deleteContact(currentUserId, contact.id)
                refreshLists()
                Toast.makeText(context, "联系人已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showEditRemarkDialog(contact: User) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("修改昵称")

        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "输入新昵称"
        }
        builder.setView(input)

        builder.setPositiveButton("确定") { _, _ ->
            val remark = input.text.toString().trim()
            if (remark.isNotEmpty()) {
                dbHelper.updateUsername(contact.id, remark)
                dbHelper.updateContactRemark(currentUserId, contact.id, remark)
                dbHelper.updateUserNickname(contact.id, remark)
                Toast.makeText(context, "昵称已更新", Toast.LENGTH_SHORT).show()
                refreshLists()
            }
        }
        builder.setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun refreshLists() {
        val updatedContacts = dbHelper.getContacts(currentUserId).sortedBy { it.nickname }
        contactListWithHeaders.clear()
        contactListWithHeaders.addAll(createGroupedList(updatedContacts))
        contactsAdapter.notifyDataSetChanged()

        groupList.clear()
        groupList.addAll(dbHelper.getGroupsForUser(currentUserId))
        groupsAdapter.notifyDataSetChanged()

        Toast.makeText(context, "列表已更新", Toast.LENGTH_SHORT).show()
    }
} 