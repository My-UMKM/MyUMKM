package com.example.myumkm

import android.content.Context
import com.example.myumkm.data.repository.implementation.AccountRepository
import com.example.myumkm.data.repository.implementation.ChatRepository
import com.example.myumkm.data.repository.implementation.SectionRepository
import com.example.myumkm.util.SharedPrefConstants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

object Injection {
    fun provideAccountRepository(context: Context): AccountRepository {
        val firebaseAuth = Firebase.auth
        val firebaseFirestore = Firebase.firestore
        val appPreferences = context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
        val gson = Gson()
        return AccountRepository.getInstance(firebaseAuth, firebaseFirestore, appPreferences, gson)
    }

    fun proviceSectionRepository(): SectionRepository {
        return SectionRepository.getInstance(Firebase.firestore)
    }

    fun provideChatRepository(): ChatRepository {
        return ChatRepository.getInstance(Firebase.firestore)
    }
}