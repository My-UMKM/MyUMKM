package com.example.myumkm.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.remote.response.ChatResponse
import com.example.myumkm.data.repository.interf.IAccountRepository
import com.example.myumkm.data.repository.interf.IChatRepository
import com.example.myumkm.data.repository.interf.ISectionRepository
import com.example.myumkm.util.ResultState
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatViewModel(val iChatRepository: IChatRepository, private val iSectionRepository: ISectionRepository,
                    val user: UserEntity,private var sectionIdParam: String?) : ViewModel() {
    private val _insertChat = MutableLiveData<ResultState<String>>()
    val insertChat: LiveData<ResultState<String>>
        get() = _insertChat

    private val _updateChat = MutableLiveData<ResultState<String>>()
    val updateChat: LiveData<ResultState<String>>
        get() = _updateChat

    private val _insertSection = MutableLiveData<ResultState<String>>()
    val insertSection: LiveData<ResultState<String>>
        get() = _insertSection

    val sectionId: MutableLiveData<String> = MutableLiveData(sectionIdParam ?: "")
    val chatsOptions: MutableLiveData<FirestoreRecyclerOptions<ChatEntity>> = MutableLiveData()

    init {
        sectionId.observeForever { id ->
            val options = FirestoreRecyclerOptions.Builder<ChatEntity>()
                .setQuery(iChatRepository.getChats(id), ChatEntity::class.java)
                .build()
            chatsOptions.value = options
        }
    }

    fun insertChat(chatUser: ChatEntity) {
        _insertChat.value = ResultState.Loading
        iChatRepository.insertChat(chatUser, user.id) { result ->
            sectionIdParam = result.toString()
            sectionId.value = result.toString()
            _insertChat.value = ResultState.Success(result.toString())
        }
    }

    fun updateChat(chatEntity: ChatEntity) {
        _updateChat.value = ResultState.Loading
        iChatRepository.updateChat(chatEntity) { _updateChat.value = it }
    }

    fun insertSection(sectionEntity: SectionEntity) {
        _insertSection.value = ResultState.Loading
        iSectionRepository.insertSection(sectionEntity) { _insertSection.value = it }
    }
}