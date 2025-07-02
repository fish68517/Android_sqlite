package com.example.orderfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.R
import com.example.orderfood.model.User

class SelectableContactsAdapter(
    private val contactList: List<User>
) : RecyclerView.Adapter<SelectableContactsAdapter.ViewHolder>() {

    val selectedContactIds = HashSet<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selectable_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contactList[position]
        holder.nickname.text = contact.nickname
        holder.checkBox.isChecked = selectedContactIds.contains(contact.id)

        holder.itemView.setOnClickListener {
            if (selectedContactIds.contains(contact.id)) {
                selectedContactIds.remove(contact.id)
            } else {
                selectedContactIds.add(contact.id)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = contactList.size

    fun getSelectedContactIds(): List<Int> {
        return ArrayList(selectedContactIds)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname: TextView = itemView.findViewById(R.id.tv_nickname)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_select)
    }
} 