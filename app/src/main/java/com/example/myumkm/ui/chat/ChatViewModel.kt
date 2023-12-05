package com.example.myumkm.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.data.repository.interf.IAccountRepository
import com.example.myumkm.data.repository.interf.IChatRepository
import com.example.myumkm.data.repository.interf.ISectionRepository
import com.example.myumkm.util.ResultState
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatViewModel(private val iChatRepository: IChatRepository, private val iAccountRepository: IAccountRepository, private val iSectionRepository: ISectionRepository, sectionIdParam: String?) : ViewModel() {
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
    val chatsOptions: FirestoreRecyclerOptions<ChatEntity> = FirestoreRecyclerOptions.Builder<ChatEntity>()
        .setQuery(iChatRepository.getChats(sectionId.value), ChatEntity::class.java)
        .build()

    fun insertChat(chatEntity: ChatEntity) {
        _insertChat.value = ResultState.Loading
        iChatRepository.insertChat(chatEntity) { _insertChat.value = it }
    }

    fun updateChat(chatEntity: ChatEntity) {
        _updateChat.value = ResultState.Loading
        iChatRepository.updateChat(chatEntity) { _updateChat.value = it }
    }

    fun insertSection(sectionEntity: SectionEntity) {
        _insertSection.value = ResultState.Loading
        iSectionRepository.insertSection(sectionEntity) { _insertSection.value = it }
    }

    var userId: String? = null

    init {
        iAccountRepository.getSession { userEntity ->
            userId = userEntity?.id
        }
    }
}