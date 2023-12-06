package com.example.myumkm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.repository.implementation.AccountRepository

class MainViewModel(private val accountRepository: AccountRepository): ViewModel() {
    private val _user = MutableLiveData<UserEntity>()
    val user: LiveData<UserEntity>
        get() = _user

    fun getSession() {
        accountRepository.getSession { _user.value = it }
    }

    init {
        getSession()
    }
}