package com.example.myumkm.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.data.entity.UserEntity
import com.example.myumkm.ui.main.MainActivity
import com.example.myumkm.databinding.ActivitySignupBinding
import com.example.myumkm.ui.ViewModelFactory
import com.example.myumkm.util.ResultState
import com.example.myumkm.util.isValidEmail
import com.example.myumkm.util.toast

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[SignupViewModel::class.java]

        viewModel.register.observe(this) { state ->
            when(state){
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    toast(state.error)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    toast(state.data)
                    startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        binding.signupButton.setOnClickListener {
            if (validation()) {
                viewModel.register(
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString(),
                    getUserEntity()
                )
            }
        }
    }

    fun getUserEntity(): UserEntity {
        toast("name : ${binding.nameEditText.text.toString()}")
        return UserEntity(
            id="",
            name = binding.nameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
        )
    }

    fun validation(): Boolean {
        return when {
            areFieldsEmpty() -> {
                toast("Fill all the columns")
                false
            }
            isEmailInvalid() -> {
                toast("Email invalid")
                false
            }
            arePasswordsNotMatching() -> {
                toast("Password and Confirm Password do not match")
                false
            }
            else -> true
        }
    }

    fun areFieldsEmpty(): Boolean {
        return binding.nameEditText.text.isNullOrEmpty() ||
                binding.emailEditText.text.isNullOrEmpty() ||
                binding.passwordEditText.text.isNullOrEmpty() ||
                binding.confirmPasswordEditText.text.isNullOrEmpty()
    }

    fun isEmailInvalid(): Boolean {
        return !binding.emailEditText.text.toString().isValidEmail()
    }

    fun arePasswordsNotMatching(): Boolean {
        return binding.passwordEditText.text.toString() != binding.confirmPasswordEditText.text.toString()
    }

    private fun showLoading(isLoading: Boolean?) {
        binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
    }
}