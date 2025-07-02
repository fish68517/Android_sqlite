package com.example.orderfood.model

data class Message(
    var id: Long = 0,
    var senderId: Int = 0,
    var receiverId: Int = 0,
    var content: String? = null,
    var timestamp: String? = null,
    var isRetracted: Boolean = false
) 