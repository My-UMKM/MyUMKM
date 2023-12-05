package com.example.myumkm.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.Injection
import com.example.myumkm.data.repository.implementation.AccountRepository
import com.example.myumkm.data.repository.implementation.ChatRepository
import com.example.myumkm.data.repository.implementation.SectionRepository

class ChatViewModelFactory private constructor(private val chatRepository: ChatRepository, private val accountRepository: AccountRepository, private val sectionRepository: SectionRepository, private val sectionId: String?) :
    ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(chatRepository, accountRepository, sectionRepository, sectionId) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    companion object {
        fun getInstance(context: Context, sectionId: String?): ChatViewModelFactory =
            synchronized(this) {
                ChatViewModelFactory(
                    Injection.provideChatRepository(),
                    Injection.provideAccountRepository(context),
                    Injection.proviceSectionRepository(),
                    sectionId
                )
            }
    }
}