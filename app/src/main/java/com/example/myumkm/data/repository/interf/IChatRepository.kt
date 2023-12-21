package com.example.myumkm.data.repository.interf

import androidx.lifecycle.LiveData
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.Query


interface IChatRepository {
    fun getChats(sectionId: String?): Query
    fun insertChat(chatEntity: ChatEntity, userId: String, result: (ResultState<String>) -> Unit)
    fun updateChat(chatEntity: ChatEntity, result: (ResultState<String>) -> Unit)
    fun predict(message: String): LiveData<out Any>
}