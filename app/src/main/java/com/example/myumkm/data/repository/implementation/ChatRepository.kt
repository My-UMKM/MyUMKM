package com.example.myumkm.data.repository.implementation

import androidx.lifecycle.liveData
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.remote.response.ChatResponse
import com.example.myumkm.data.remote.retrofit.ApiService
import com.example.myumkm.data.repository.interf.IChatRepository
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import retrofit2.HttpException

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

    override fun insertChat(chatEntity: ChatEntity, result: (ResultState<String>) -> Unit) {
        val docRef = firebaseFirestore.collection(CHAT_COLLECTION).document()
        chatEntity.id = docRef.id
        docRef.set(chatEntity)
            .addOnSuccessListener {
                liveData {
                    try {
                        val successResponse = apiService.predict(chatEntity.chatContent!!)
                        chatEntity.chatResponse = successResponse.responseText
//                        emit(ResultState.Success(successResponse))
                        updateChat()
                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        emit(ResultState.Error("Error"))
                    }
                }
                result.invoke(
                    ResultState.Success(docRef.id)
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