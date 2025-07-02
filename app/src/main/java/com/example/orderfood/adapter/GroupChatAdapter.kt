package com.example.orderfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.model.Message
import com.example.orderfood.model.User
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GroupChatAdapter(
    private val messageList: List<Message>,
    private val currentUserId: Int,
    private val membersMap: Map<Int, User>,
    private val longClickListener: OnMessageLongClickListener
) : RecyclerView.Adapter<GroupChatAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_RETRACTED = 3
    }

    interface OnMessageLongClickListener {
        fun onMessageLongClicked(message: Message, position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return when {
            message.isRetracted -> VIEW_TYPE_RETRACTED
            message.senderId == currentUserId -> VIEW_TYPE_SENT
            else -> VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            VIEW_TYPE_SENT -> inflater.inflate(R.layout.item_group_message_sent, parent, false)
            VIEW_TYPE_RECEIVED -> inflater.inflate(R.layout.item_group_message_received, parent, false)
            else -> inflater.inflate(R.layout.item_message_retracted, parent, false) // VIEW_TYPE_RETRACTED
        }
        return MessageViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message, position, longClickListener, membersMap[message.senderId])
    }

    override fun getItemCount(): Int = messageList.size

    class MessageViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        private val messageContent: TextView = itemView.findViewById(R.id.tv_message_content)
        private val timestamp: TextView? = if (viewType != VIEW_TYPE_RETRACTED) itemView.findViewById(R.id.tv_timestamp) else null
        private val senderNickname: TextView? = if (viewType == VIEW_TYPE_RECEIVED) itemView.findViewById(R.id.tv_sender_nickname) else null

        fun bind(message: Message, position: Int, listener: OnMessageLongClickListener, sender: User?) {
            messageContent.text = message.content

            if (message.isRetracted) {
                itemView.setOnLongClickListener(null)
                timestamp?.visibility = View.GONE
                senderNickname?.visibility = View.GONE
            } else {
                timestamp?.text = formatTimestamp(message.timestamp)
                if (senderNickname != null && sender != null) {
                    senderNickname.text = sender.nickname
                    senderNickname.visibility = View.VISIBLE
                } else {
                    senderNickname?.visibility = View.GONE
                }

                itemView.setOnLongClickListener {
                    listener.onMessageLongClicked(message, position)
                    true
                }
            }
        }

        private fun formatTimestamp(dbTimestamp: String?): String {
            if (dbTimestamp.isNullOrEmpty()) return ""
            val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val displayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return try {
                val date = dbFormat.parse(dbTimestamp)
                displayFormat.format(date)
            } catch (e: ParseException) {
                ""
            }
        }
    }
} 