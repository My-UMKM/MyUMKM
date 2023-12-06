package com.example.myumkm.data.repository.implementation

import android.content.SharedPreferences
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.data.repository.interf.IAccountRepository
import com.example.myumkm.util.ResultState
import com.example.myumkm.util.SharedPrefConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AccountRepository(
    private val firebaseAuth: FirebaseAuth,
    val firebaseFirestore: FirebaseFirestore,
    val appPreferences: SharedPreferences,
    val gson: Gson
    ) : IAccountRepository {
    override fun registerUser(
        email: String,
        password: String,
        userEntity: UserEntity,
        result: (ResultState<String>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    userEntity.id = it.result.user?.uid ?: ""
                    updateUserInfo(userEntity) { state ->
                        when(state){
                            is ResultState.Success -> {
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null){
                                        result.invoke(ResultState.Error("User register successfully but session failed to store"))
                                    }else{
                                        result.invoke(
                                            ResultState.Success("User register successfully!")
                                        )
                                    }
                                }
                            }
                            is ResultState.Error -> {
                                result.invoke(ResultState.Error(state.error))
                            }
                            ResultState.Loading -> TODO()
                        }
                    }
                }else{
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(ResultState.Error("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(ResultState.Error("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(ResultState.Error("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(ResultState.Error(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    ResultState.Error(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateUserInfo(userEntity: UserEntity, result: (ResultState<String>) -> Unit) {
        val document = firebaseFirestore.collection(USER_COLLECTION).document(userEntity.id)
        document
            .set(userEntity)
            .addOnSuccessListener {
                result.invoke(
                    ResultState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    ResultState.Error(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun loginUser(email: String, password: String, result: (ResultState<String>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: ""){
                        if (it == null){
                            result.invoke(ResultState.Error("Failed to store local session"))
                        }else{
                            result.invoke(ResultState.Success("Login successfully!"))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(ResultState.Error("Authentication failed, Check email and password"))
            }
    }

    override fun forgotPassword(email: String, result: (ResultState<String>) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(ResultState.Success("Email has been sent"))

                } else {
                    result.invoke(ResultState.Error(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(ResultState.Error("Authentication failed, Check email"))
            }
    }

    override fun logout(result: () -> Unit) {
        firebaseAuth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,null).apply()
        result.invoke()

    }

    override fun storeSession(id: String, result: (UserEntity?) -> Unit) {
        firebaseFirestore.collection(USER_COLLECTION).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val user = it.result.toObject(UserEntity::class.java)
                    appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(user)).apply()
                    result.invoke(user)
                }else{
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (UserEntity?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (userStr == null){
            result.invoke(null)
        }else{
            val user = gson.fromJson(userStr,UserEntity::class.java)
            result.invoke(user)
        }
    }

    companion object {
        private const val USER_COLLECTION = "users"

        @Volatile
        private var instance: AccountRepository? = null

        fun getInstance(
            firebaseAuth: FirebaseAuth,
            firebaseFirestore: FirebaseFirestore,
            appPreferences: SharedPreferences,
            gson: Gson

        ): AccountRepository =
            instance ?: synchronized(this) {
                instance ?: AccountRepository(firebaseAuth, firebaseFirestore, appPreferences, gson)
            }
    }
}