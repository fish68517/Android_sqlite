package com.example.orderfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.model.User

class UserSearchAdapter(
    private val userList: List<User>,
    private val listener: OnAddContactListener
) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    interface OnAddContactListener {
        fun onAddContact(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.nickname.text = user.nickname
        holder.addButton.setOnClickListener {
            listener.onAddContact(user)
            holder.addButton.text = "已添加"
            holder.addButton.isEnabled = false
        }
    }

    override fun getItemCount(): Int = userList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname: TextView = itemView.findViewById(R.id.tv_user_nickname)
        val addButton: Button = itemView.findViewById(R.id.btn_add)
    }
} 