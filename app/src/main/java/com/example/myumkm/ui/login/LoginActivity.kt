package com.example.myumkm.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.R
import com.example.myumkm.databinding.ActivityLoginBinding
import com.example.myumkm.ui.ViewModelFactory
import com.example.myumkm.ui.main.MainActivity
import com.example.myumkm.ui.section.SectionActivity
import com.example.myumkm.ui.signup.SignupActivity
import com.example.myumkm.util.ResultState
import com.example.myumkm.util.isValidEmail
import com.example.myumkm.util.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[LoginViewModel::class.java]

        setGooglePlusButtonText(binding.signInButton, getString(R.string.google))


        auth = FirebaseAuth.getInstance()

        viewModel.login.observe(this) { state ->
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
                    updateUI(auth.currentUser)
                }
            }
        }

        binding.loginButton.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )
            }
        }

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        binding.signInButton.setOnClickListener {
            signIn()
        }


        binding.signupAccount.setOnClickListener{
            val intentSignup = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intentSignup)
        }
    }

    private fun showLoading(isLoading: Boolean?) {
        binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
    }


    private fun setGooglePlusButtonText(signInButton: SignInButton, buttonText: String?) {
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)
            if (v is TextView) {
                v.text = buttonText
                return
            }
        }
    }

    private fun signIn() {
        showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun validation(): Boolean {
        var isValid = true
        if(binding.emailEditText.text.isNullOrEmpty()) {
            isValid = false
            toast("Please input the email")
        } else {
            if (!binding.emailEditText.text.toString().isValidEmail()){
                isValid = false
                toast("Email invalid")
            }
        }
        if (binding.passwordEditText.text.isNullOrEmpty()){
            isValid = false
            toast("Please input the password")
        }else{
            if (binding.passwordEditText.text.toString().length < 8){
                isValid = false
                toast("Password length minim 8 characters")
            }
        }
        return isValid
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}