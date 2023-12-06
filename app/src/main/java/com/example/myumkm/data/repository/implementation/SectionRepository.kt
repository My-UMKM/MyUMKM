package com.example.myumkm.data.repository.implementation

import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.data.repository.interf.ISectionRepository
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SectionRepository(
    private val firebaseFirestore: FirebaseFirestore,
    ): ISectionRepository {
    override fun getSections(userId: String): Query {
        return firebaseFirestore.collection(SECTION_COLLECTION)
            .whereEqualTo(USER_ID_FIELD, userId)
            .limit(50)
    }
    override fun insertSection(sectionEntity: SectionEntity, result: (ResultState<String>) -> Unit) {
        firebaseFirestore.collection(SECTION_COLLECTION)
            .add(sectionEntity)
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

    companion object {
        private const val USER_ID_FIELD = "userId"
        const val SECTION_ID_FIELD = "id"
        private const val SECTION_COLLECTION = "sections"

        @Volatile
        private var instance: SectionRepository? = null

        fun getInstance(
            firebaseFirestore: FirebaseFirestore,
        ): SectionRepository =
            instance ?: synchronized(this) {
                instance ?: SectionRepository(firebaseFirestore)
            }
    }
}