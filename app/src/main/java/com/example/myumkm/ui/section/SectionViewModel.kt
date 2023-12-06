package com.example.myumkm.ui.section

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.repository.interf.IAccountRepository
import com.example.myumkm.data.repository.interf.ISectionRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SectionViewModel(val iSectionRepository: ISectionRepository, val user: UserEntity) : ViewModel() {
    private val _sectionsOptions: MutableLiveData<FirestoreRecyclerOptions<SectionEntity>> = MutableLiveData()

    val sectionOptions: LiveData<FirestoreRecyclerOptions<SectionEntity>>
        get() = _sectionsOptions

    init {
        _sectionsOptions.value = FirestoreRecyclerOptions.Builder<SectionEntity>()
            .setQuery(iSectionRepository.getSections(user.id), SectionEntity::class.java)
            .build()
    }
}