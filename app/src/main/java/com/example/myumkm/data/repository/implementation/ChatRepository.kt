package com.example.myumkm.data.repository.implementation

import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.repository.implementation.SectionRepository.Companion.SECTION_ID_FIELD
import com.example.myumkm.data.repository.interf.IChatRepository
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRepository(
    private val firebaseFirestore: FirebaseFirestore,
): IChatRepository {

    override fun getChats(sectionId: String?): Query {
        return firebaseFirestore.collection(CHAT_COLLECTION)
            .whereEqualTo(SECTION_ID_FIELD, sectionId)
            .limit(50)
            .orderBy(CHAT_TIMESTAMP)
    }

    override fun insertChat(chatEntity: ChatEntity, result: (ResultState<String>) -> Unit) {
        firebaseFirestore.collection(CHAT_COLLECTION)
            .add(chatEntity)
            .addOnSuccessListener {
                result.invoke(
                    ResultState.Success(it.id)
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
        ): ChatRepository =
            instance ?: synchronized(this) {
                instance ?: ChatRepository(firebaseFirestore)
            }
    }
}