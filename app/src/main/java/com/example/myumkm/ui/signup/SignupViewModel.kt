package com.example.myumkm.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.repository.implementation.AccountRepository
import com.example.myumkm.util.ResultState

class SignupViewModel(private val accountRepository: AccountRepository): ViewModel() {
    private val _register = MutableLiveData<ResultState<String>>()
    val register: LiveData<ResultState<String>>
        get() = _register

    fun register(
        email: String,
        password: String,
        userEntity: UserEntity
    ) {
        _register.value = ResultState.Loading
        accountRepository.registerUser(email, password, userEntity) { _register.value = it }
    }
}