package com.example.orderfood.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.activity.LoginActivity
import com.example.orderfood.model.User

class ProfileFragment : Fragment() {

    private lateinit var tvNickname: TextView
    private lateinit var tvUsername: TextView
    private lateinit var btnEditNickname: Button
    private lateinit var btnLogout: Button
    private lateinit var dbHelper: DataBaseOpenHelper
    private var currentUserId = -1
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DataBaseOpenHelper(requireContext())
        val sessionPrefs = requireActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNickname = view.findViewById(R.id.tv_nickname)
        tvUsername = view.findViewById(R.id.tv_username)
        btnEditNickname = view.findViewById(R.id.btn_edit_nickname)
        btnLogout = view.findViewById(R.id.btn_logout)

        if (currentUserId == -1) {
            logout()
            return
        }

        btnEditNickname.setOnClickListener { showEditNicknameDialog() }
        btnLogout.setOnClickListener { logout() }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        if (currentUserId != -1) {
            currentUser = dbHelper.getUser(currentUserId)
            currentUser?.let {
                tvNickname.text = it.nickname
                tvUsername.text = "用户名: ${it.username}"
            } ?: run {
                Toast.makeText(context, "用户数据异常，请重新登录", Toast.LENGTH_SHORT).show()
                logout()
            }
        }
    }

    private fun showEditNicknameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("修改昵称")

        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            setText(currentUser?.nickname)
        }
        builder.setView(input)

        builder.setPositiveButton("确定") { _, _ ->
            val newNickname = input.text.toString().trim()
            if (newNickname.isNotEmpty()) {
                val rowsAffected = dbHelper.updateUserNickname(currentUserId, newNickname)
                if (rowsAffected > 0) {
                    Toast.makeText(context, "昵称修改成功", Toast.LENGTH_SHORT).show()
                    loadUserProfile()
                } else {
                    Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "昵称不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun logout() {
        val sessionPrefs = requireActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        sessionPrefs.edit().clear().apply()

        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }
} 