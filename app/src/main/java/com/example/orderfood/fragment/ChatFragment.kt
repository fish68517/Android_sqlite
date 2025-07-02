package com.example.orderfood.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R
import com.example.orderfood.activity.SearchMessagesActivity
import com.example.orderfood.adapter.ConversationAdapter
import com.example.orderfood.model.Conversation

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConversationAdapter
    private var conversationList: MutableList<Conversation> = mutableListOf()
    private lateinit var dbHelper: DataBaseOpenHelper
    private var currentUserId = -1
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionPrefs = requireActivity().getSharedPreferences("AppSession", Context.MODE_PRIVATE)
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        dbHelper = DataBaseOpenHelper(requireContext())
        recyclerView = view.findViewById(R.id.recycler_view_conversations)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.search_view_messages)

        adapter = ConversationAdapter(conversationList)
        recyclerView.adapter = adapter

        setupSearch()

        return view
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    val intent = Intent(activity, SearchMessagesActivity::class.java).apply {
                        putExtra("QUERY", query.trim())
                    }
                    startActivity(intent)
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadConversations()
    }

    private fun loadConversations() {
        val loadedConversations = dbHelper.getConversations(currentUserId)
        conversationList.clear()
        conversationList.addAll(loadedConversations)
        adapter.notifyDataSetChanged()
    }
} 