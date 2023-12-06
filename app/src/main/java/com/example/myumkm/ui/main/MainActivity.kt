package com.example.myumkm.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.databinding.ActivityMainBinding
import com.example.myumkm.ui.ViewModelFactory
import com.example.myumkm.ui.login.LoginActivity
import com.example.myumkm.ui.section.SectionActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]

        viewModel.user.observe(this){
            if(it == null) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        setupView()
    }

    private fun setupView() {
        val user = viewModel.user.value
        if (user == null) {
            // Perform action when userEntity is null
        } else {
            binding.tvName.text = user.name
        }

        binding.btnChatbot.setOnClickListener {
            startActivity(Intent(this@MainActivity, SectionActivity::class.java))
        }
        binding.fabLogout.setOnClickListener {
            viewModel.logout()
        }
    }

}