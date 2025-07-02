package com.example.orderfood.model

data class Conversation(
    var id: Long = 0,
    var name: String? = null,
    var lastMessage: String? = null,
    var timestamp: String? = null,
    var isGroup: Boolean = false
) 