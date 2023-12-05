package com.example.myumkm.data.repository.interf

import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.Query


interface ISectionRepository {
    fun getSections(userId: String): Query
    fun insertSection(sectionEntity: SectionEntity, result: (ResultState<String>) -> Unit)
}