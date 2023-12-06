package com.example.myumkm.ui.section

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.Injection
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.repository.implementation.AccountRepository
import com.example.myumkm.data.repository.implementation.SectionRepository

class SectionViewModelFactory private constructor(private val sectionRepository: SectionRepository,private val user: UserEntity) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(SectionViewModel::class.java) -> {
                SectionViewModel(sectionRepository, user) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    companion object {
        fun getInstance(context: Context): SectionViewModelFactory =
            synchronized(this) {
                SectionViewModelFactory(
                    Injection.proviceSectionRepository(),
                    Injection.provideUser(context)
                )
            }
    }
}