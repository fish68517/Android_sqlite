package com.example.orderfood.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.activity.ChatActivity
import com.example.orderfood.activity.GroupChatActivity
import com.example.orderfood.model.SearchResult

class MessageSearchAdapter(
    private val results: List<SearchResult>,
    private val query: String?
) : RecyclerView.Adapter<MessageSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        // Highlight the query in the content
        if (!query.isNullOrBlank()) {
            val content = result.content
            val startIndex = content?.lowercase()?.indexOf(query.lowercase())
            if (startIndex != -1) {
                val highlightedContent = content.substring(0, startIndex) +
                        "<font color='red'>" + content.substring(startIndex, startIndex + query.length) + "</font>" +
                        content.substring(startIndex + query.length)
                holder.content.text = HtmlCompat.fromHtml(highlightedContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                holder.content.text = content
            }
        } else {
            holder.content.text = result.content
        }


        holder.details.text = "来自 ${result.senderNickname} 在 ${result.conversationName} - ${result.timestamp}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = if (result.groupId > 0) { // It's a group chat
                Intent(context, GroupChatActivity::class.java).apply {
                    putExtra("GROUP_ID", result.groupId)
                    putExtra("GROUP_NAME", result.conversationName)
                }
            } else { // It's a direct message
                val currentUserId = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                    .getInt("CURRENT_USER_ID", -1)
                Intent(context, ChatActivity::class.java).apply {
                    putExtra("CONTACT_ID", if (result.senderId == currentUserId) result.receiverId else result.senderId)
                    putExtra("CONTACT_NICKNAME", result.conversationName)
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = results.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.tv_message_content)
        val details: TextView = itemView.findViewById(R.id.tv_message_details)
    }
} 