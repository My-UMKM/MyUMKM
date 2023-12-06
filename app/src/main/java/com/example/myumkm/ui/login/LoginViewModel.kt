package com.example.myumkm.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.repository.implementation.AccountRepository
import com.example.myumkm.util.ResultState

class LoginViewModel(private val accountRepository: AccountRepository): ViewModel() {
    private val _login = MutableLiveData<ResultState<String>>()
    val login: LiveData<ResultState<String>>
        get() = _login

    fun login(email: String, password: String) {
        _login.value = ResultState.Loading
        accountRepository.loginUser(email, password) { _login.value = it }
    }

    fun getSession(result: (UserEntity?) -> Unit){
        accountRepository.getSession(result)
    }
}