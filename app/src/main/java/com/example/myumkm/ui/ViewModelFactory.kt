package com.example.myumkm.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.Injection
import com.example.myumkm.data.repository.implementation.AccountRepository
import com.example.myumkm.ui.login.LoginViewModel
import com.example.myumkm.ui.main.MainViewModel
import com.example.myumkm.ui.signup.SignupViewModel

class ViewModelFactory private constructor(private val accountRepository: AccountRepository) :
    ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(accountRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(accountRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(accountRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    companion object {
        fun getInstance(context: Context): ViewModelFactory =
            synchronized(this) {
                ViewModelFactory(
                    Injection.provideAccountRepository(context),
                )
            }
    }
}
