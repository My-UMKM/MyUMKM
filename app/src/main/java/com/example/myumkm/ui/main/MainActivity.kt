package com.example.myumkm.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.ViewModelProvider
import com.example.myumkm.databinding.ActivityMainBinding
import com.example.myumkm.ui.ViewModelFactory
import com.example.myumkm.ui.faq.FaqActivity
import com.example.myumkm.ui.legal.LegalActivity
import com.example.myumkm.ui.login.LoginActivity
import com.example.myumkm.ui.money.MoneyActivity
import com.example.myumkm.ui.productquality.ProductQualityActivity
import com.example.myumkm.ui.section.SectionActivity
import com.example.myumkm.util.toast
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader

@ExperimentalGetImage class MainActivity : AppCompatActivity() {
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
        binding.btnFaq.setOnClickListener {
            startActivity(Intent(this@MainActivity, FaqActivity::class.java))
        }
        binding.btnLegalitas.setOnClickListener {
            startActivity(Intent(this@MainActivity, LegalActivity::class.java))
        }
        binding.btnQuality.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProductQualityActivity::class.java))
        }
        binding.btnScanner.setOnClickListener {
            startActivity(Intent(this@MainActivity, MoneyActivity::class.java))
        }

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel("Product-Image-Classification", DownloadType.LOCAL_MODEL, conditions)

        FirebaseModelDownloader.getInstance()
            .getModel("Money-Image-Classification", DownloadType.LOCAL_MODEL, conditions)

        FirebaseModelDownloader.getInstance()
            .getModel("Product-Quality-Classification", DownloadType.LOCAL_MODEL, conditions)
    }
}