package com.example.myumkm.data.repository.interf

import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.util.ResultState

interface IAccountRepository {
    fun registerUser(email: String, password: String, userEntity: UserEntity, result: (ResultState<String>) -> Unit)
    fun updateUserInfo(userEntity: UserEntity, result: (ResultState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (ResultState<String>) -> Unit)
    fun forgotPassword(email: String, result: (ResultState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (UserEntity?) -> Unit)
    fun getSession(result: (UserEntity?) -> Unit)
}