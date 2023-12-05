package com.example.myumkm.ui.section

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myumkm.R
import com.example.myumkm.databinding.ActivitySectionBinding
import com.example.myumkm.ui.chat.ChatActivity

class SectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySectionBinding
    private lateinit var adapter: SectionAdapter
    private lateinit var viewModel: SectionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        viewModel = ViewModelProvider(this, SectionViewModelFactory.getInstance(this))[SectionViewModel::class.java]
        adapter = SectionAdapter(viewModel.sectionsOptions)
        binding.rvSection.layoutManager = LinearLayoutManager(this)
        binding.rvSection.adapter = adapter

        binding.itemSection.imgBtnSection.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val SECTION = "section"
    }
}