package com.example.orderfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.model.User

class GroupMemberAdapter(
    private val memberList: List<User>,
    private val currentUserId: Int,
    private val creatorId: Int,
    private val listener: OnRemoveMemberClickListener
) : RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>() {

    interface OnRemoveMemberClickListener {
        fun onRemoveMember(member: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = memberList[position]
        holder.nickname.text = member.nickname

        // Only the group creator can remove other members
        if (currentUserId == creatorId && member.id != currentUserId) {
            holder.removeButton.visibility = View.VISIBLE
            holder.removeButton.setOnClickListener {
                listener.onRemoveMember(member)
            }
        } else {
            holder.removeButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = memberList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname: TextView = itemView.findViewById(R.id.tv_member_nickname)
        val removeButton: Button = itemView.findViewById(R.id.btn_remove_member)
    }
} 