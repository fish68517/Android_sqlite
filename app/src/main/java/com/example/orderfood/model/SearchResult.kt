package com.example.orderfood.model

data class SearchResult(
    var messageId: Long = 0,
    var content: String? = null,
    var timestamp: String? = null,
    var senderId: Int = 0,
    var senderNickname: String? = null,
    var conversationName: String? = null,
    var receiverId: Int = 0,
    var groupId: Long = 0
) 