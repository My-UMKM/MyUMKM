package com.example.myumkm.data.repository.implementation

import android.util.Log
import retrofit2.HttpException
import androidx.lifecycle.liveData
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.data.remote.request.PredictRequest
import com.example.myumkm.data.remote.response.ChatResponse
import com.example.myumkm.data.remote.retrofit.ApiService
import com.example.myumkm.data.repository.interf.IChatRepository
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRepository(
    private val firebaseFirestore: FirebaseFirestore,
    private val apiService: ApiService
): IChatRepository {

    override fun getChats(sectionId: String?): Query {
        return firebaseFirestore.collection(CHAT_COLLECTION)
            .whereEqualTo(SECTION_ID_FIELD, sectionId)
            .limit(50)
            .orderBy(CHAT_TIMESTAMP)
    }

    override fun insertChat(chatEntity: ChatEntity, userId: String, result: (ResultState<String>) -> Unit) {
        val docRef = firebaseFirestore.collection(CHAT_COLLECTION).document()
        chatEntity.id = docRef.id
        docRef.set(chatEntity)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = PredictRequest(message = chatEntity.chatContent!!)
                        val chatResponse = apiService.predict(request)
                        chatEntity.chatResponse = chatResponse.response.responseText
                        chatEntity.chatbotTimestamp = System.currentTimeMillis()
                        if (chatEntity.sectionId.isNullOrEmpty()) {
                            val sectionDocRef = firebaseFirestore.collection(SectionRepository.SECTION_COLLECTION).document()
                            val sectionId = sectionDocRef.id
                            val sectionEntity = SectionEntity(id = sectionId,sectionName = chatResponse.response.section, userId = userId)
                            chatEntity.sectionId = sectionId
                            chatEntity.isBotTyping = false
                            sectionDocRef.set(sectionEntity)
                                .addOnSuccessListener {
                                    result.invoke(
                                        ResultState.Success(sectionId)
                                    )
                                }
                        }
                        else {
                            Log.d("Section", "Section not null: ${chatEntity.sectionId}")
                        }
                        docRef.set(chatEntity)
                            .addOnSuccessListener {
                                // Handle success of updating the document
                            }
                            .addOnFailureListener { e ->
                                // Handle failure of updating the document
                                result.invoke(
                                    ResultState.Error(
                                        e.localizedMessage
                                    )
                                )
                            }
                    } catch (e: HttpException) {
                        Log.d("LogTag", "Something wrong")
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    ResultState.Error(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateChat(chatEntity: ChatEntity, result: (ResultState<String>) -> Unit) {
        val document = firebaseFirestore.collection(CHAT_COLLECTION).document(chatEntity.id!!)
        document
            .set(chatEntity)
            .addOnSuccessListener {
                result.invoke(
                    ResultState.Success("Note has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    ResultState.Error(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun predict(message: String)= liveData {
        emit(ResultState.Loading)
        try {
            val request = PredictRequest(message = message)
            val chatResponse = apiService.predict(request)
            emit(ResultState.Success(chatResponse))
        } catch (e: HttpException) {
            emit(ResultState.Error("Chat error"))
        }
    }

    companion object {
        private const val CHAT_COLLECTION = "chats"
        const val SECTION_ID_FIELD = "sectionId"
        const val CHAT_TIMESTAMP = "chatTimestamp"
        @Volatile
        private var instance: ChatRepository? = null
        fun getInstance(
            firebaseFirestore: FirebaseFirestore,
            apiService: ApiService
        ): ChatRepository =
            instance ?: synchronized(this) {
                instance ?: ChatRepository(firebaseFirestore, apiService)
            }
    }
}