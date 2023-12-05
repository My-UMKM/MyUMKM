package com.example.myumkm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myumkm.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    showLoading(true)
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        showLoading(false)
                        if (it.isSuccessful){
                            Log.d("SignupActivity", "Pendaftaran berhasil")
                            val intentA = Intent(this@SignupActivity, MainActivity::class.java)
                            startActivity(intentA)
                            finish()
                        }
                        else {
                            Log.e("SignupActivity", "Message: ${it.exception}")
                            Toast.makeText(this@SignupActivity, "Terjadi kesalahan saat mendaftar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }  else {
                    Toast.makeText(this@SignupActivity, "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean?) {
        binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
    }
}