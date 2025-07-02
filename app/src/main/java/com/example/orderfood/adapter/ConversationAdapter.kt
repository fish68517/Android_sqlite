package com.example.orderfood.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.activity.ChatActivity
import com.example.orderfood.activity.GroupChatActivity
import com.example.orderfood.model.Conversation

class ConversationAdapter(
    private var conversations: List<Conversation>
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]

        holder.name.text = conversation.name
        holder.lastMessage.text = conversation.lastMessage
        holder.timestamp.text = conversation.timestamp // Consider formatting this nicely

        if (conversation.isGroup) {
            holder.avatar.setImageResource(R.mipmap.ic_launcher) // Group avatar
        } else {
            holder.avatar.setImageResource(R.mipmap.ic_launcher_round) // User avatar
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = if (conversation.isGroup) {
                Intent(context, GroupChatActivity::class.java).apply {
                    putExtra("GROUP_ID", conversation.id)
                    putExtra("GROUP_NAME", conversation.name)
                }
            } else {
                Intent(context, ChatActivity::class.java).apply {
                    putExtra("CONTACT_ID", conversation.id.toInt())
                    putExtra("CONTACT_NICKNAME", conversation.name)
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = conversations.size

    fun updateList(newList: List<Conversation>) {
        conversations = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        val name: TextView = itemView.findViewById(R.id.tv_conversation_name)
        val lastMessage: TextView = itemView.findViewById(R.id.tv_last_message)
        val timestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
    }
} 