package com.example.orderfood.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.activity.ChatActivity
import com.example.orderfood.model.User

class ContactsAdapter(
    private val items: List<Any>,
    private val actionsListener: OnContactActionsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CONTACT = 1
    }

    interface OnContactActionsListener {
        fun onEditClick(contact: User)
        fun onDeleteClick(contact: User)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) VIEW_TYPE_HEADER else VIEW_TYPE_CONTACT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_contact_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_contact, parent, false)
            ContactViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_HEADER -> {
                val headerHolder = holder as HeaderViewHolder
                headerHolder.headerTitle.text = items[position] as String
            }
            VIEW_TYPE_CONTACT -> {
                val contactHolder = holder as ContactViewHolder
                val contact = items[position] as User
                contactHolder.contactName.text = contact.nickname

                contactHolder.itemView.setOnClickListener {
                    val context = contactHolder.itemView.context
                    val intent = Intent(context, ChatActivity::class.java).apply {
                        putExtra("CONTACT_ID", contact.id)
                        putExtra("CONTACT_NICKNAME", contact.nickname)
                    }
                    context.startActivity(intent)
                }

                contactHolder.editButton.setOnClickListener {
                    actionsListener.onEditClick(contact)
                }

                contactHolder.deleteButton.setOnClickListener {
                    actionsListener.onDeleteClick(contact)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.tv_contact_name)
        val editButton: Button = itemView.findViewById(R.id.btn_edit_contact)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete_contact)
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTitle: TextView = itemView.findViewById(R.id.tv_header)
    }
} 