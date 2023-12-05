package com.example.myumkm.ui.section

import androidx.lifecycle.ViewModel
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.data.repository.interf.IAccountRepository
import com.example.myumkm.data.repository.interf.ISectionRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SectionViewModel(private val iSectionRepository: ISectionRepository, private val iAccountRepository: IAccountRepository) : ViewModel() {
    var userId: String? = null
    val sectionsOptions: FirestoreRecyclerOptions<SectionEntity> = FirestoreRecyclerOptions.Builder<SectionEntity>()
        .setQuery(iSectionRepository.getSections(userId!!), SectionEntity::class.java)
        .build()

    init {
        iAccountRepository.getSession { userEntity ->
            userId = userEntity?.id
        }
    }
}