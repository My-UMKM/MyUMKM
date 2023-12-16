package com.example.myumkm.data.entity

data class ChatEntity(
    var id: String? = null,
    val chatContent: String? = null,
    val chatTimestamp: Long? = null,
    var chatResponse: String? = null,
    val chatbotTimestamp: Long? = null,
    val isBotTyping: Boolean? = null,
    val sectionId: String? = null
)