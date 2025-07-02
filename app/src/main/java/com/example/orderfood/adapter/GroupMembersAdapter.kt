package com.example.orderfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.model.User

class GroupMembersAdapter(
    private val members: List<User>,
    private val creatorId: Int,
    private val currentUserId: Int,
    private val listener: OnRemoveMemberClickListener
) : RecyclerView.Adapter<GroupMembersAdapter.ViewHolder>() {

    interface OnRemoveMemberClickListener {
        fun onRemoveMemberClick(member: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.memberName.text = member.nickname

        // Only the group creator can remove members, and cannot remove themselves
        if (currentUserId == creatorId && member.id != creatorId) {
            holder.removeButton.visibility = View.VISIBLE
            holder.removeButton.setOnClickListener {
                listener.onRemoveMemberClick(member)
            }
        } else {
            holder.removeButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = members.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberName: TextView = itemView.findViewById(R.id.tv_member_nickname)
        val removeButton: Button = itemView.findViewById(R.id.btn_remove_member)
    }
} 