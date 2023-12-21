package com.example.myumkm.data.entity

data class ChatEntity(
    var id: String? = null,
    val chatContent: String? = null,
    val chatTimestamp: Long? = null,
    var chatResponse: String? = null,
    var chatbotTimestamp: Long? = null,
    var isBotTyping: Boolean? = null,
    var sectionId: String? = null
)