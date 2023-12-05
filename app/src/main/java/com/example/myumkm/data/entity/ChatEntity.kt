package com.example.myumkm.data.entity

data class ChatEntity(
    val chatId: String? = null,
    val chatContent: String,
    val chatTimestamp: Long,
    val chatResponse: String?,
    val chatbotTimestamp: Long?,
    val isBotTyping: Boolean = false,
    val sectionId: String?
)