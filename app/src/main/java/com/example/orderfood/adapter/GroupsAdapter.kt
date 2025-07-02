package com.example.orderfood.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.activity.GroupChatActivity
import com.example.orderfood.model.Group

class GroupsAdapter(private val groups: List<Group>) : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.groupName.text = group.name

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, GroupChatActivity::class.java).apply {
                putExtra("GROUP_ID", group.id)
                putExtra("GROUP_NAME", group.name)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = groups.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.tv_group_name)
    }
} 